package Support;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marioviti on 27/08/15.
 */
public class SupporHolder {

    public static Bundle si = null;

    public static String latestDay = "";
    public static int latestDayID = -1;
    public static short globalTypeVariable = -1;
    public static int currentDayID = -1;
    public static HashMap<String,float[]> summaryCalendarCache = new HashMap<>();

    public static String summaryKey (int currentDayID, int type) {
        return currentDayID+","+type;
    }

}
