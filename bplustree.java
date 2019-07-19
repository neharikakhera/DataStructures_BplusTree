import java.io.*;
import java.util.*;

public class bplustree{


	public static void main(String args[]) throws IOException{

			// input and output files and streams are initialized
			FileHandler fh = new FileHandler(args[0],"output_file.txt");

			Tree bpt;
			
			// implement something so that filePath can be 
			//out of directory as well
			//OR NOT
			ArrayList<String> sentences;
			sentences = fh.readInput();

			
			//object of bPlus Tree
			bpt = new Tree();
			

			for(int i=0;i<sentences.size();i++){

				//represents a line gotten from the input file
				String line = sentences.get(i);
				
				//tokentised the line based on the following delimiters
				StringTokenizer st = new StringTokenizer(line,"(,)");

				//operation is either Insert or Search
				String operation = st.nextToken();

				//parameter list will be used to provide inputs and
				//distinguish between different types of searches
				Vector<String> parameterList = new Vector<String>();


				while(st.hasMoreTokens()){
	            	parameterList.add(st.nextToken());
	   		     }

	   		     if(operation.equals("initialize"))
	   		     {
	   		     	bpt.initialize(Integer.parseInt(parameterList.get(0)));
	   		     }

	   		     else if(operation.equals("Insert")){
	   		     	
	   		     	//inserting key value pair into the tree for insert op
	   		     	bpt.insertPair(Double.parseDouble(parameterList.get(0)),parameterList.get(1));
	   		     	

	   		     }
	   		     else if(operation.equals("Delete"))
	   		     {
	   		     	bpt.delete(Double.parseDouble(parameterList.get(0)));
	   		     }
	   		     else if(operation.equals("Search")){

	   		     	if(parameterList.size()==1){

	   		     		String toWrite;
	   		     		toWrite = bpt.getSearch(Double.parseDouble(parameterList.get(0)),bpt.root);
	   		     			   		     		//writing the output of search on one key, to the file
	   		     		fh.writeOutput(toWrite);
	   		     	}
	   		     	else if(parameterList.size()==2){
	   		     		String toWrite;
	   		     		try{
	   		     		 toWrite = bpt.rangeSearch(Double.parseDouble(parameterList.get(0)),Double.parseDouble(parameterList.get(1)));
	   		     		 //writing the output of range search to the output file
	   		     		fh.writeOutput(toWrite);
	   		     		}catch(NullPointerException e){}

	   		     	}	
	   		     	else{
	   		     		//will be intered of there is a discrepency in the number of 
	   		     		//search parameters
	   		     		System.out.println("wrong number of parameters in search");
	   		     		// System.out.println(parameterList.size());
	   		     		// for(int s=0;s<parameterList.size();s++)
	   		     		// 	System.out.println(parameterList.get(s));
	   		     	}
	   		     }
				
				

			}
			
			fh.closeOutputBuffer();
			// System.out.println("LIST-++++++____++++");
			// bpt.printLL();
			


	}
}