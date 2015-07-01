import java.util.ArrayList;
import java.lang.Math;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.entropy_bench.utility.CodeBlock;
import com.entropy_bench.utility.Edge;
import com.entropy_bench.utility.EdgeProbability;
import com.entropy_bench.utility.cfgHelper;
import com.entropy_bench.utility.pathProbal;


public class CalcuEntropy {

	public static List<List<Integer>> path = new ArrayList();
	public static List<List<Integer>> loop = new ArrayList();
	
	// the path is all possibile  for three level.
	// [+]all path- 3 level
	//	( <path><loop><loop>)(<path><loop><loop>)
	public static List<List<List<Integer>>> all_path = new ArrayList();
	//[]+
	// (<edge,edge>,<edge,edge>)
	public static List<List<List<Edge>>> all_keyedge = new ArrayList();
	
	//( <edge-path, probability><edge-path, probability> )
	public static List<pathProbal> all_path_probal = new ArrayList();
	
	//set the max loop numbers, the larger means the possible path will be more
	public static int maxLoop = 5;
	
	public static List<Double> pro_path = new ArrayList();
	public static List<Double> pro_loop = new ArrayList();
	
	public static final int Keyderive[] = EdgeProbability.Arith_50max_KeyDeriveBlock;
	public static final int KeyBlock[][] = EdgeProbability.Arith_50max_keyBlock;
	public static final int Cumu[][] = EdgeProbability.Arith_50max_Cumu;
	public static final double edgeProba[][] = EdgeProbability.Arith_50max_edgeProba;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new cfgHelper();
		
		//@@identify the direct path and loop
		pathIdentify();
		//cfgHelper.writeintoFile(path, "D:\\GitProject\\path-profile\\entropy_bench\\JGF\\path.txt");
		//cfgHelper.writeintoFile(loop, "D:\\GitProject\\path-profile\\entropy_bench\\JGF\\loop.txt");
		
		//@@combine the path and loop.
		getAllPossPath();
		
		//@@ identify the key edge
		keyEdgeIdentify();
		
		//@@ replace the low probability path fragment with the high probability loop .
//		replcePathfragWithloop();
		
		replcacePathWithHighPro();
		
		cfgHelper.writeEdge(all_keyedge,"D:\\GitProject\\path-profile\\entropy_bench\\JGF\\edge.txt");
		//@@calcualte each path probability
		calcuPossPathPro();
//		
		
		
		
		

		
	}
	
	public static void test()
	{
//	int in  = indexf(Keyderive, 67);
//	System.out.println(in + "," + KeyBlock[in][0] + "," + edgeProba[in][0]);
	
	
	List<List<Integer>> llist = new ArrayList();
	
	List<Integer> list=new ArrayList<Integer>();  
	   
	 list.add(1);  
	 list.add(2);  
	 list.add(4);  
	 list.add(1);  
	 list.add(2);  
	 list.add(5);  
	 list.add(1); 
	 list.add(24); 
	 list.add(9); 
	 list.add(11); 
	 list.add(5); 
	 llist.add(list);
	 List<Integer> list2 = new ArrayList();
	 List<Integer> list3 = new ArrayList();

	 copy_list(list2, list.subList(3, 7));
	 copy_list(list3, list);
	 llist.add(list2);
	 llist.add(list3);
	 
	 
	 System.out.println("before:" + llist);
	 list3.remove(4);
	 //llist = new ArrayList(new LinkedHashSet(llist));
	 llist = new ArrayList(new LinkedHashSet(llist));
	System.out.println("af" + llist);
	}
	
	public static void calcuPossPathPro()
	{
		int i,j,k,l;
		
		double pro = 1;
		for(i=0 ; i < all_keyedge.size() ; i++)
		{
			List<List<Edge>> path_edge = all_keyedge.get(i);//get (<edge,edge><edge,edge> loop or path)
			pro = 1;// each from 1 start.
			//System.out.printf("%d:%d\n", i, path_edge.size());
			for(j=0 ; j< 1 ; j++)// move to calcu the only path
			{
				List<Edge> pathfrag_edge = path_edge.get(j);
				//System.out.printf(j + ":" + path_edge);

				for(k=0 ; k < pathfrag_edge.size() ; k++)
				{
					int indexOfKey = indexf(Keyderive,  pathfrag_edge.get(k).ori);
					int[] branchCB = KeyBlock[indexOfKey];
					double[] branchPro = edgeProba[indexOfKey];
					int whichBrach = indexf(branchCB, pathfrag_edge.get(k).dest);
					System.out.printf(" [%d]:[%d->%d][%f], ", indexOfKey,pathfrag_edge.get(k).ori, pathfrag_edge.get(k).dest,branchPro[whichBrach] );
					//System.out.printf("%d:%f\n", i, branchPro[whichBrach]);
					pro *= branchPro[whichBrach];					
				}
				System.out.println();
			}
			pathProbal pp = new pathProbal();
			//pp.path_edge = xx;
			pp.probability = pro;
			System.out.printf("%d:%.20f,%.20f\n", i, pro, Math.log(1/pro)/Math.log(2) * pro);
		}
	}
	
	public static void replcacePathWithHighPro()
	{
		int i,j,k;
		for(i=0 ; i< all_keyedge.size() ; i++)
		{
			List<List<Edge>> eachpathAndloop = all_keyedge.get(i); //get  (<edge,edge><edge,edge>)
			List<Edge> pathedge = eachpathAndloop.get(0);// get the edge of the path
			List<Integer> inToRemove = new ArrayList();// record the index of pathedge to be removed
			for(j=0 ; j< pathedge.size() ;j++)// traverse and replace the skip loop block with into loop block
			{
				Edge e = pathedge.get(j);
				for(k=1 ; k < eachpathAndloop.size() ; k++)
				{
					double loopfrag = getEdgePro(eachpathAndloop.get(k).get(0).ori, eachpathAndloop.get(k).get(0).dest);
					double pathfrag = getEdgePro(e.ori,e.dest);
					if( eachpathAndloop.get(k).get(0).ori == e.ori && loopfrag > pathfrag)//
					{						
						inToRemove.add(j);
						break;
					}
				}
			}
			//System.out.print("b" + inToRemove);
			for(j= inToRemove.size() -1; j > 0 ; j--)
			{
				int x = inToRemove.get(j);
				pathedge.remove(x);
			}
			//System.out.println(",a" + pathedge.size());
						
		}
	}
	
	public static double getEdgePro(int ori, int dest)
	{
		int indexOfKey = indexf(Keyderive,  ori);
		int[] branchCB = KeyBlock[indexOfKey];
		double[] branchPro = edgeProba[indexOfKey];
		int whichBrach = indexf(branchCB, dest);
		//System.out.printf(" [%d]:[%d->%d][%f], ", indexOfKey,pathfrag_edge.get(k).ori, pathfrag_edge.get(k).dest,branchPro[whichBrach] );
		//System.out.printf("%d:%f\n", i, branchPro[whichBrach]);
		return branchPro[whichBrach];				
		
	}
	
	
	public static void replcePathfragWithloop()
	{
		int i,j,k;
		for(i=0 ; i< all_keyedge.size() ; i++)
		{
			List<List<Edge>> eachpathAndloop = all_keyedge.get(i); //get  (<edge,edge><edge,edge>)
			List<Edge> pathedge = eachpathAndloop.get(0);// get the edge of the path
			List<Integer> inToRemove = new ArrayList();// record the index of pathedge to be removed
			for(j=0 ; j< pathedge.size() ;j++)// traverse and replace the skip loop block with into loop block
			{
				Edge e = pathedge.get(j);
				for(k=1 ; k < eachpathAndloop.size() ; k++)
				{
					if( eachpathAndloop.get(k).get(0).ori == e.ori )//
					{						
						inToRemove.add(j);
						break;
					}
				}
			}
			//System.out.print("b" + inToRemove);
			for(j= inToRemove.size() -1; j > 0 ; j--)
			{
				int x = inToRemove.get(j);
				pathedge.remove(x);
			}
			//System.out.println(",a" + pathedge.size());
			
			
		}
	}
	
	public static void keyEdgeIdentify()
	{	
		List<List<Edge>> eachpath_edge;
		List<Edge> subpath_edge = null;
		
		int i,j,k,l;
		for(i=0 ; i< all_path.size() ; i++)
		{
			List<List<Integer>> eachpathAndloop = all_path.get(i);//get each path (<path><loop><loop>)
			eachpath_edge = new ArrayList();// get  (<edge,edge><edge,edge>)
			for(j=0 ; j< eachpathAndloop.size() ; j++)
			{
				List<Integer> subPath = eachpathAndloop.get(j);
				subpath_edge = new ArrayList();//get (<edge,edge>)
				for(k=0; k< subPath.size() ; k++)
				{
					if(  indexf(Keyderive,subPath.get(k)) != -1 && k +1 < subPath.size())// if find the key block and has next block
					{
						//System.out.println("in");
						Edge e = new Edge();
						e.ori = subPath.get(k);
						e.dest = subPath.get(k+1);
						subpath_edge.add(e);
					}
				}
				eachpath_edge.add(subpath_edge);
			}
			//\System.out.println(eachpath_edge);
			all_keyedge.add(eachpath_edge);
		}
		
	}
	
	//search the num , in the array.
	public static int indexf(int[] input, int s)
	{
		int i;
		for(i=0 ; i<input.length ; i++)
		{
			if( input[i] == s )
				return i;
		}
		return -1;
	}
	
	
	public static void getAllPossPath()
	{
		int i,j,k,l,m;
		for(i=0 ; i< path.size() ; i++)// for each original path
		{
			List<List<Integer>> subPossPath = new ArrayList();
			List<Integer> oriPath = path.get(i);
			subPossPath.add(oriPath);// add the path self;
			for(j=0 ; j < oriPath.size(); j++)
			{
				addAllPossibleLoop(oriPath.get(j), subPossPath,true);
			}
			subPossPath = new ArrayList(new LinkedHashSet(subPossPath)); // del the duplicate loop
			all_path.add(subPossPath);// add the find all possible path
		}
		all_path = new ArrayList(new LinkedHashSet(all_path));// del the duplicate path
		//repeatTheLoop();
	}
	
	
	//repeat the loop times to combine the more every possible path
	public static void repeatTheLoop()
	{
		int i,j,k;
		List<Integer> newLoop = new ArrayList();
		for(i=0 ; i< all_path.size() ; i++)
		{
			List<List<Integer>> subPossPath = all_path.get(i);//get each path
			for(j=1 ; j< subPossPath.size() ; j++)
			{
				// to be done[++++]. while the original path number is 4096,which is full for List
			}
		}
	}
	
	
	public static void addAllPossibleLoop(int search, List<List<Integer>> psPath,boolean isGodown)
	{
		int i,j,k;
		for(i=0 ; i< loop.size() ; i++)//add all possibile loop
		{
			if(loop.get(i).get(0) == search)//if start from the loop
			{
				psPath.add(loop.get(i)); 
				if( isGodown)// for 2 iterator loop
				{
					for(j=0 ; j<loop.get(i).size() ; j++)
					{
						addAllPossibleLoop(loop.get(i).get(j), psPath,false);
					}
				}
			}
		}
	}
	
	
	//given a cfg, and identify all this loop path fragment.
	//And make the direct fragment and the loop fragment compose.	
	public static void pathIdentify()
	{
		List<Integer> trace = new ArrayList();
				
		dfs(trace, cfgHelper.codeblock.get(0));//start form the 0 index code block
		
		path = new ArrayList(new LinkedHashSet(path));
		loop = new ArrayList(new LinkedHashSet(loop));

		
	}
	
	public static void dfs(List<Integer> trace, CodeBlock cb)
	{
		if(trace.indexOf(cb.id) != -1)// if the code block has been traversed.
		{
			if( trace.get(trace.size()-1) > cb.id)// if it is the loop
			{
				//print_trace(trace,trace.indexOf(cb.id), trace.size()-1,"Loop");
				List<Integer> temp = new ArrayList() ;
				copy_list(temp, trace.subList(trace.indexOf(cb.id), trace.size()));
				loop.add(temp);
			}
			return;
		}
		
		trace.add(cb.id);
		List<Integer> outB = cb.outBlock;
		if(outB.size() < 1)// if the exit code block is reached
		{
			//print_trace(trace,0,trace.size()-1,"path-Trace");
			trace.remove(trace.size()-1);//del the code block just traversed
			List<Integer> temp = new ArrayList() ;
			copy_list(temp, trace);
			path.add(temp);//put the path trace into record for further calculate
			//System.out.println("[trace]" + trace + "\n" + temp + path.size() );

			return;
		}
		int i;
		//print_trace(trace,0,trace.size()-1,"[gotrace]" + cb.id);
		for(i=0 ; i < outB.size() ; i++)
		{
			//System.out.printf("[branch]:%d\n",outB.get(i));
			dfs(trace, cfgHelper.codeblock.get(outB.get(i)));
		}		
		trace.remove(trace.size()-1);//del the code block just traversed
		//print_trace(trace,0,trace.size()-1,"[backtrace]" + cb.id);

	}
	
	public static void copy_list(List<Integer> dest, List<Integer> src)
	{
		Iterator<Integer> it = src.iterator();
		while(it.hasNext())
		{
			dest.add(it.next());
		}
	}
	
	public static void print_trace(List<Integer> trace, int beg, int end, String str)
	{
		System.out.printf( str + ":");
		int i;
		for(i=beg ; i< end ; i++)
		{
			System.out.printf("%d,",trace.get(i));
		}
		System.out.println(trace.get(i));
	}

}
