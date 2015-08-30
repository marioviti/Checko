package Support;

import java.util.ArrayList;

/**
 * Created by marioviti on 28/08/15.
 */
public class CalendarEntry {
    public String day;
    public int dayID;
    public int cacheID;
    public int[] values; // 6 interi: 5 valori + id data corrente
    public float[][] summaries; // 5 array (tipi) con 4 float (nutritivi)

    public CalendarEntry(String day,  int[] values, int cacheID, int dayID) {
        this.day = day;
        this.dayID = dayID;
        this.cacheID = cacheID;
        this.values = values;
        summaries = new float[5][4];
    }

    @Override
    public String toString() {
        return  "day: "+day+"\n"+
                "dayID: "+dayID+"\n"+
                "cacheID: "+cacheID+"\n"+
                ", values: { "+values[0]+","+values[1]+","+values[2]+","+values[3]+","+values[4]+" }\n"+
                ", summaries:"+"\n"+
                "\t type1 : {"+ summaries[0][0] +","+ summaries[0][1] +","+ summaries[0][2] +","+ summaries[0][3] +"} ;"+"\n"+
                "\t type2 : {"+ summaries[1][0] +","+ summaries[1][1] +","+ summaries[1][2] +","+ summaries[1][3] +"};"+"\n"+
                "\t type2 : {"+ summaries[2][0] +","+ summaries[2][1] +","+ summaries[2][2] +","+ summaries[2][3] +"};"+"\n"+
                "\t type2 : {"+ summaries[3][0] +","+ summaries[3][1] +","+ summaries[3][2] +","+ summaries[3][3] +"};"+"\n"+
                "\t type2 : {"+ summaries[4][0] +","+ summaries[4][1] +","+ summaries[4][2] +","+ summaries[4][3] +"};";

    }

}
