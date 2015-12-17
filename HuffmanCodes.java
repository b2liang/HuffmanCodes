package practice;

import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;


public class HuffmanCodes {

	
public static ArrayList <ValueNode> storeNodes = new ArrayList<ValueNode>();
public static ArrayList <Map.Entry> list new ArrayList();
public static Map charAndbit = new HashMap<Character,String>();
	
	private static class Node {
		String bitsValue;
		Node right;
		Node left;
		byte value;
		private void setRight(Node node){};
		private void setLeft(Node node){};
		private String getBitsValue(){return bitsValue;};
		private void setBitsValue(String a){};
		
	}
	private static class DecisionNode extends Node{
		
		private DecisionNode(){};
		
		 void setRight(Node node){
			
		}
		 void setLeft(Node node){
			
		}
		
	}
	
	private static class ValueNode extends Node{
		char Value;
		String bitsValue;
		private ValueNode(char a){
			this.Value=a;
			bitsValue="";
			
		}
			 String getBitsValue(){
				return bitsValue;
			
		}	
			 void setBitsValue(String a){
				 this.bitsValue=a;
			 }
	}
	
	///////////////////////////////getFrequency/////////////////////////////////////////
	public Map<Byte, Integer> countFrequencies(byte [] data) { 
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
	
	///////////////////////////////create the original forest and put frequencies in order/////////////////
	public PriorityQueue<Node> buildForest(Map<Byte, Integer> frequencies) { 
		  PriorityQueue<Node> forest; 
         forest = new PriorityQueue<>(frequencies.size()); 
		    for(Map.Entry<Byte, Integer> entry:
		      frequencies.entrySet()) { 
		    
		      forest.add(new ValueNode(entry.getKey(), entry.getValue())); 
		 } 
		return forest; 
		}
	
	
	///////////////////////////////////////getBits//////////////////////////////////////////////////////////////////////////
	
	public static Queue<String> getBits(Queue<Map.Entry> que){
	
	char a = (Character)((Entry) que.remove()).getKey();
	char b = (Character)((Entry) que.remove()).getKey();
	
	ValueNode firstNode = new ValueNode(a);
	ValueNode secondNode = new ValueNode(b);
	DecisionNode decisionnode1 = new DecisionNode();
	decisionnode1.setRight(firstNode);
	
	
	firstNode.setBitsValue("1");

	decisionnode1.setLeft(secondNode);
	secondNode.setBitsValue("0"); 
	storeNodes.add(firstNode);
	storeNodes.add(secondNode);
	
	DecisionNode decisionnode = decisionnode1;
	
	
	while ((que.size())!=0){
       
        for(int i =0;i<storeNodes.size();i++){
        	storeNodes.get(i).setBitsValue("1"+storeNodes.get(i).getBitsValue());
        	
        }
		char c =  (Character)(((Entry) que.remove()).getKey());
		ValueNode newvaluenode = new ValueNode(c);
		newvaluenode.setBitsValue("0");
		storeNodes.add(newvaluenode);
		
		DecisionNode newdecisionnode = new DecisionNode();
		newdecisionnode.setRight(decisionnode);
		newdecisionnode.setLeft(newvaluenode);
		
		decisionnode=newdecisionnode;
	
	}
	Queue newStoreBits = new LinkedList<String>();

	
	for(int i =0;i<storeNodes.size();i++){
		String bitsValue = storeNodes.get(i).getBitsValue();
		newStoreBits.add(bitsValue);
	}
	
	return newStoreBits;
	
	}
	
	
	
////////////////////////////////////////////main///////////////////////////////////////////////////////////////////////////	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		if(args[0].equals("--help")){
			System.out.println(" -e, --encode               encodes IN to OUT");
			System.out.println(" -d, --decode               decodes IN to OUT");
			System.out.println("     --show-frequency       show the frequencies of each byte ");
			System.out.println("     --show-codes           show the codes for each byte");
			System.out.println("     --show-binary          show the encoded sequence in binary");
			System.out.println("-h, --help                 display this help and exit");
		
		}
		
		/////////////////////////////////
	        boolean showFreq=false;
		  for(int i=0;i<args.length;i++){
			  if(args[i].equals("--show-frequency")){
				  showFreq=true;
			  }
		  }
		  if(showFreq==true){
			  System.out.println("FREQUENCY TABLE");
			  for(int i=0;i<list.size();i++){
				  System.out.println(list.get(i).getKey()+": "+list.get(i).getValue());
			  }
		  }
		 ///////////////////////////////////////////
		     
		  
		  
		  
		  
	
	}
		
		/////////////////////////////////////////////////////////////////////////////////
		
 public static void encode(BitsInputFile in, BitsOutputFile out){
	 byte[] data = in.allBytes();
	 
	 
      Map frequency = new HashMap<Byte,Integer>();
      for(Byte everyByte : data){
    	  if(!frequency.containsKey(everyByte)){
    		  frequency.put(everyByte,1);
    	  }
    	  else{
    		  frequency.put(everyByte,(Integer)frequency.get(everyByte)+1);
    	  }
      }     //put every char and its number into the map
    
      
      Collections.sort( list , new Comparator() {
          public int compare( Object o1 , Object o2 )
          {
              
              Map.Entry e1 = (Map.Entry)o1 ;
              Map.Entry e2 = (Map.Entry)o2 ;
              Integer first = (Integer)e1.getValue();
              Integer second = (Integer)e2.getValue();
	 if(first==second){
	     return ((Byte)e1.getKey()).compareTo((Byte)e2.getKey());
	 }
              return second.compareTo( first );
          }
      });
      
      Queue<Map.Entry> charQue = new LinkedList<Map.Entry>();
      for (int i=list.size()-1;i>=0;i--){
    	  Map.Entry entry = (Entry) list.get(i);
    	  charQue.add(entry);     //put the entry of map into a queue where the least value one is in the first place.
      }
      
 
      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
      ArrayList charList2 = new ArrayList(charQue);
    
     
      
    // getBits getBits1 = new getBits(charQue);
     Queue<String> bitsValue = getBits(charQue);
     while(bitsValue.size()!= 0){
    	 
     }
    
    System.out.println(bitsValue);
    for(int i =0;i<charList2.size();i++){
    charAndbit.put(((Map.Entry)(charList2.get(i))).getKey(),bitsValue.remove());
    }
      
      System.out.print(charAndbit);
      
      
      
      
      
      
      
	}
	
	
	
}
