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

    /*

    EFFECTS: Ritorna false in 2 casi: se non ci sono date inserite nel database, se l'ultima data è
    diversa da quella corrente.
    Ritorna true se la data nel databse è uguale a quella corrente.
    latestDay e latestDayID sono settate se presenti date nel DB.

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

    /*

    EFFECTS: currentDay e latestDay , currentDayID e latestDayID coincidono

     */
    private void makeNewDay(SQLiteDatabase wDb) {

        wDb.insert(DBOpenHelper.DB_TABLE_CAL, DBOpenHelper.CAL_COL_TYPE1, null);
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
            Log.d("makeNewDay", "----------------------------------latestDateID nuovo: " + (SupporHolder.latestDayID));
        }
        cursor.close();
    }

    private void newEntry( SQLiteDatabase wDb, ContentValues params) {

        params.put(DBOpenHelper.PROD_COL_DATE_FK_ID, SupporHolder.latestDayID);
        wDb.insert(DBOpenHelper.DB_TABLE_PROD, null, params);

        Log.d("newEntry", "----------------------------------OK");

    }

    private void updateDate( SQLiteDatabase wDb, int typeValue ) {

        String type = "type"+typeValue;
        String update = "UPDATE "+DBOpenHelper.DB_TABLE_CAL+" SET "+type+" = "+type+" + 1 "+
                "WHERE "+DBOpenHelper.CAL_COL_PK+" = "+ SupporHolder.latestDayID + " ;";

        wDb.execSQL(update);
        Log.d("updateDate", update);
        Log.d("updateDate", "----------------------------------OK");

    }

    /*

    EFFECTS: Il currentDayID e currentDay devono essere settati al giorno più recente <=> l'id è il
    massimo dell'indice del database.

     */
    private void createCalendar(SQLiteDatabase rDb) {
        int cachelimit = SupporHolder.cacheDayLimit;
        String query = "SELECT * FROM " + DBOpenHelper.DB_TABLE_CAL + " ORDER BY " + DBOpenHelper.CAL_COL_PK + " DESC LIMIT "+cachelimit+";";
        Cursor cursor = rDb.rawQuery(query, null);

        int i = 0;
        while (cursor.moveToNext() && i<cachelimit) {
            String key = cursor.getString(1);
            int[] values = new int[] {cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getInt(0)};
            SupporHolder.calendarCache[i] = new CalendarEntry(key,values,i,cursor.getInt(0));
            if(i==0) {
                SupporHolder.currentDay = key;
                SupporHolder.currentDayID = cursor.getInt(0);
            }
            i++;
        }
        SupporHolder.currentChaceDayID = 0;
        cursor.close();
    }

    /*

    EFFECTS: Vengono creati un numero di sommari pari al massimo a cacheDayLimit oppure al numero di
    giorni se sono meno di cacheDayLimit.

     */
    private void createSummary(SQLiteDatabase rDb) {

        int cacheDayID = SupporHolder.currentDayID;
        int i = 0;
        while(cacheDayID>0 && i<SupporHolder.cacheDayLimit) {
            String query = "SELECT" +
                    " SUM(" + DBOpenHelper.PROD_COL_CARB + ")" +
                    ", SUM(" + DBOpenHelper.PROD_COL_PROT + ")" +
                    ", SUM(" + DBOpenHelper.PROD_COL_FAT + ")" +
                    ", SUM(" + DBOpenHelper.PROD_COL_CAL + ")" +
                    ", " + DBOpenHelper.PROD_COL_TYPE +
                    " FROM " + DBOpenHelper.DB_TABLE_PROD +
                    " GROUP BY " + DBOpenHelper.PROD_COL_TYPE +
                    " HAVING " + DBOpenHelper.PROD_COL_DATE_FK_ID + " = " + cacheDayID +
                    " ;";
            Cursor cursor = rDb.rawQuery(query, null);
            while (cursor.moveToNext()) {
                fillSummaryValues(cursor, i, cursor.getInt(4));
            }
            Log.d("DBTransactionAsyncTask", "created:\n"+SupporHolder.calendarCache[i].toString());
            cursor.close();
            cacheDayID--;
            i++;
        }
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
        SupporHolder.currentChaceDayID = 0;
    }

    @Override
    protected Object doInBackground(ContentValues... params) {
        // FSA
        switch (task) {

            case DBQueryManager.INSERT : {

                SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                if(!checkCurrentDay()) {
                    makeNewDay(db);
                }
                newEntry(db, params[0]);
                updateDate(db, params[0].getAsInteger(DBOpenHelper.PROD_COL_TYPE));

                break;
            }
            case DBQueryManager.REFRESH_FETCH : {

                if(SupporHolder.latestDayID==-1)
                    checkCurrentDay();
                // uso dell'effetto collaterale
                if(SupporHolder.latestDayID!=-1) {
                    SQLiteDatabase db = myOpenHelper.getReadableDatabase();
                    createCalendar(db);
                    createSummary(db);
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
        caller.manageQueryRes(result, task);
    }
}
