import java.io.*;
import java.util.*;

public class Tree{

	int m; //the order of tree
	int n; //minimum number of required children/children 

	//for testing/printing purposes only 
	public Queue<Node> q = new LinkedList<Node>();

	public class Node{

		public boolean isleaf;
		public boolean isroot;

		//number of keys
		public int nokeys;

		//keys
		public double[] keys = new double[m];//number of keys could be max at m-1 but one extra is added as for padding
	

		//value node to handle large number of values for a same key
		public Vnode[] values = new Vnode[m];

		//children
		public Node[] children = new Node[m+1];//pointers for a node which an be max m for an m way b+ tree

		//pointers for doubly linked list
		public Node next;
		public Node previous;

		//parent
		public Node parent;

		Node(){

		}


		Node(boolean isleaf, boolean isroot){
			this.isleaf = isleaf;
			this.isroot = isroot;
		}

	}

	Node root;

	Tree()
	{

	}

	
	
	public void initialize(int m)
	{
		this.m = m;
		double d = m/2;
		n = (int)Math.floor(d);
		root = new Node(true,true);
	}

	

	//data structure for adding values associated with a same key
	public class Vnode{
		public String value;
		public Vnode next;

		Vnode(String value, Vnode next){
			this.value = value;
			this.next = next;
		}

	}

	//search operation is performed to check whether is already present or a new key value pair has to be added
	public Node search(double key, Node node){
		
		int x=0;
		
			while(x<node.nokeys)
			{
				if(key == node.keys[x])
				{

				//checks if it is leaf, if yes then return else check the right children
				if(node.isleaf)
					return node;
				else
					return search(key,node.children[x+1]);
				
			    }
			else if(key < node.keys[x])
			{
				
				if(node.children[x]==null && node.isleaf) 
					return node;
				else 
					return search(key,node.children[x]);

			}
			
			x++;
		}
		//if traversed whole tree and no key is equal found or less than 
		//then by now i would be qual to no. of keys
		if(node.children[x]==null && node.isleaf) return node;
		else return search(key, node.children[x]);

	}

	// inserts a new pair
	public void insertPair(double key, String value)
	{
	//we search and reach the leaf node to get either the node or where it should be inserted
	
		Node leaf = search(key,root);
		

	//check if key is present in leaf or not
		boolean present = false;
		int y=0;
		//for( a=0;a<leaf.nokeys;a++){
		  while(y<leaf.nokeys)
		  {
			if(key == leaf.keys[y])
			{
				present = true;
				 break;
			}
				y++;
			
		    }
		if(!present)
		{
			//if not already present, so insert key-value pair
			//if  there is no overflow, mergeIntoLeaf is done
			
			if(leaf.nokeys < m-1)
			{
				
				leafmerge(leaf,key,value);
			}
			else
			{

			//if there is overflow, leafmerge is done a
			// current leaf is updated and we merge smallest key of the right IntoParent
				
				leafmerge(leaf,key,value);// make a new leaf
				Node newLeaf = new Node(true,false);

				
				for(int i=n,j=0;i<m;i++,j++){          // we filled second half of data from leaf into new leaf
						
						newLeaf.keys[j] = leaf.keys[i];
						newLeaf.values[j] = leaf.values[i];

						//remove these very entries from the old leaf as well
						leaf.keys[i] = 0;
						leaf.values[i] = null;

						//number of keys
						leaf.nokeys = leaf.nokeys - 1;
						newLeaf.nokeys = newLeaf.nokeys + 1;
				        }
 
				// take care of old leaf children and the new leaf children for the linked list
				newLeaf.next = leaf.next;
				newLeaf.previous = leaf;
				leaf.next = newLeaf;

				parentmerge(leaf,newLeaf,newLeaf.keys[0]);


				}


			}	


		else if(present)
		{
		
			Vnode temp;
			temp = leaf.values[y];
			while(temp.next!=null)
			{
				temp = temp.next;
			}
			temp.next = new Vnode(value,null);

	        }
	    }

	    public void delete(double key)
	   {
		Node leaf = search(key,root);
		//check if key is present in leaf or not
		boolean present = false;
		int y=0;
		//for( a=0;a<leaf.nokeys;a++){
		  while(y<leaf.nokeys)
		  {
			if(key == leaf.keys[y])
			{
				 break;
			}
				y++;
			
		   }
			if(leaf.nokeys >= n+1)
			{
			leaf.keys[y] = 0;
			leaf.values[y] = null;
			}
			leaf.nokeys--;
		
	}


	//adds new ke value pair in correct spot in the leaf	
	public void leafmerge(Node leaf,double key,String value)
	{
	
		int q=0;

		while(q<leaf.nokeys)
		{
			if(key<leaf.keys[q])
				break;
			q++;
		}
		//q is the index of where the key should be inserted
		int whereInsert = q;

		for(q=leaf.nokeys;q>whereInsert;q--){
			//move everything one space ahead and an empty spot is created 
			leaf.keys[q] = leaf.keys[q-1];
			leaf.values[q] = leaf.values[q-1];


		}

		
		//so now insert into empty spot
		leaf.keys[whereInsert] = key;
		leaf.values[whereInsert] = new Vnode(value,null);
		leaf.values[whereInsert].next=null;

		leaf.nokeys= leaf.nokeys + 1;
	}

	//data structure for nonleaf split
	public class Splitret{
		public Node left;
		public Node right;
		public double skey;

		Splitret(Node left, Node right, double skey){
			this.left = left;
			this.right = right;
			this.skey = skey;
		}
	}

	//splitting of non-leaf elements
	public Splitret nonleafsplit(Node nonleaf,Node empty){

		//no. keys in nonleaf are more than m-1
		double skey = nonleaf.keys[n];

	
		int a=n;
		int b=0;
		while(a<m)
		{			
			empty.keys[b] = nonleaf.keys[a];
			nonleaf.keys[a] = 0;
			
			empty.children[b] = nonleaf.children[a+1];
			nonleaf.children[a+1].parent = empty;
			nonleaf.children[a+1] = null;

			// number of keys
			nonleaf.nokeys = nonleaf.nokeys - 1;
			empty.nokeys = empty.nokeys + 1;
			a++;
			b++;

		}

		// just removing the skey node from the now filled Empty node
		int i=1;
		while(i<empty.nokeys)
		{
			empty.keys[i-1] = empty.keys[i];
			empty.keys[i] = 0;
			i++;
		}
		empty.nokeys--;


		Splitret ret = new Splitret(nonleaf,empty,skey);	
		return ret;
	}

	//recursively merging into parent and moving upwards
	public void parentmerge(Node lefthalf,Node righthalf,double key)
	{
		//root case and other cases handled seperately
		
		if(!lefthalf.isroot)
		{
			int i=0;
			while(i<lefthalf.parent.nokeys)
			{
				if(key<lefthalf.parent.keys[i])
					break;
				i++;
			}

			//i is the index of where the key should be inserted
			int whereInsert = i;

			int l=lefthalf.parent.nokeys;
			while(l>whereInsert){
				//first move everything one space ahead
				//and then insert the new key value pair
				//where it is supposed to be inserted
				lefthalf.parent.keys[l] = lefthalf.parent.keys[l-1];
				//shifting the children appropriately
				lefthalf.parent.children[l+1] = lefthalf.parent.children[l];
				l--;
		       }

			//The above loop emptied up the spot where we are supposed to insert
			//so now insert
			lefthalf.parent.keys[whereInsert] = key;
			lefthalf.parent.children[whereInsert+1] = righthalf;
			righthalf.parent = lefthalf.parent;


			lefthalf.parent.nokeys = lefthalf.parent.nokeys + 1;

			

			// handlingoverflow condition
			if(lefthalf.parent.nokeys>m-1)
			{
				
				
				// we split and recurse
				Node parentsRightHalf = new Node(false,false);

				//nonleafsplit will split the non-leaf according to how B-trees do it,
				//and store the leftHalf in lefthalf.parent and right half in parentsRightHalf
				//and return the skey as well
			
				

				Splitret split = nonleafsplit(lefthalf.parent,parentsRightHalf);
			

				//recurse to merge in granddad
				parentmerge(split.left,split.right,split.skey);
			}

			
		}
		

		
		else
		{
			Node nroot = new Node(false,true);
			nroot.keys[0] = key; //as key is the leftmost key in righthalf
			nroot.nokeys = nroot.nokeys + 1;

			//updating children
			nroot.children[0] = lefthalf;
			nroot.children[1] = righthalf;

			lefthalf.parent = nroot;
			righthalf.parent = nroot;

			//we update the old root
			lefthalf.isroot = false;
			
			//updating the root 
			root = nroot;
		}





	}

	//on single saerch operation, a perfecly formatted sting is returned
	public String getSearch(double key, Node node){
		
		Node exp = search(key,node);
		
		boolean present = false;
		int a;
		Vector<String> res=new Vector<String>();
		a=0;
		while(a<exp.nokeys)
		{
			if(key == exp.keys[a])
			{
				present = true; 
				break;
			}
				a++;
			}
		
		if(present)
		{
			Vnode temp = exp.values[a];
			while(temp!=null){
				res.add(temp.value);
				temp = temp.next;
		        }
		}
		else if(!present)
		{
				res.add("Null"); 
			
		}
		
		 return printsearch(res);

	}
	public String printsearch(Vector<String> res)
	{
		String print= new String();
		int i;
		for(i=0;i<res.size()-1;i++){
			print=print+res.get(i);
			print=print + ",";
		}
		//to avod comma on last element, last element addes seperately
		print=print+res.get(i);
		return print;
		
	}

	// for the range search query, a perfecly formatted string is returned
	public String rangeSearch(double key1, double key2)
	{

		Node leaf = search(key1,root);
		Node temp = leaf;
		String result = new String();
		int a;
		while(temp!=null)
		{
			for( a=0;a<temp.nokeys;a++)
			{
				if(key1<=temp.keys[a]&&temp.keys[a]<=key2)
				{
					Vnode valueTemp = temp.values[a];
					while(valueTemp!=null)
					{
						result+=valueTemp.value+",";
						valueTemp = valueTemp.next;
					}
				}
				
			}
			temp =temp.next;
		}
		//removing the last comma
		if(result.isEmpty()) result="Null";
		else result = result.substring(0, result.length() - 1);

		return result;
		//we print the result
		

	}


	
}

	