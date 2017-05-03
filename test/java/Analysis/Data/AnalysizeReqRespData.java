package Analysis.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class AnalysizeReqRespData {

	
	public static ArrayList<String> StringSplit(String targetData)
	{
		String newString ="";
		int fromIndex = 0;
		int endIndex = targetData.length();
		int DataLength=0;
		ArrayList<String> targetList = new ArrayList<String>();
		for(;;)
		{
			if(fromIndex >= targetData.length())
			{
				break;
			}
			if((targetData.indexOf("2e2e0401",fromIndex)==-1) && (targetData.indexOf("2e2e0201",fromIndex)==-1)&& (targetData.indexOf("2e2e8101",fromIndex)==-1))
    		{
    			break;
    		}
			while(true)
			{	//判断命令的种类
	    		if(targetData.indexOf("2e2e0401",fromIndex)==fromIndex)
	    		{
	        		DataLength = 20*2;
	        		break;
	    		}
	    		if(targetData.indexOf("2e2e0201",fromIndex)==fromIndex)
	    		{
	         		DataLength = 20*2;
	        		break;
	    		}
	    		if(targetData.indexOf("2e2e8101",fromIndex)==fromIndex)
	    		{
	        		DataLength = 31*2;
	        		break;
	    		}else
	    		{
	    			break;
	    		}
			}
			if(DataLength!=0)
			{
				newString = targetData.substring(fromIndex,fromIndex + ((endIndex-fromIndex<DataLength)?endIndex-fromIndex:DataLength)); 
	    		fromIndex =fromIndex + DataLength;
	    		targetList.add(newString);
	    		//System.out.println("new string" + newString);
			}
    		
			
		}
		return targetList;
	}
	
	 public static void writedata(String file, String inputData)
	   {
		 long timestamp =System.currentTimeMillis();
		   BufferedWriter out = null;   
		     try {   
		         out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));   
		         out.write(timestamp + ", " + inputData + "\r\n");   
		     } catch (Exception e) {   
		         e.printStackTrace();   
		     } finally {   
		         try {   
		         	if(out != null){
		         		out.close();   
		             }
		         } catch (IOException e) {   
		             e.printStackTrace();   
		         }   
		     }    
	   }
	 public static void writeRespData(String file, String inputData)
	   {
		   BufferedWriter out = null;   
		     try {   
		         out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));   
		         out.write(inputData + "\r\n");   
		     } catch (Exception e) {   
		         e.printStackTrace();   
		     } finally {   
		         try {   
		         	if(out != null){
		         		out.close();   
		             }
		         } catch (IOException e) {   
		             e.printStackTrace();   
		         }   
		     }    
	   }
	 
	 public static int fileSzie(String inputFile)
	 {
		File file = new File(inputFile);
		 return (int)file.length();
	 }
	 
	 public static void splitFile(String inputFile, String outFile, int n)
	   {
		 BufferedReader dataRespReader = null;
		 BufferedWriter bw = null;
		 StringBuffer s= new StringBuffer();
		 int countLines = 0;
		 int m=0;
		 try {
			dataRespReader = new BufferedReader(new FileReader(inputFile));
			File file = new File(inputFile);
			try {
				dataRespReader.mark((int)file.length()+1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		 String line="";
		 while(true)
	     {
	    	    try {
					line = dataRespReader.readLine();
					countLines +=1;
					//System.out.println("test!!" + line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	    
		        if (line == null) {
		            //throw new IOException(filename + ": unable to read line");
		        	break;
		        }
	     }
		 try {
			dataRespReader.reset();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 System.out.println("countLines: " + countLines);
		while(m<n)
		{
			 for(int i=0; i<((countLines%n==0)?(countLines/n):(countLines/n)+1);i++)
				{
					try {
						
						line = dataRespReader.readLine();
						if(line==null)
						{
							break;
						}
						s.append(line + "\r\n");
						
						//System.out.println("test!!" + line);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	    
					//System.out.println("i: " + i);
				}
			 try {
				 
				bw = new BufferedWriter(new FileWriter(outFile + "_"+ m +".txt"));
				bw.write(s.toString());
				bw.close();
				s.setLength(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 //writeRespData(outFile + "_"+ m+1 +".txt", line);
			 m +=1;
		}
		
		 	
	   }
	public static void orderRespData(String RespFile, String RespOrderedData)
	 {
		 BufferedReader dataRespReader = null;
		 try {
			dataRespReader = new BufferedReader(new FileReader(RespFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 String[] ResponseList;
		 String line="";
		 String key="";
		 TreeMap<String,String> treeMap = new TreeMap<String,String>();
		 while(true)
	       {
	    	    try {
					line = dataRespReader.readLine();
					//countLines +=1;
					//System.out.println("test!!" + line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	    
		        if (line == null) {
		            //throw new IOException(filename + ": unable to read line");
		        	break;
		        }
		        ResponseList = line.split(", ");
		        //获取逗号后面的参数
		        if(ResponseList[1]!=null)
		        {
		        	 key = ResponseList[1].substring(8,34);
		        	 treeMap.put(key,line);
		        }
	       }
		 //输出已经排序好的Data
		 for(Entry<String, String> entry: treeMap.entrySet())
		 {
			 writeRespData(RespOrderedData, entry.getValue());
		 }
		 
	 }
	public static void readData(String ReqFile, String RespFile, long timeGap, String errorFile)
	{
		BufferedReader dataReader = null;
		BufferedReader dataRespReader = null;
		String line ="";
		String requestHeader = "";
		boolean isFind = false;
		boolean isNewLoop = false;
		String[] requestList;
		String[] respList;
		ArrayList<String> targetList = new ArrayList<String>();
		 try {
			dataReader = new BufferedReader(new FileReader(ReqFile));
			dataRespReader = new BufferedReader(new FileReader(RespFile));
			File file = new File(RespFile);
			try {
				//System.out.println("length: " + file.length());
				dataRespReader.mark((int)file.length()+1);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			 while(true)
		       {
		    	    try {
						line = dataReader.readLine();
						//countLines +=1;
						System.out.println("test!!" + line);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	    
			        if (line == null) {
			            //throw new IOException(filename + ": unable to read line");
			        	break;
			        }
			        requestList = line.split(", ");
			        //获取逗号后面的参数
			        if(requestList[1]!=null)
			        {
			        	requestHeader = requestList[1].substring(8,34);
			        	isNewLoop = true;
			        	while(true)
			        	{
			        		try {
			        			if(isNewLoop)
			        			{
			        				//dataRespReader = new BufferedReader(new FileReader(RespFile));
			        				dataRespReader.reset();
			        			}
			        			if(isFind)
			        			{
			        				break;
			        			}
		
			        			line = dataRespReader.readLine();
			        			//System.out.println("test===" + line);
								
								if(line == null && isFind==false)
								{
									writedata(errorFile, "No Response for " + requestList[1]);
								}
								if(line == null)
								{
									break;	
								}
								isNewLoop = false;
								respList = line.split(", ");
								if(respList[1]!=null)
								{
									targetList = StringSplit(respList[1]);
									for(String temp: targetList)
									{
										if(temp.indexOf(requestHeader)!=-1)
										{
											if(Long.parseLong(respList[0])- Long.parseLong(respList[0])>timeGap)
											{
												writedata(errorFile, requestList[1]);
											}
											isFind = true;
											break;
											
										}
									}
								}
								
			        		} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			        	}
			        	isFind=false;
			        	
			        	
						
			        	
			        }
		       }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {   
	        try {   
	        	if(dataReader != null){
	        		dataReader.close(); 
	        		dataRespReader.close();
	            }
	        }catch (IOException e) {   
		            e.printStackTrace();   
		        }
		}
	}
	
	
	public static void main(String[] args) {
		String userDir = System.getProperty("user.dir"); 
		String RequestData=  userDir + File.separator + "errorRequest.txt";
		String RespData = userDir + File.separator + "respData.txt";
		String ReqSplitFile = userDir + File.separator + "requestData";
		String ErrorFile = userDir + File.separator + "errorReport.txt";
		//splitFile(RequestData, ReqSplitFile, 50);
		//orderRespData(RespData,RespOrderedData);
		//readData(RequestData, RespData, 6000, ErrorFile);
		
		
	//StringSplit("2e2e0401223344556677889900112233445566772e2e81010000112233445566778899001122334455667788990011223344552e2e020111223344556677889900112233445566");
	}
}
