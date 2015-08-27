package databaseHandling;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import Support.SupporHolder;

/**
 * Created by marioviti on 24/08/15.
 */

public class DBTransactionAsyncTask extends AsyncTask<ContentValues, String, ContentValues> {

    private DBQueryManager caller;
    private int task;
    private DBOpenHelper myOpenHelper;

    public DBTransactionAsyncTask(DBQueryManager activity, DBOpenHelper myOpenHelper, int task){
        this.task = task;
        this.caller = activity;
        this.myOpenHelper = myOpenHelper;
    }

    private boolean checkDay() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String newDate = dateFormat.format(date);
        SQLiteDatabase rDb = myOpenHelper.getReadableDatabase();
        String rawQuery = "SELECT "+DBOpenHelper.CAL_COL_DATE+
                " FROM " + DBOpenHelper.DB_TABLE_CAL +
                " ORDER BY "+ DBOpenHelper.CAL_COL_PK +
                " DESC LIMIT 1 ;";
        Cursor cursor = rDb.rawQuery(rawQuery, null);
        if (cursor.getCount() == 0) {
            SupporHolder.latestDay = newDate;
            cursor.close();
            Log.d("checkDay","----------------------------------prima entry:" + SupporHolder.latestDay);
            return false;
        }
        int columnIndexDATE = cursor.getColumnIndex(DBOpenHelper.CAL_COL_DATE);
        if (columnIndexDATE > -1) {
            cursor.moveToFirst();
            SupporHolder.latestDay = cursor.getString(columnIndexDATE);
        }
        if (newDate.equals(SupporHolder.latestDay)){
            cursor.close();
            Log.d("checkDay", "----------------------------------data non cambiata:" + SupporHolder.latestDay);
            return true;
        }
        cursor.close();
            Log.d("checkDay", "----------------------------------data cambiata:" + SupporHolder.latestDay + " - " + newDate);
        SupporHolder.latestDay = newDate;
        Log.d("checkDay", "----------------------------------data cambiata:" + SupporHolder.latestDay + " - " + newDate);
        return false;
    }

    private void makeNewDay(SQLiteDatabase wDb) {

        wDb.insert(DBOpenHelper.DB_TABLE_CAL, DBOpenHelper.CAL_COL_TYPE1, null);
        SQLiteDatabase rDb = myOpenHelper.getReadableDatabase();
        Cursor cursor = rDb.rawQuery("SELECT MAX("+DBOpenHelper.CAL_COL_PK+") as MAXID FROM "+ DBOpenHelper.DB_TABLE_CAL+" ;",null);
        int columnIndexMAXID = cursor.getColumnIndex("MAXID");
        Log.d("makeNewDay", "----------------------------------successo: " + (cursor.getCount()));
        if (columnIndexMAXID > -1) {
            // UPDATE DATE ID
            Log.d("makeNewDay", "----------------------------------latestDateID vecchio: " + (SupporHolder.latestDayID));
            cursor.moveToFirst();
            SupporHolder.latestDayID = cursor.getInt(columnIndexMAXID);
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

    private void getCalEntries(SQLiteDatabase rDb) {

        String query = "SELECT * FROM " + DBOpenHelper.DB_TABLE_CAL + " ORDER BY " + DBOpenHelper.CAL_COL_PK + " DESC;";
        Cursor cursor = rDb.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Log.d("FETCH_RES", "----------------------------------columnIndexPK: " + cursor.getInt(0));
            Log.d("FETCH_RES", "----------------------------------columnIndexDATE: " + cursor.getString(1));
            Log.d("FETCH_RES", "----------------------------------columnIndexTYPE0: " + cursor.getInt(2));
            Log.d("FETCH_RES", "----------------------------------columnIndexTYPE1: " + cursor.getInt(3));
            Log.d("FETCH_RES", "----------------------------------columnIndexTYPE2: " + cursor.getInt(4));
            Log.d("FETCH_RES", "----------------------------------columnIndexTYPE3: " + cursor.getInt(5));
            Log.d("FETCH_RES", "----------------------------------columnIndexTYPE4: " + cursor.getInt(6));
        }
        cursor.close();
    }

    private void createSummary(SQLiteDatabase rDb) {

        String query = "SELECT" +
                " SUM("+DBOpenHelper.PROD_COL_CARB+")"+
                ", SUM(" +DBOpenHelper.PROD_COL_PROT+")"+
                ", SUM(" +DBOpenHelper.PROD_COL_FAT+")"+
                ", SUM(" +DBOpenHelper.PROD_COL_CAL+")"+
                ", " +DBOpenHelper.PROD_COL_TYPE+
                " FROM " +DBOpenHelper.DB_TABLE_PROD+
                " GROUP BY " +DBOpenHelper.PROD_COL_TYPE +
                " HAVING " +DBOpenHelper.PROD_COL_DATE_FK_ID +" = "+ SupporHolder.currentDayID +
                " ;";

        Cursor cursor = rDb.rawQuery(query, null);

        while (cursor.moveToNext()) {

            fillSummaryValues(cursor,SupporHolder.currentDayID);
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
    protected ContentValues doInBackground(ContentValues... params) {
        // FSA
        switch (task) {

            case DBQueryManager.INSERT : {

                SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                if(!checkDay()) {
                    makeNewDay(db);
                }
                newEntry(db, params[0]);
                updateDate(db, params[0].getAsInteger(DBOpenHelper.PROD_COL_TYPE));

                break;
            }
            case DBQueryManager.FETCH : {

                SQLiteDatabase db = myOpenHelper.getReadableDatabase();
                getCalEntries(db);
                //createSummary(db);

                break;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(ContentValues result) {
        caller.manageQueryRes(result, task);
    }
}
