package Analysis.Data;

public class StatCalculatorLong extends StatCalculator<Long> {

    public StatCalculatorLong() {
        super(Long.valueOf(0L), Long.valueOf(Long.MIN_VALUE), Long.valueOf(Long.MAX_VALUE));
    }

    /**
     * Add a single value (normally elapsed time)
     * 
     * @param val the value to add, which should correspond with a single sample
     */
    public void addValue(long val){
        super.addValue(Long.valueOf(val));
    }

    /**
     * Update the calculator with the value for an aggregated sample.
     * 
     * @param val the aggregate value, normally the elapsed time
     * @param sampleCount the number of samples contributing to the aggregate value
     */
    public void addValue(long val, int sampleCount){
        super.addValue(Long.valueOf(val), sampleCount);
    }

    @Override
    protected Long divide(Long val, int n) {
        return Long.valueOf(val.longValue() / n);
    }

    @Override
    protected Long divide(Long val, long n) {
        return Long.valueOf(val.longValue() / n);
    }
}
