package Analysis.Data;

import java.io.Serializable;
import java.text.Format;
import java.util.Date;

public class Sample implements Serializable, Comparable<Sample> {
    private static final long serialVersionUID = 240L;

    private final long data; // = elapsed

    private final long average;

    private final long median;

    private final long distributionLine; // TODO: what is this for?

    private final long deviation;

    private final double throughput;

    private long errorCount;

    private final boolean success;

    private final String label;

    private final String threadName;

    private final long count;

    private final long endTime;
    
    private final long cpercent90;
    
    private final long cpercent95;
    
    private final long cpercent99;
    
    private final long min;
    
    private final long max;
    
    private final double errorRate;

    private final int bytes;

    public Sample(String name, long data, long average, long deviation, long median, long distributionLine,
            double throughput, long errorCount, boolean success, long num, long endTime, long cpercent90, long cpercent95, long cpercent99, long min, long max, double errorRate) {
        this.data = data;
        this.average = average;
        this.deviation = deviation;
        this.throughput = throughput;
        this.success = success;
        this.median = median;
        this.distributionLine = distributionLine;
        this.label = name;
        this.errorCount = errorCount;
        this.count = num;
        this.endTime = endTime;
        this.cpercent90 = cpercent90;
        this.cpercent95 = cpercent95;
        this.cpercent99 = cpercent99;
        this.min = min;
        this.max = max;
        this.errorRate = errorRate;
        this.bytes = 0;
        this.threadName = "";
    }

//    public Sample(String name, long data, long average, long deviation, long median, long distributionLine,
//            double throughput, long errorCount, boolean success, long num, long endTime, long cpercent90, long cpercent95, long cpercent99, long min, long max, long errorRate,int bytes, String threadName) {
//        this.data = data;
//        this.average = average;
//        this.deviation = deviation;
//        this.throughput = throughput;
//        this.success = success;
//        this.median = median;
//        this.distributionLine = distributionLine;
//        this.label = name;
//        this.errorCount = errorCount;
//        this.count = num;
//        this.endTime = endTime;
//        this.bytes = bytes;
//        this.threadName = threadName;
//    }
//
    public Sample() {
        this(null, 0, 0, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    // Appears not to be used - however it is invoked via the Functor class
    public int getBytes() {
        return bytes;
    }

    /**
     * @return Returns the average.
     */
    public long getAverage() {
        return average;
    }

    /**
     * @return Returns the count.
     */
    public long getCount() {
        return count;
    }

    /**
     * @return Returns the data (usually elapsed time)
     */
    public long getData() {
        return data;
    }

    /**
     * @return Returns the deviation.
     */
    public long getDeviation() {
        return deviation;
    }

    /**
     * @return Returns the distributionLine.
     */
    public long getDistributionLine() {
        return distributionLine;
    }

    /**
     * @return Returns the error.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return Returns the errorCount.
     */
    public long getErrorCount() {
        return errorCount;
    }
    
    /**
     * @return Returns the errorCount.
     */
    public long setErrorCount(long count) {
    	errorCount = count;
        return errorCount;
    }
    /**
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return Returns the threadName.
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * @return Returns the median.
     */
    public long getMedian() {
        return median;
    }
    
    /**
     * @return Returns 90%.
     */
    public long getPercent90() {
        return cpercent90;
    }
    
    /**
     * @return Returns 95%.
     */
    public long getPercent95() {
        return cpercent95;
    }
    
    /**
     * @return Returns 99%.
     */
    public long getPercent99() {
        return cpercent99;
    }
    
    /**
     * @return Returns Min.
     */
    public long getMin() {
        return min;
    }
    
    /**
     * @return Returns Max.
     */
    public long getMax() {
        return max;
    }
    
    /**
     * @return Returns errorRate.
     */
    public double getErrorRate() {
        return errorRate;
    }
    
    /**
     * @return Returns the throughput.
     */
    public double getThroughput() {
        return throughput;
    }

    /** {@inheritDoc} */
    public int compareTo(Sample o) {
        Sample oo = o;
        return ((count - oo.count) < 0 ? -1 : (count == oo.count ? 0 : 1));
    }

    // TODO should equals and hashCode depend on field other than count?
    
    @Override
    public boolean equals(Object o){
        return (
                (o instanceof Sample) &&
                (this.compareTo((Sample) o) == 0)
                );
    }

    @Override
    public int hashCode(){
        return (int)(count ^ (count >>> 32));
    }

    /**
     * @return Returns the endTime.
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * @return Returns the (calculated) startTime, assuming Data is the elapsed time.
     */
    public long getStartTime() {
        return endTime-data;
    }

    /**
     * @param format the format of the time to be used
     * @return the start time using the specified format
     * Intended for use from Functors
     */
    public String getStartTimeFormatted(Format format) {
        return format.format(new Date(getStartTime()));
    }
}
