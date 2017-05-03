package Analysis.Data;

import java.io.File;

class TestThread extends Thread{
	
	private String RequestData;
	private String RespData;
	private long time;
	private String ErrorFile;
	public TestThread(String RequestData, String RespData, long time, String ErrorFile)
	{
		this.RequestData = RequestData;
		this.RespData = RespData;
		this.time = time;
		this.ErrorFile = ErrorFile;
	}
	public void run()
	{
		AnalysizeReqRespData.readData(RequestData, RespData, time, ErrorFile);
	}
	
	
	public static void main(String[] args) {
		String userDir = System.getProperty("user.dir"); 
		String RequestData=  userDir + File.separator + "requestData_";
		String RespData = userDir + File.separator + "respData.txt";
		String ReqSplitFile = userDir + File.separator + "requestData";
		String ErrorFile = userDir + File.separator + "errorReport.txt";
		String RequestFile = RequestData;
		
		for(int i=0; i<20; i++)
		{
			RequestFile += i + ".txt";
			new TestThread(RequestFile, RespData, 6000, ErrorFile).start();
			RequestFile = RequestData;
		}
		
	}
	
}
