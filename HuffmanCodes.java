
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.*;
import java.io.*;
import org.instructures.*;

public class HuffmanCodes {
	
    
    public static boolean showFreq=false;
    public static boolean showCodes=false;
    public static boolean showBinary=false;
       private final Option ENCODE, DECODE;
  private final Option SHOW_FREQUENCY, SHOW_CODES, SHOW_BINARY;
  private final ArgsParser parser;
  private final Operand<File> IN, OUT;

  private ArgsParser.Bindings settings;

  public HuffmanCodes() {    
    this.ENCODE = Option.create("-e, --encode").summary("Encodes IN to OUT");
    this.DECODE = Option.create("-d, --decode").summary("Decodes IN to OUT");
    this.SHOW_FREQUENCY = Option.create("--show-frequency")
      .associatedWith(ENCODE).summary("Output byte frequencies");
    this.SHOW_CODES = Option.create("--show-codes")
      .summary("Output the code for each byte");
    this.SHOW_BINARY = Option.create("--show-binary").associatedWith(ENCODE)
      .summary("Output a base-two representation of the encoded sequence");

    this.parser = ArgsParser.create("java HuffmanCodes")
      .summary("Encodes and decodes files using Huffman's technique")
      .helpFlags("-h, --help");
    this.IN = Operand.create(File.class, "IN");
    this.OUT = Operand.create(File.class, "OUT");
    parser.requireOneOf("mode required", ENCODE, DECODE)
      .optional(SHOW_FREQUENCY).optional(SHOW_CODES).optional(SHOW_BINARY)
      .requiredOperand(IN).requiredOperand(OUT);
  }

  public static void main(String[] args) {
    HuffmanCodes app = new HuffmanCodes();
    app.start(args);
  }

  public void start(String[] args) {
    settings = parser.parse(args);
    try (BitInputStream in = new BitInputStream(settings.getOperand(IN));
         BitOutputStream out = new BitOutputStream(settings.getOperand(OUT))) {
      if (settings.hasOption(ENCODE)) {
        encode(in, out);
      } else {
        decode(in, out);
      }
    } catch (Exception e) {
      System.err.printf("Error: %s%n", e.getMessage());
      System.exit(1);
    }
  }
	abstract class Node implements Comparable<Node>{
		final protected int count;
		protected Node(int count){
		this.count=count;
		}
		
		public int compareTo(Node a){
			return Integer.compare(this.count,a.count);
		}
	 
		public final Map<Byte, String> getAllCodes(){
			Map<Byte, String> codeTable = new HashMap<>();
			this.putCodes(codeTable, "");
			return codeTable;
		}
		
     	protected abstract void putCodes(Map<Byte, String> table, String bits);	
       public abstract void writeTo(BitOutputStream out)throws IOException;
      	public abstract byte next(BitInputStream in)throws IOException;
	}
		 class DecisionNode extends Node{
			Node left;
			Node right;
			
	     	DecisionNode(Node left, Node right){
                        super(left.count + right.count);
			this.left=left;
			this.right=right;
				
	       }
	       protected void putCodes(Map<Byte, String> table, String bits){
		        left.putCodes(table, bits + "0");
			right.putCodes(table, bits + "1");
				 
				 } 
	       public void writeTo(BitOutputStream out)throws IOException{
			 out.writeBit(0);
			 left.writeTo(out);
			 right.writeTo(out);
		}
			 
	      	 public byte next (BitInputStream in) throws IOException{
		  Node nextone = (in.readBit() == 0) ? left : right;
		    return nextone.next(in);
		  }
	       }
			
		
		
        	 class ValueNode extends Node{
			final byte value;
			
		       ValueNode(byte value, int count){
		       super(count);
                       this.value =value;			
				
			}
	    	protected void putCodes(Map<Byte, String> table, String bits){
		        table.put(value, bits);
		 }
	       	public void writeTo(BitOutputStream out) throws IOException{
			      out.writeBit(1);
			      out.writeByte(value);
	      	}
			public byte next(BitInputStream in) throws IOException{
				return value;
			}
					
		}
		
      	///////////////////////////////getFrequency///////////////////////////
		public Map<Byte, Integer> countFrequencies(byte[] data) { 
			Map<Byte, Integer> frequencies = new HashMap<>(); 
				for(int i = 0; i < data.length; ++i) { 
				    Integer count = frequencies.get(data[i]); 
				    if(count == null) { 
				      count = 0; 
				    } 
				    frequencies.put(data[i], count + 1); 
				  } 
			
	                 return frequencies; 
		}
		
 ////////////create the original forest and put frequencies in order///////
       	 private PriorityQueue<Node> buildForest(Map<Byte, Integer> frequencies) { 
			  PriorityQueue<Node> forest; 
	         forest = new PriorityQueue<Node>(frequencies.size()); 
			    for(Map.Entry<Byte, Integer> entry:
		  frequencies.entrySet()) { 
        Node newNode = new ValueNode(entry.getKey(), entry.getValue());
			      forest.add(newNode); 
			 } 
			return forest; 
			}
		
		
///////////////////////////////////////setBits and find the root////////////////
		
	
		private Node buildTree(Map<Byte, Integer> freq) { 
			PriorityQueue<Node> forest = buildForest(freq);
			 
			 while (forest.size() > 1) {
			 Node left = forest.remove();
			 Node right = forest.remove();
			 forest.add(new DecisionNode(left, right));
			 }
			 return forest.remove(); 
			
			
			
		}
		
/////////////////////////////recoverCodingTree//////////////////////////////////
		
		private Node recoverCodingTree(BitInputStream in)throws IOException{
			int tag = in.readBit();
			if(tag == 1){
			  byte value = (byte) in.readByte();
			  return new ValueNode(value, -1);
			}
			else{
			  Node left = recoverCodingTree(in);
			  Node right = recoverCodingTree(in);
			  return new DecisionNode(left, right);
			  }
			}
		 
//////////////////////////////////////encode////////////////////////////////////
		
  public void encode(BitInputStream in, BitOutputStream out)throws IOException{
			
					
	       	byte[] data = in.allBytes();
	       final Map<Byte, Integer> frequencies = countFrequencies(data);
	      
             if (settings.hasOption(SHOW_FREQUENCY)) {
      
    
	       System.out.println("FREQUENCY TABLE"); 
               
               List<Byte> ordering1 = new ArrayList<Byte>(frequencies.keySet());
                   Collections.sort(ordering1);
                   Collections.sort(ordering1, new Comparator<Byte>() {
                   public int compare(Byte a, Byte b) {
              return Integer.compare(frequencies.get(a), frequencies.get(b));
                 }
               });

		  
		    
				
       for (byte value: ordering1) {
	    int count = frequencies.get(value);
             System.out.printf("%s: %s%n", looksFormal(value), count);
         }             
			 //	 printFrequencies
	   }
	      Node tree = buildTree(frequencies);
		    
	      int inputLength = data.length;
	      out.writeInt(inputLength);
	      tree.writeTo(out);
	      int headerBits = out.tally();

       Map<Byte, String> codeTable = tree.getAllCodes();
       
              if (settings.hasOption(SHOW_CODES)) {
		System.out.println("CODES");
     List<Byte> ordering2 = new ArrayList<Byte>(codeTable.keySet());
    Collections.sort(ordering2, new Comparator<Byte>() {
        public int compare(Byte a, Byte b) {
          String codeA = codeTable.get(a);
          String codeB = codeTable.get(b);
          int lengthFirst = Integer.compare(codeA.length(), codeB.length());
          if (lengthFirst == 0) {
            return codeA.compareTo(codeB);
          }
          return lengthFirst;
        }
      });
    
		    
		 
    for (Byte value: ordering2) {
      String code = codeTable.get(value);
      System.out.printf("\"%s\" -> %s%n", code, looksFormal(value));
      }
    //printCodeTable
    }

	  for (int i = 0; i < data.length; ++i) {
	      String code = codeTable.get(data[i]);
	      for (int bitIndex = 0; bitIndex < code.length(); ++bitIndex) {
		        out.writeBit((code.charAt(bitIndex) == '1') ? 1 : 0);
	       }
	   }
	        if (settings.hasOption(SHOW_BINARY)) {
	      	for (int i = 0; i < data.length; ++i) {
                 String code = codeTable.get(data[i]);
                 System.out.print(code);
                }
	       System.out.println();
	       // printEncodedSequence
	    }
		   

		    int encodingBits = out.tally() - headerBits;
		    int outputLength = out.bytesNeeded();
		    double saved = outputLength / (double)inputLength;
	 System.out.printf(" input: %d bytes [%d bits]%n",
		                      inputLength, inputLength * 8);
    System.out.printf("output: %d bytes [header: %d bits; encoding: %d bits]%n",
		                      outputLength, headerBits, encodingBits);
	    System.out.printf("output/input size: %.4f%%%n", saved * 100.0);
			
		}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		
		
 //////////////////////////////////////decode///////////////////////////////////
		
 private void decode(BitInputStream in, BitOutputStream out)throws IOException{
			
	       	int length1 = in.readInt();
	       	Node root1 = recoverCodingTree(in);
	       	Map<Byte, String> codeTable = root1.getAllCodes();
			
			
	       	
	   if(showCodes==true){
                            //printCodeTable
		System.out.println("CODES");
                List<Byte> ordering = new ArrayList<Byte>(codeTable.keySet());
                Collections.sort(ordering, new Comparator<Byte>() {
                public int compare(Byte a, Byte b) {
                String codeA = codeTable.get(a);
                String codeB = codeTable.get(b);
              int lengthFirst = Integer.compare(codeA.length(), codeB.length());
                    if (lengthFirst == 0) {
                        return codeA.compareTo(codeB);
                     }
          return lengthFirst;
                   }
                  });
              for (Byte value: ordering) {
                  String code = codeTable.get(value);
                  System.out.printf("\"%s\" -> %s%n", code, looksFormal(value));
              }
	    }
				    
			
		 System.out.printf("original size: %d%n", length1);
		       for (int i = 0; i < length1; ++i) {
		       byte value = root1.next(in);
			out.writeByte(value);
	       }
			
			
		}
		
 
    ////////////////////////////////////////////////////////////////////////////
    private static final Map<String, String> dressing = new HashMap<>();
  static {
    dressing.put("\n", "\'\\n\'");
    dressing.put("\r", "\'\\r\'");
    dressing.put("\r", "\'\\r\'");
    dressing.put("\\", "\'\\\\\'");
    dressing.put("\'", "\'\\\'\'");
  }
    ////////////////////////////////////////////////////////////////////////////
 private static String looksFormal(byte a) {
    String convert = new String(new byte[] {a});
    String form = dressing.get(convert);
    if (form == null) {
      form = String.format("\'%s\'", convert);
    }
    return form;
  }
 	
		
		

 
	      
	        
	      
	      
}// very outer class
		
