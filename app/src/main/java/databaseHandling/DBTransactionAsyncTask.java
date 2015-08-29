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

    private boolean checkCurrentDay() {

        // Ritorna false in 2 casi, se non ci sono date inserite nel database o se l'ultima data è diversa da quella corrente
        // Ritorna true se la data nel databse è uguale a quella corrente e le setta nel contenitore globale.

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
            //SupporHolder.latestDay = newDate;
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

    private void makeNewDay(SQLiteDatabase wDb) {

        // current e latest coincidono

        wDb.insert(DBOpenHelper.DB_TABLE_CAL, DBOpenHelper.CAL_COL_TYPE1, null);
        SQLiteDatabase rDb = myOpenHelper.getReadableDatabase();
        String rawQuery = "SELECT "+DBOpenHelper.CAL_COL_PK+
                ", "+DBOpenHelper.CAL_COL_DATE+
                " FROM " + DBOpenHelper.DB_TABLE_CAL +
                " ORDER BY "+ DBOpenHelper.CAL_COL_PK +
                " DESC LIMIT 1 ;";
        Cursor cursor = rDb.rawQuery(rawQuery,null);
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

    private void createCalendar(SQLiteDatabase rDb) {
        int cachelimit = SupporHolder.cacheDayLimit;
        String query = "SELECT * FROM " + DBOpenHelper.DB_TABLE_CAL + " ORDER BY " + DBOpenHelper.CAL_COL_PK + " DESC LIMIT "+cachelimit+";";
        Cursor cursor = rDb.rawQuery(query, null);

        int i = 0;
        while (cursor.moveToNext() && i<cachelimit) {
            Log.d("FETCH_RES", "----------------------------------columnIndexPK: " + cursor.getInt(0));
            Log.d("FETCH_RES", "----------------------------------columnIndexDATE: " + cursor.getString(1));
            Log.d("FETCH_RES", "----------------------------------columnIndexTYPE0: " + cursor.getInt(2));
            Log.d("FETCH_RES", "----------------------------------columnIndexTYPE1: " + cursor.getInt(3));
            Log.d("FETCH_RES", "----------------------------------columnIndexTYPE2: " + cursor.getInt(4));
            Log.d("FETCH_RES", "----------------------------------columnIndexTYPE3: " + cursor.getInt(5));
            Log.d("FETCH_RES", "----------------------------------columnIndexTYPE4: " + cursor.getInt(6));
            String key = cursor.getString(1);
            int[] values = new int[] {cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getInt(0)};
            SupporHolder.calendarCache[i] = new CalendarEntry(key,values);
            SupporHolder.currentDay = key;
            SupporHolder.currentDayID = cursor.getInt(0);
            SupporHolder.currentChaceDayID = 0;
            i++;
        }
        cursor.close();
    }

    private void createSummary(SQLiteDatabase rDb) {

        int currentDayID = SupporHolder.currentDayID;
        String query = "SELECT" +
                " SUM("+DBOpenHelper.PROD_COL_CARB+")"+
                ", SUM(" +DBOpenHelper.PROD_COL_PROT+")"+
                ", SUM(" +DBOpenHelper.PROD_COL_FAT+")"+
                ", SUM(" +DBOpenHelper.PROD_COL_CAL+")"+
                ", " +DBOpenHelper.PROD_COL_TYPE+
                " FROM " +DBOpenHelper.DB_TABLE_PROD+
                " GROUP BY " +DBOpenHelper.PROD_COL_TYPE +
                " HAVING " +DBOpenHelper.PROD_COL_DATE_FK_ID +" = "+ currentDayID +
                " ;";
        Cursor cursor = rDb.rawQuery(query, null);
        while (cursor.moveToNext()) {
            fillSummaryValues(cursor,currentDayID);
        }
        cursor.close();
    }

    private void fillSummaryValues(Cursor cursor, int currentDayID) {

        String key = SupporHolder.summaryKey(currentDayID,cursor.getInt(4));
        float[] values = new float[4];
        values[0] = cursor.getFloat(0);//CARB
        values[1] = cursor.getFloat(1);//PROT
        values[2] = cursor.getFloat(2);//FAT
        values[3] = cursor.getFloat(3);//CAL
        SupporHolder.summaryCalendarCache.put(key, values);

        float[] valuesck = SupporHolder.summaryCalendarCache.get(key);
        Log.d("fillSummaryValues", "-----------------values: "+valuesck[0]+"-"+valuesck[1]+"-"+valuesck[2]+"-"+valuesck[3]+" key: "+key);
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
