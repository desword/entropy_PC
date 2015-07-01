package com.entropy_bench.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class cfgHelper {
	public static String[] path = {"D:\\GitProject\\path-profile\\entropy_bench\\JGF\\cfg_50\\Arith.txt"}; 
	
	public static List<CodeBlock> codeblock = new ArrayList();
	
	public cfgHelper()
	{
		readFileByLines(path[0]);
	}
	
	public void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	CodeBlock cb = new CodeBlock();
            	//System.out.println(tempString);
            	String[] inandout = tempString.split(",out");
            	String[] contentIO = {inandout[0].substring(3,inandout[0].length()-1),
            			inandout[1].substring(1,inandout[1].length()-1)};
            	String[] inB = contentIO[0].split(",");
            	String[] outB = contentIO[1].split(",");
            	//System.out.println(inB[0]+ "||"+ outB[1]);
            	int i;
            	for(i=0 ;i < inB.length ;i++)
            	{
            		if( inB[i].equals(""))
            			continue;
            		cb.inBlock.add(Integer.parseInt(inB[i]));
            	}
            	for(i=0 ; i< outB.length ; i++)
            	{
            		if( outB[i].equals(""))
            			continue;
            		cb.outBlock.add(Integer.parseInt(outB[i]));
            	}
            	cb.id = line-1;
            	codeblock.add(cb);
                line++;
            }
            //System.out.println("stop");
            
            
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
	
	public static void writeEdge(List<List<List<Edge>>> p, String path)
	{
		File file = new File(path);
        FileWriter fw = null;
        BufferedWriter writer = null;
        int i,j,k;
        try {
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            for(i=0  ;i< p.size() ; i++)
            {           	
            	
            	writer.write("edge:");
            	for(j=0 ; j< p.get(i).size(); j++)
            	{            		
            		for(k=0 ; k< p.get(i).get(j).size() ; k++)
            		{
            			Edge e = p.get(i).get(j).get(k);
            			String s = String.format( "[%d-->%d]",e.ori,e.dest);
            			writer.write(s);  
            		}          		
            		               
                	   
            	}
            	writer.newLine();//换行   
            }   
            writer.newLine();//换行   
                       
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                writer.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	public static void justWrite(List<List<List<Integer>>> p, String path)
	{
		File file = new File(path);
        FileWriter fw = null;
        BufferedWriter writer = null;
        int i,j;
        try {
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            
            writer.write(p.toString());  
            writer.newLine();//换行   
                       
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                writer.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	public static void writeintoFile(List<List<Integer>> p, String path)
	{
		File file = new File(path);
        FileWriter fw = null;
        BufferedWriter writer = null;
        int i,j;
        try {
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            for(i=0  ;i< p.size() ; i++)
            {           	
            	
            	writer.write("path:" + p.size());
//            	for(j=0 ; j< p.get(i).size(); j++)
//            	{            		
//            		writer.write(p.get(i).get(j) + ",");                 
//                	   
//            	}
            	writer.newLine();//换行   
            }           
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                writer.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
}
