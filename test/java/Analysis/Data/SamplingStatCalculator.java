package Analysis.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public class SamplingStatCalculator {
	private final StatCalculatorLong calculator = new StatCalculatorLong();

    private double maxThroughput;

    private long firstTime;

    private String label;

    private volatile Sample currentSample;
    
    private enum ParserState {INITIAL, PLAIN, QUOTED, EMBEDDEDQUOTE}
    public static final char QUOTING_CHAR = '"';

    public SamplingStatCalculator(){ // Only for use by test code
        this("");
    }

    public SamplingStatCalculator(String label) {
        this.label = label;
        init();
    }

    private void init() {
        firstTime = Long.MAX_VALUE;
        calculator.clear();
        maxThroughput = Double.MIN_VALUE;
        currentSample = new Sample();
    }

    /**
     * Clear the counters (useful for differential stats)
     *
     */
    public synchronized void clear() {
        init();
    }

    public Sample getCurrentSample() {
        return currentSample;
    }

    /**
     * Get the elapsed time for the samples
     *
     * @return how long the samples took
     */
    public long getElapsed() {
        if (getCurrentSample().getEndTime() == 0) {
            return 0;// No samples collected ...
        }
        return getCurrentSample().getEndTime() - firstTime;
    }

    /**
     * Returns the throughput associated to this sampler in requests per second.
     * May be slightly skewed because it takes the timestamps of the first and
     * last samples as the total time passed, and the test may actually have
     * started before that start time and ended after that end time.
     *
     * @return throughput associated with this sampler per second
     */
    public double getRate() {
        if (calculator.getCount() == 0) {
            return 0.0; // Better behaviour when howLong=0 or lastTime=0
        }

        return getCurrentSample().getThroughput();
    }

    /**
     * Throughput in bytes / second
     *
     * @return throughput in bytes/second
     */
    public double getBytesPerSecond() {
        // Code duplicated from getPageSize()
        double rate = 0;
        if (this.getElapsed() > 0 && calculator.getTotalBytes() > 0) {
            rate = calculator.getTotalBytes() / ((double) this.getElapsed() / 1000);
        }
        if (rate < 0) {
            rate = 0;
        }
        return rate;
    }

    /**
     * Throughput in kilobytes / second
     *
     * @return Throughput in kilobytes / second
     */
    public double getKBPerSecond() {
        return getBytesPerSecond() / 1024; // 1024=bytes per kb
    }

    /**
     * calculates the average page size, which means divide the bytes by number
     * of samples.
     *
     * @return average page size in bytes (0 if sample count is zero)
     */
    public double getAvgPageBytes() {
        long count = calculator.getCount();
        if (count == 0) {
            return 0;
        }
        return calculator.getTotalBytes() / (double) count;
    }

    /**
     * @return the label of this component
     */
    public String getLabel() {
        return label;
    }
    public static void writeAggregateReportHTML(String file, HashMap<String, Sample> Sample) throws FileNotFoundException
    {
    	StringBuilder sb = new StringBuilder();
    	PrintStream printStream = new PrintStream(new FileOutputStream("report.html")); 
    	for(Entry<String, Sample> entry: Sample.entrySet())
		{
    		sb.append("<html>");
        	sb.append("<head>");
        	sb.append("<meta charset=\"UTF-8\">");
        	sb.append("</head>");
        	sb.append("<body>");
        	sb.append("<table border=\"1\"><tr>");
        	sb.append("<th>");
        	sb.append(entry.getValue().getLabel());
        	sb.append("<th>");
        	sb.append("<th>");
        	sb.append(entry.getValue().getCount());
        	sb.append("<th>");
        	sb.append("</table>");
        	sb.append("</body>");
        	sb.append("</html>");
		}
    	
    	
    	
    	printStream.println(sb.toString()); 
    }
    
    public static void writeAggregateReport(String file, String inputData)
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
  //insert MySql
  	 public static void insertDB(Connection conn, String ProjectName, Entry<String, Sample> entry) throws ClassNotFoundException, SQLException, UnknownHostException, SocketException
  	 {
  		 	String sql = "insert into aggregate_report (id, test_group_index, client_ip,project_name,date,label,samples,average,median,90_percent_line,95_percent_line,99_percent_line, min, max, error_rate,tps) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  		 	PreparedStatement preStmt = conn.prepareStatement(sql);
  		 	//get Data
  		 	SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
  		 	SimpleDateFormat day = new SimpleDateFormat("yyyyMMDD");
  		 	UUID uuid = UUID.randomUUID();
		 	preStmt.setString(1, uuid.toString());
		 	preStmt.setString(2, ProjectName+"_"+day.format(new Date()));
  		 	preStmt.setString(3, ErrorReqAnalysis.HostIP());
  		 	preStmt.setString(4, ProjectName);
  		 	preStmt.setString(5, df.format(new Date()));
  		 	preStmt.setString(6, entry.getValue().getLabel());
  		 	preStmt.setLong(7, entry.getValue().getCount());
  		 	preStmt.setLong(8, entry.getValue().getAverage());
  		 	preStmt.setLong(9, entry.getValue().getMedian());
  		 	preStmt.setLong(10, entry.getValue().getPercent90());
  		 	preStmt.setLong(11, entry.getValue().getPercent95());
  		 	preStmt.setLong(12, entry.getValue().getPercent99());
  		 	preStmt.setLong(13, entry.getValue().getMin());
  		 	preStmt.setLong(14, entry.getValue().getMax());
  		 	preStmt.setDouble(15, entry.getValue().getErrorRate());
  		 	preStmt.setDouble(16, entry.getValue().getThroughput());
  	
  		 	
  		 	preStmt.execute();
  	 }
    /*
     * Read Aggregate Report 
     * 
     * */
    public void AggregateReportHandler(String ProjectName, String filename,String output, String url, String userName, String passWord) throws IOException, ClassNotFoundException, SQLException
    {		
    	Connection connect  = ErrorReqAnalysis.DBConn(url, userName, passWord);
    	BufferedReader dataReader = null;
    	LinkedHashMap<String,Long> SampleNameMap = new LinkedHashMap<String,Long>();
    	LinkedHashMap<String[],String> SampleFullDataMap = new LinkedHashMap<String[],String>();
    	HashMap<String,Sample> SampleMap = new HashMap<String,Sample>();
    	String[] parts;
    	String[] Statparts;
    	String newline="";
    	int countline=1;
    	
    	 try {
			dataReader = new BufferedReader(new FileReader(filename));
			File file = new File(filename);
			dataReader.mark((int)file.length()+1);
			//dataReader.mark(400);// Enough to read the header column names
	         // Get the first line, and see if it is the header
			
			while(true)
			{
				if(countline == 1)
				{
					dataReader.readLine();
					
				}

				String line = dataReader.readLine();
				//System.out.println("c: " + countline );
				if (line == null) {
		        	 break;
		         } 
					parts = line.split(",");
					
					newline =parts[0] +","+ parts[1] + ","+ parts[2] +","+ parts[7] +","+ parts[9];

					//System.out.println("line " + newline);
				if((Statparts = newline.split(",")).length!=0)
				{
					 	
					 if(!SampleNameMap.containsKey(parts[2]))
						 {
							 SampleNameMap.put(parts[2], (long)1);
							 
						 }else
						 {
							 long val = SampleNameMap.get(parts[2]);
							 SampleNameMap.put(parts[2], val+1);
						 }
						// SampleMap.put(parts[2], addSample(parts, (long)1));
						 SampleFullDataMap.put(Statparts, parts[2]);
						 //System.out.println("key: " + SampleFullDataMap.get(Statparts));

					
					 
				 }
				 countline ++;
			}
			
			for(Entry<String, Long> entry: SampleNameMap.entrySet())
			{
				countline =1;
				calculator.clear();
				firstTime = Long.MAX_VALUE;
				dataReader.reset();
				for(Entry<String[], String> entryData: SampleFullDataMap.entrySet())
				{
					//System.out.println("value: " + entryData.getValue());
					//System.out.println("key: " + entryData.getKey());
					if(entry.getKey().equals(entryData.getValue()))
					{
						SampleMap.put(entryData.getValue(), addSample(entryData.getKey(), (long)1));
					}
						
				}
				
//				while(true)
//				{
//					
//					if(countline == 1)
//					{
//						
//						dataReader.readLine();
//						
//						
//					}
////					
//					String line = dataReader.readLine();
//					if (line == null) {
//			        	 break;
//			         } 
//					 if((parts = line.split(",")).length!=0)
//					 {
//					
//						 if(parts[2].equals(entry.getKey()))
//						 {
//							 SampleMap.put(parts[2], addSample(parts, (long)1));
//						 }
//						
//						 
//					 }
//					 countline ++;
//				}
			}

			
			//writeAggregateReportHTML(output,SampleMap);
			writeAggregateReport(output, "==================== Aggregate Report ====================");
			writeAggregateReport(output,"Lable" + " | " + "Samples" + " | " + "Average" + " | " + "Median" + " | " + "90% Line" + " | " + "95% Line" +" | " + "99% Line" + " | " + "Min" + " | " + "Max" + " | " + "Error% " + " | " + "TPS ");
			for(Entry<String, Sample> entry: SampleMap.entrySet())
			{
				
				
				writeAggregateReport(output, entry.getValue().getLabel() + " | " + entry.getValue().getCount() +  " | " + entry.getValue().getAverage() + " | " + entry.getValue().getMedian() + " | " + entry.getValue().getPercent90() + " | " + entry.getValue().getPercent95() + " | " + entry.getValue().getPercent99() + " | " + entry.getValue().getMin() + " | " + entry.getValue().getMax() + " | " + entry.getValue().getErrorRate() + " | " + entry.getValue().getThroughput());
				//insert mySQL
				if(connect!=null)
				{
					insertDB(connect, ProjectName, entry);
				}
			}
			
			
	         
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
         
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
    	SamplingStatCalculator aggregate = new SamplingStatCalculator();
    	String userDir = System.getProperty("user.dir"); 
		String RequestData=  userDir + File.separator + "Perflogfile.csv";
		String outReport = userDir + File.separator + "Aggregate_Report.txt";
		String url = "jdbc:mysql://10.1.32.147:3306/perf_log";
		String userName = "admin";
		String passWord ="admin";
		String ProjectName = "LieBao_Sprint01";
    	aggregate.AggregateReportHandler(ProjectName, RequestData, outReport, url, userName, passWord);
	}
    private static boolean isDelimOrEOL(char delim, int ch) {
        return ch == delim || ch == '\n' || ch == '\r';
    }
    /**Read Aggregate Report .CSV file
     * infile is BufferedData from Aggregate Report
     */
    public static String[] csvReadFile(BufferedReader infile, char delim)
            throws IOException {
        int ch;
        ParserState state = ParserState.INITIAL;
        List<String> list = new ArrayList<String>();
        CharArrayWriter baos = new CharArrayWriter(200);
        boolean push = false;
        while (-1 != (ch = infile.read())) {
            push = false;
            switch (state) {
            case INITIAL:
                if (ch == QUOTING_CHAR) {
                    state = ParserState.QUOTED;
                } else if (isDelimOrEOL(delim, ch)) {
                    push = true;
                } else {
                    baos.write(ch);
                    state = ParserState.PLAIN;
                }
                break;
            case PLAIN:
                if (ch == QUOTING_CHAR) {
                    baos.write(ch);
                    throw new IOException(
                            "Cannot have quote-char in plain field:["
                                    + baos.toString() + "]");
                } else if (isDelimOrEOL(delim, ch)) {
                    push = true;
                    state = ParserState.INITIAL;
                } else {
                    baos.write(ch);
                }
                break;
            case QUOTED:
                if (ch == QUOTING_CHAR) {
                    state = ParserState.EMBEDDEDQUOTE;
                } else {
                    baos.write(ch);
                }
                break;
            case EMBEDDEDQUOTE:
                if (ch == QUOTING_CHAR) {
                    baos.write(QUOTING_CHAR); // doubled quote => quote
                    state = ParserState.QUOTED;
                } else if (isDelimOrEOL(delim, ch)) {
                    push = true;
                    state = ParserState.INITIAL;
                } else {
                    baos.write(QUOTING_CHAR);
                    throw new IOException(
                            "Cannot have single quote-char in quoted field:["
                                    + baos.toString() + "]");
                }
                break;
            } // switch(state)
            if (push) {
                if (ch == '\r') {// Remove following \n if present
                    infile.mark(1);
                    if (infile.read() != '\n') {
                        infile.reset(); // did not find \n, put the character
                                        // back
                    }
                }
                String s = baos.toString();
                list.add(s);
                baos.reset();
            }
            if ((ch == '\n' || ch == '\r') && state != ParserState.QUOTED) {
                break;
            }
        } // while not EOF
        if (ch == -1) {// EOF (or end of string) so collect any remaining data
            if (state == ParserState.QUOTED) {
                throw new IOException("Missing trailing quote-char in quoted field:[\""
                        + baos.toString() + "]");
            }
            // Do we have some data, or a trailing empty field?
            if (baos.size() > 0 // we have some data
                    || push // we've started a field
                    || state == ParserState.EMBEDDEDQUOTE // Just seen ""
            ) {
                list.add(baos.toString());
            }
        }
        return list.toArray(new String[list.size()]);
    }

    
    /**
     * Records a sample.
     *
     * @param res
     *            the sample to record
     * @return newly created sample with current statistics
     *
     */
    public Sample addSample(String[] res, long SampleCount) {
        long rtime, cmean, cstdv, cmedian, cpercent, cpercent90,cpercent99,cpercent95,eCount, endTime;
        double throughput, errorRate;
        boolean rbool;
        synchronized (calculator) {
        	if(calculator.getCount()==0)
        	{
        		getCurrentSample().setErrorCount(0);
        	}
        	calculator.addValue(Long.parseLong(res[1]), SampleCount);
            calculator.addBytes(Long.parseLong(res[4]));
            setStartTime(Long.parseLong(res[0]));
            eCount = getCurrentSample().getErrorCount();
            eCount += res[3].equals("true")?0:1;
            endTime = getEndTime(Long.parseLong(res[0])+Long.parseLong(res[1]));
            long howLongRunning = endTime - firstTime;
            throughput = ((double) calculator.getCount() / (double) howLongRunning) * 1000.0;
            if (throughput > maxThroughput) {
                maxThroughput = throughput;
            }

            rtime = Long.parseLong(res[1]);
            cmean = (long)calculator.getMean();
            cstdv = (long)calculator.getStandardDeviation();
            cmedian = calculator.getMedian().longValue();
            cpercent90 = calculator.getPercentPoint( 0.900 ).longValue();
            cpercent99 = calculator.getPercentPoint( 0.990 ).longValue();
            cpercent95 = calculator.getPercentPoint( 0.950 ).longValue();
            cpercent = calculator.getPercentPoint( 0.500 ).longValue();
// TODO cpercent is the same as cmedian here - why? and why pass it to "distributionLine"?
            rbool = Boolean.parseBoolean(res[3]);
        }

        long count = calculator.getCount();
        errorRate = (double)eCount/(double)count ;
        Sample s =
            new Sample(res[2] , rtime, cmean, cstdv, cmedian, cpercent, throughput, eCount, rbool, count, endTime, cpercent90, cpercent95, cpercent99, calculator.getMin(), calculator.getMax(), errorRate);
        currentSample = s;
        //System.out.println("Lable" + " | " + "Samples" + " | " + "Average" + " | " + "Median" + " | " + "90% Line" + " | " + "95% Line" +" | " + "99% Line" + " | " + "Min" + " | " + "Max" + " | " + "Error% " + " | " + "TPS ");
        //System.out.println(s.getLabel() + " | " + s.getCount() +  " | " + s.getAverage() + " | " + s.getMedian() + " | " + s.getPercent90() + " | " + s.getPercent95() + " | " + s.getPercent99() + " | " + s.getMin() + " | " + s.getMax() + " | " + s.getErrorRate() + " | " + s.getThroughput());
        return s;
    }

    private long getEndTime(long res) {
        long endTime = res;
        long lastTime = getCurrentSample().getEndTime();
        if (lastTime < endTime) {
            lastTime = endTime;
        }
        return lastTime;
    }

    /**
     * @param res
     */
    private void setStartTime(long res) {
        long startTime = res;
        if (firstTime > startTime) {
            // this is our first sample, set the start time to current timestamp
            firstTime = startTime;
        }
    }

    /**
     * Returns the raw double value of the percentage of samples with errors
     * that were recorded. (Between 0.0 and 1.0)
     *
     * @return the raw double value of the percentage of samples with errors
     *         that were recorded.
     */
    public double getErrorPercentage() {
        double rval = 0.0;

        if (calculator.getCount() == 0) {
            return rval;
        }
        rval = (double) getCurrentSample().getErrorCount() / (double) calculator.getCount();
        return rval;
    }

    /**
     * For debugging purposes, only.
     */
    @Override
    public String toString() {
        StringBuilder mySB = new StringBuilder();

        mySB.append("Samples: " + this.getCount() + "  ");
        mySB.append("Avg: " + this.getMean() + "  ");
        mySB.append("Min: " + this.getMin() + "  ");
        mySB.append("Max: " + this.getMax() + "  ");
        mySB.append("Error Rate: " + this.getErrorPercentage() + "  ");
        mySB.append("Sample Rate: " + this.getRate());
        return mySB.toString();
    }

    /**
     * @return errorCount
     */
    public long getErrorCount() {
        return getCurrentSample().getErrorCount();
    }

    /**
     * @return Returns the maxThroughput.
     */
    public double getMaxThroughput() {
        return maxThroughput;
    }

    public Map<Number, Number[]> getDistribution() {
        return calculator.getDistribution();
    }

    public Number getPercentPoint(double percent) {
        return calculator.getPercentPoint(percent);
    }

    public long getCount() {
        return calculator.getCount();
    }

    public Number getMax() {
        return calculator.getMax();
    }

    public double getMean() {
        return calculator.getMean();
    }

    public Number getMeanAsNumber() {
        return Long.valueOf((long) calculator.getMean());
    }

    public Number getMedian() {
        return calculator.getMedian();
    }

    public Number getMin() {
        if (calculator.getMin().longValue() < 0) {
            return Long.valueOf(0);
        }
        return calculator.getMin();
    }

    public Number getPercentPoint(float percent) {
        return calculator.getPercentPoint(percent);
    }

    public double getStandardDeviation() {
        return calculator.getStandardDeviation();
    }

}
