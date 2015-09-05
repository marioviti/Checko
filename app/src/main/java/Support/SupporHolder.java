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

    public static String latestDay = "";
    public static int latestDayID = -1;
    public static String currentDay = "";
    public static int currentDayID = -1;
    public static int currentCacheDayID = -1;
    public static int lastCacheDayID = -1;
    public static final int cacheDayLimit = 10;

    public static short globalTypeVariable = -1;
    public static CalendarEntry[] calendarCache = new CalendarEntry[cacheDayLimit];

    public static final int MENU_FRAG = 0;
    public static final int ROOT_FRAG = 1;
    public static final int CALEDAR_FRAG = 2;

    public static int frameHeight;
    public static int frameWidth;

    public static int currentPage = MENU_FRAG;

    public static float BMR = 2010;

}
