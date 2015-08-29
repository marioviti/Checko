package Support;

/**
 * Created by marioviti on 28/08/15.
 */
public class CalendarEntry {
    public String day;
    public int[] values; // 6 interi: 5 valori + id data corrente

    public CalendarEntry(String day,  int[] values){
        this.day = day;
        this.values = values;
    }
    @Override
    public String toString() {
        return "day: "+day+", values"+values[0]+","+values[1]+","+values[2]+","+values[3]+","+values[4]+",";
    }

}
