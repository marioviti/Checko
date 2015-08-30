package Support;

import android.os.Bundle;

import com.example.marioviti.checko.CalendarFragment;

import java.util.ArrayList;
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
    public static int currentChaceDayID = -1;
    public static String currentDay = "";
    public static HashMap<String,float[]> summaryCalendarCache = new HashMap<>();
    // e queste sono settate
    public static CalendarEntry[] calendarCache = new CalendarEntry[10];
    public static final int cacheDayLimit = 10;

    public static String summaryKey (int currentDayID, int type) {
        return currentDayID+","+type;
    }

}
