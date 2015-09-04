package databaseHandling;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.marioviti.checko.CalendarFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import Support.CalendarEntry;
import Support.SupporHolder;

/**
 * Created by marioviti on 24/08/15.
 */

public class DBTransactionAsyncTask extends AsyncTask<ContentValues, String, Object> {

    private DBQueryManager caller;
    private int task;
    private DBOpenHelper myOpenHelper;
    private Object reValues = null;

    public DBTransactionAsyncTask(DBQueryManager activity, DBOpenHelper myOpenHelper, int task){
        this.task = task;
        this.caller = activity;
        this.myOpenHelper = myOpenHelper;
    }

    /**
     * DEBUGGING utility
     * */
    public void show(String table) {
        Cursor cur;
        String query = "SELECT * FROM "+ table;
        SQLiteDatabase db = myOpenHelper.getReadableDatabase();
        cur = db.rawQuery(query,null);
        int colcount = cur.getColumnCount();
        Log.d("show", "FROM "+table);
        while(cur.moveToNext()) {
            for (int i = 0 ; i< cur.getColumnCount(); i++) {
                Log.d("show", "\n-------------------------------------------------"+cur.getColumnName(i) +": "+cur.getString(i));
            }
        }
    }

    /**
     * Return: Ritorna false in 2 casi: se non ci sono date inserite nel database, se l'ultima data è
     * diversa da quella corrente.
     * Ritorna true se la data nel databse è uguale a quella corrente.
     * latestDay e latestDayID sono settate se presenti date nel DB.
     *
     * MODIFY: SupporHolder.latestDay, latestDay
     */
    private boolean checkCurrentDay() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String newDate = dateFormat.format(date);
        SQLiteDatabase rDb = myOpenHelper.getReadableDatabase();
        String rawQuery = "SELECT "+DBOpenHelper.CAL_COL_PK+
                ", "+DBOpenHelper.CAL_COL_DATE+
                " FROM " + DBOpenHelper.DB_TABLE_CAL +
                " ORDER BY "+ DBOpenHelper.CAL_COL_PK +
                " DESC LIMIT 1 ;";
        Cursor cursor = rDb.rawQuery(rawQuery, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            Log.d("checkDay","----------------------------------prima entry:" + SupporHolder.latestDay);
            return false;
        }
        int columnIndexDATE = cursor.getColumnIndex(DBOpenHelper.CAL_COL_DATE);
        int columnIndexDATEID = cursor.getColumnIndex(DBOpenHelper.CAL_COL_PK);
        if (columnIndexDATE > -1) {
            cursor.moveToFirst();
            SupporHolder.latestDay = cursor.getString(columnIndexDATE);
            SupporHolder.latestDayID = cursor.getInt(columnIndexDATEID);
        }
        cursor.close();
        if (newDate.equals(SupporHolder.latestDay)){
            Log.d("checkDay", "----------------------------------data non cambiata:" + SupporHolder.latestDay);
            return true;
        }
            Log.d("checkDay", "----------------------------------data cambiata:" + SupporHolder.latestDay + " - " + newDate);
        return false;
    }

    /**
     * EFFECTS: currentDay e latestDay , currentDayID e latestDayID coincidono
     *
     * MODIFY: SupporHolder.latestDay, latestDayID, currentDay, currentDayID
     *
     */
    private void makeNewDay(SQLiteDatabase wDb) {

        // di default la data è il tempo corrente.
        String query =  "INSERT INTO " + DBOpenHelper.DB_TABLE_CAL +
                " ( type0, type1, type2, type3, type4 )"+
                " VALUES ( '0','0','0','0','0');";
        wDb.execSQL(query);
        SQLiteDatabase rDb = myOpenHelper.getReadableDatabase();
        String rawQuery = "SELECT "+DBOpenHelper.CAL_COL_PK+
                ", "+DBOpenHelper.CAL_COL_DATE+
                " FROM " + DBOpenHelper.DB_TABLE_CAL +
                " ORDER BY "+ DBOpenHelper.CAL_COL_PK +
                " DESC LIMIT 1 ;";
        Cursor cursor = rDb.rawQuery(rawQuery, null);
        int columnIndexDATE = cursor.getColumnIndex(DBOpenHelper.CAL_COL_DATE);
        int columnIndexDATEID = cursor.getColumnIndex(DBOpenHelper.CAL_COL_PK);
        if (columnIndexDATE > -1) {
            cursor.moveToFirst();
            SupporHolder.latestDay = cursor.getString(columnIndexDATE);
            SupporHolder.latestDayID = cursor.getInt(columnIndexDATEID);
            SupporHolder.currentDay = SupporHolder.latestDay;
            SupporHolder.currentDayID = SupporHolder.latestDayID;
        }
        cursor.close();
    }

    private void newEntry( SQLiteDatabase wDb, ContentValues params) {

        params.put(DBOpenHelper.PROD_COL_DATE_FK_ID, SupporHolder.latestDayID);
        wDb.insert(DBOpenHelper.DB_TABLE_PROD, null, params);
    }

    private void updateDate( SQLiteDatabase wDb, int typeValue ) {

        String type = "type"+typeValue;
        String update = "UPDATE "+DBOpenHelper.DB_TABLE_CAL+" SET "+type+" = "+type+" + 1 "+
                "WHERE "+DBOpenHelper.CAL_COL_PK+" = "+ SupporHolder.latestDayID + " ;";
        wDb.execSQL(update);
    }

    /**
     * EFFECTS: Il currentDayID e currentDay devono essere settati al giorno più recente <=> l'id è il
     *  massimo dell'indice del database.
     *
     */
    private void createCalendar(SQLiteDatabase rDb) {

        int cachelimit = SupporHolder.cacheDayLimit;
        String query = "SELECT * FROM " + DBOpenHelper.DB_TABLE_CAL +
                " WHERE "+ DBOpenHelper.CAL_COL_PK +
                " <= " + SupporHolder.latestDayID +
                " ORDER BY " + DBOpenHelper.CAL_COL_PK +
                " DESC LIMIT "+cachelimit+";";

        Cursor cursor = rDb.rawQuery(query, null);

        int i = 0;
        while (cursor.moveToNext() && i<cachelimit) {
            String key = cursor.getString(cursor.getColumnIndex(DBOpenHelper.CAL_COL_DATE));
            int[] values = new int[] {cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6)};
            SupporHolder.calendarCache[i] = new CalendarEntry(key,values,i,cursor.getInt(cursor.getColumnIndex(DBOpenHelper.CAL_COL_PK)));
            if(i==0) {
                SupporHolder.currentDay = key;
                SupporHolder.currentDayID = cursor.getInt(0);
            }
            i++;
        }
        for(int j = i; j<SupporHolder.calendarCache.length; j++) {
            SupporHolder.calendarCache[j]=null;
        }
        SupporHolder.currentCacheDayID = 0;
        cursor.close();
        createCalendarCreateSummary(rDb);
    }

    /**
     *
     * EFFECTS: Vengono creati un numero di sommari pari al massimo a cacheDayLimit oppure al numero di
     * giorni se sono meno di cacheDayLimit.
     *
     * MODIFY: SupporHolder.calendarCache
     *
     */
    private void createCalendarCreateSummary(SQLiteDatabase rDb) {

        int cacheDayID = SupporHolder.currentDayID;
        int i = 0;
        Cursor cursor = null;
        while(cacheDayID>0 && i<SupporHolder.cacheDayLimit) {

            String query = "SELECT" +
                    " SUM(" + DBOpenHelper.PROD_COL_CARB + ")" +
                    ", SUM(" + DBOpenHelper.PROD_COL_PROT + ")" +
                    ", SUM(" + DBOpenHelper.PROD_COL_FAT + ")" +
                    ", SUM(" + DBOpenHelper.PROD_COL_CAL + ")" +
                    ", " + DBOpenHelper.PROD_COL_TYPE +
                    ", " + DBOpenHelper.PROD_COL_DATE_FK_ID +
                    " FROM " + DBOpenHelper.DB_TABLE_PROD +
                    " WHERE " + DBOpenHelper.PROD_COL_DATE_FK_ID + " = " + cacheDayID +
                    " GROUP BY " + DBOpenHelper.PROD_COL_TYPE +
                    " ;";

            cursor = rDb.rawQuery(query, null);
            while (cursor.moveToNext()) {
                Log.d("CreateSummary","\n"+cursor.getInt(5));
                fillSummaryValues(cursor, i, cursor.getInt(4));
            }
            Log.d("CreateSummary",""+SupporHolder.calendarCache[i].toString());
            cacheDayID--;
            i++;
        }
        if(cursor!=null)
            cursor.close();
    }

    private void fillSummaryValues(Cursor cursor, int cachePos, int type) {

        CalendarEntry ce = SupporHolder.calendarCache[cachePos];
        if(ce!=null) {
            float[] values = new float[4];
            values[0] = cursor.getFloat(0);//CARB
            values[1] = cursor.getFloat(1);//PROT
            values[2] = cursor.getFloat(2);//FAT
            values[3] = cursor.getFloat(3);//CAL
            ce.summaries[type] = values;
        }
    }

    private void firstLaunc() {
        SupporHolder.calendarCache[0] = new CalendarEntry("Starts Today", new int [] {20,20,20,20,20,0},0,0);
        SupporHolder.calendarCache[0].summaries = new float[][] {{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
        SupporHolder.currentCacheDayID = 0;
    }

    @Override
    protected Object doInBackground(ContentValues... params) {
        // FSA
        switch (task) {

            case DBQueryManager.INSERT : {

                SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                if(!checkCurrentDay()) {
                    task = DBQueryManager.NEW_DATE;
                    makeNewDay(db);
                }
                newEntry(db, params[0]);
                updateDate(db, params[0].getAsInteger(DBOpenHelper.PROD_COL_TYPE));
                SQLiteDatabase rdb = myOpenHelper.getReadableDatabase();
                createCalendar(rdb);

                break;
            }
            case DBQueryManager.REFRESH_FETCH : {

                if(SupporHolder.latestDayID==-1)
                    checkCurrentDay();
                // uso dell'effetto collaterale
                if(SupporHolder.latestDayID!=-1) {
                    SQLiteDatabase db = myOpenHelper.getReadableDatabase();
                    createCalendar(db);
                } else {
                    firstLaunc();
                }
                break;
            }

            case DBQueryManager.REFRESH_FETCH_SYNC : {

                if(SupporHolder.latestDayID==-1)
                    checkCurrentDay();
                // uso dell'effetto collaterale
                if(SupporHolder.latestDayID!=-1) {
                    SQLiteDatabase db = myOpenHelper.getReadableDatabase();
                    createCalendar(db);
                } else {
                    firstLaunc();
                }
                break;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object result) {

        //show(DBOpenHelper.DB_TABLE_CAL);
        //show(DBOpenHelper.DB_TABLE_PROD);
        caller.manageQueryRes(result, task);
    }
}
