package Analysis.Data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class LogAnalysis extends DefaultHandler{
	
	Stack<String> tags = new Stack<String>();
	boolean isReadException = false;
	int count =0;
	String userDir = System.getProperty("user.dir"); 
	String errorReqfile =  userDir + File.separator + "errorRequest.txt";
	String ts="";
	String preTag=null;


public static void writeAssertErrordata(String file, String timeStamp, String inputData)
	   {
			BufferedWriter out = null;   
		     try {   
		         out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));   
		         out.write(timeStamp + ", " + inputData + "\r\n");   
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

public static void main(String[] args) {
	
	String userDir = System.getProperty("user.dir"); 
	String ReqDatafile=  userDir + File.separator + "AssertError.txt";
//	try {
//		SAXParserFactory sf = SAXParserFactory.newInstance();
//		SAXParser sp = sf.newSAXParser();
//		LogAnalysis reader = new LogAnalysis();
//		sp.parse(new InputSource(ReqDatafile), reader);
//		}catch (Exception e)
//		{
//		e.printStackTrace();
//		}    
	
	errorLog(ReqDatafile);
}

public static void errorLog(String ReqDatafile) {
	
	try {
		SAXParserFactory sf = SAXParserFactory.newInstance();
		SAXParser sp = sf.newSAXParser();
		LogAnalysis reader = new LogAnalysis();
		sp.parse(new InputSource(ReqDatafile), reader);
		}catch (Exception e)
		{
		e.printStackTrace();
		}    
}
public void characters(char ch[], int start, int length)
throws SAXException {
//String tag = (String) tags.peek();
	
StringBuffer sb = new StringBuffer();
if(preTag!=null)
{
	if (preTag.equals("name")) {
		System.out.print("name：" + new String(ch, start, length));
		}

		if (preTag.equals("samplerData")) {
		System.out.println("samplerData:" + new String(ch, start, length));
		sb.append(new String(ch, start, length));
		count +=1;
		System.out.println("count: " + count);
		writeAssertErrordata(errorReqfile, ts, sb.toString());

		}	
}


}
public void startElement(String uri, String localName, String qName,Attributes attrs)
{
	String timeStamp ="";

	for(int i=0; i<attrs.getLength();i++)
	{
		//System.out.println("i: " + i);
		//tags.push(qName);
		//System.out.println(attrs.getQName(i));
		//System.out.println(attrs.getValue(i));
			if(attrs.getQName(i).equals("ts"))
			{
				timeStamp = attrs.getValue(i);
			}
			if(attrs.getValue(i).contains("ReadException"))
			{
				isReadException = true;
				ts = timeStamp;
				
			}		
		
	}
	if(qName.equals("samplerData") && isReadException)
	{
		//tags.push(qName);
		preTag = qName;
		isReadException=false;
		
	}else
	{
		//tags.push("ingore");
		preTag ="";
	}
	


}

public void endElement(String url, String localName, String qName)
		throws SAXException {
//tags.pop();
	preTag = null;
}


}
