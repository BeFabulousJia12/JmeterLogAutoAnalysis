package Analysis.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ErrorReqAnalysis {
		
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
			if((targetData.indexOf("2e2e0401",fromIndex)==-1) && (targetData.indexOf("2e2e0201",fromIndex)==-1)&& (targetData.indexOf("2e2e81fe",fromIndex)==-1) && (targetData.indexOf("2e2e0601",fromIndex)==-1))
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
	    		if(targetData.indexOf("2e2e0601",fromIndex)==fromIndex)
	    		{
	         		DataLength = 28*2;
	        		break;
	    		}
	    		if(targetData.indexOf("2e2e0201",fromIndex)==fromIndex)
	    		{
	         		DataLength = 20*2;
	        		break;
	    		}
	    		if(targetData.indexOf("2e2e81fe",fromIndex)==fromIndex)
	    		{
	        		DataLength = 286*2;
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
	//判断系统是否为windows
	public static boolean isWindowsOS() {
		boolean isWindowsOS = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			isWindowsOS = true;
			}
		return isWindowsOS;
		}
	//获取linux的IP
	private static String getLinuxLocalIp() throws SocketException {
		String ip = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
			{ NetworkInterface intf = en.nextElement();
			String name = intf.getName();
			if (!name.contains("docker") && !name.contains("lo")) {
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ipaddress = inetAddress.getHostAddress().toString();
						if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
							ip = ipaddress;
							//System.out.println(ipaddress);
							}
						}
					}
				}
			}
			} catch (SocketException ex) {
			System.out.println("获取ip地址异常");
			ip = "127.0.0.1";
			ex.printStackTrace();
			}
		System.out.println("IP:"+ip);
		return ip;
		}
	/*获取当前IP地址*/
	public static String HostIP() throws UnknownHostException, SocketException
	{
		if(isWindowsOS())
		{
			InetAddress address = InetAddress.getLocalHost();
			System.out.println("address " + address);
			return address.toString();
		}else{
			
			return getLinuxLocalIp();
		}
		
	}
	public static HashMap<String,String> ComparedTagretDataMap(String RespFile)
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
		 HashMap<String,String> RespMap = new HashMap<String,String>();
		 ArrayList<String> targetList = new ArrayList<String>();
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
	        if(!line.isEmpty())
	        {
	        	ResponseList = line.split(", ");
		        //构建Key为TID，Resp为Timestamp
		        if(ResponseList[1]!=null)
		        {
		        	
						targetList = StringSplit(ResponseList[1]);
						for(String temp: targetList)
						{ 
							key = temp.substring(8,34);
							RespMap.put(key,ResponseList[0]);
						}
		        }
	        }
	        
       }
		 try {
			dataRespReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println("MAP SIZE: " + RespMap.size());
		 return RespMap;
		 
	 }
	
	public static HashMap<String,String> ComparedOriginDataMap(String RespFile)
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
		 HashMap<String,String> RespMap = new HashMap<String,String>();
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
	        //构建Key为TID，Resp为Timestamp
	        if(ResponseList[1]!=null)
	        {
	        	 key = ResponseList[1].substring(8,34);
	        	 RespMap.put(key,ResponseList[0]);
	        }
      }
		 try {
			dataRespReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println("MAP SIZE: " + RespMap.size());
		 return RespMap;
		 
	 }
	
	public static HashMap<String,String> FullDataMap(String RespFile)
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
		 HashMap<String,String> FullDataMap = new HashMap<String,String>();
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
	        //构建Key为TID，Resp为Timestamp
	        if(ResponseList[1]!=null)
	        {
	        	 key = ResponseList[1].substring(8,34);
	        	 FullDataMap.put(key,ResponseList[1]);
	        }
      }
		 try {
			dataRespReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return FullDataMap;
		 
	 }
	 public static void writeErrorData(String file, String inputData)
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
	 
	 //Connect to MySQL
	 public static Connection DBConn(String url, String userName, String passWord) throws ClassNotFoundException, SQLException
	 {
		 	String driver = "com.mysql.jdbc.Driver";
			
			
			Class.forName(driver);
			
			Connection conn = DriverManager.getConnection(url, userName, passWord);
			
			if(!conn.isClosed())
			{
				System.out.println("Succeeded connecting to the Database!");
			}
			return conn;
	 }
	 //insert MySql
	 public static void insertDB(Connection conn, String ProjectName, int inputData01, int inputData02) throws ClassNotFoundException, SQLException, UnknownHostException, SocketException
	 {
		 	String sql = "insert into error_report (id, test_group_index, client_ip,project_name,date,latency_count,no_response_count) values (?,?,?,?,?,?,?)";
		 	PreparedStatement preStmt = conn.prepareStatement(sql);
		 	//get Data
		 	SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		 	SimpleDateFormat day = new SimpleDateFormat("yyyyMMDD");
		 	UUID uuid = UUID.randomUUID();
		 	preStmt.setString(1, uuid.toString());
		 	preStmt.setString(2, ProjectName+"_"+day.format(new Date()));
		 	preStmt.setString(3, HostIP());
		 	preStmt.setString(4, ProjectName);
		 	preStmt.setString(5, df.format(new Date()));
		 	preStmt.setLong(6, inputData01);
		 	preStmt.setLong(7, inputData02);
		 	
		 	preStmt.execute();
	 }
	 public static void AutoAnalysis(String projectName, String RequestFile, String RespFile, String ErrorFile, long ExpectedLatency, String url, String userName, String password) throws ClassNotFoundException, SQLException, UnknownHostException, SocketException {
			
			Connection mysqlconn = DBConn(url,userName, password);
			HashMap<String, String>  errorData= new HashMap<String, String>();
			HashMap<String,String> RespData = new HashMap<String, String>();
			HashMap<String,String> FullData = new HashMap<String, String>();
			errorData = ComparedOriginDataMap(RequestFile);
			RespData = ComparedTagretDataMap(RespFile);
			FullData = FullDataMap(RequestFile);
			long errorDataTimeStamp = 0;
			long RespDataTimeStamp = 0;
			int countLatencyNum = 0;
			int countNoResp = 0 ;
			writeErrorData(ErrorFile, "==================== Error Report ====================");
			for(Entry<String, String> entry: errorData.entrySet())
			{
					if(RespData.containsKey(entry.getKey()))
					{
						errorDataTimeStamp = Long.parseLong(entry.getValue());
						RespDataTimeStamp = Long.parseLong(RespData.get(entry.getKey()));
						if(RespDataTimeStamp - errorDataTimeStamp > ExpectedLatency)
						{
							writeErrorData(ErrorFile, "IMSI-TID Found: " + FullData.get(entry.getKey()) + "; "+ "ExpectedLatency: " + ExpectedLatency +"; " +"ActualLatency: " + (RespDataTimeStamp - errorDataTimeStamp));
							
							countLatencyNum++;
						}
					}else
					{
						writeErrorData(ErrorFile, "No Response for this IMSI-TID: " + FullData.get(entry.getKey()));
						countNoResp++;
					}
			}
			writeErrorData(ErrorFile, "Latency Count: " + countLatencyNum);
			writeErrorData(ErrorFile, "NoResponse Count: " + countNoResp);
			if(mysqlconn!=null)
			{
				insertDB(mysqlconn, projectName, countLatencyNum , countNoResp);
			}
		}
	 
	public static void main(String[] args) throws ClassNotFoundException, SQLException, UnknownHostException, SocketException {
		
		String userDir = System.getProperty("user.dir"); 
		String RequestFile =  userDir + File.separator + "errorRequest.txt";
		String RespFile = userDir + File.separator + "respData.txt";
		String ErrorFile = userDir + File.separator + "errorReport.txt";
		String projectName = "LieBao";
		Connection mysqlconn = DBConn("jdbc:mysql://10.1.32.147:3306/perf_log", "admin","admin");
		HashMap<String, String>  errorData= new HashMap<String, String>();
		HashMap<String,String> RespData = new HashMap<String, String>();
		HashMap<String,String> FullData = new HashMap<String, String>();
		errorData = ComparedOriginDataMap(RequestFile);
		RespData = ComparedTagretDataMap(RespFile);
		FullData = FullDataMap(RequestFile);
		long errorDataTimeStamp = 0;
		long RespDataTimeStamp = 0;
		long expectedTime =70000;
		int countLatencyNum = 0;
		int countNoResp = 0 ;
		writeErrorData(ErrorFile, "==================== Error Report ====================");
		for(Entry<String, String> entry: errorData.entrySet())
		{
				if(RespData.containsKey(entry.getKey()))
				{
					errorDataTimeStamp = Long.parseLong(entry.getValue());
					RespDataTimeStamp = Long.parseLong(RespData.get(entry.getKey()));
					if(RespDataTimeStamp - errorDataTimeStamp > expectedTime)
					{
						writeErrorData(ErrorFile, "IMSI-TID Found: " + FullData.get(entry.getKey()) + "; "+ "ExpectedLatency: " + expectedTime +"; " +"ActualLatency: " + (RespDataTimeStamp - errorDataTimeStamp));
						
						countLatencyNum++;
					}
				}else
				{
					writeErrorData(ErrorFile, "No Response for this IMSI-TID: " + FullData.get(entry.getKey()));
					countNoResp++;
				}
		}
		writeErrorData(ErrorFile, "Latency Count: " + countLatencyNum);
		writeErrorData(ErrorFile, "NoResponse Count: " + countNoResp);
		
		if(mysqlconn!=null)
		{
			insertDB(mysqlconn, projectName, countLatencyNum , countNoResp);
		}
		
		
	}
	
}
