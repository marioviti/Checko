package databaseHandling;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String newDate = dateFormat.format(date);
        if (newDate.equals(DBOpenHelper.latestDay)){
            return true;
        }
        // UPDATE DATE
        DBOpenHelper.latestDay = newDate;
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
            Log.d("makeNewDay", "----------------------------------latestDateID vecchio: " + (DBOpenHelper.latestDateID));
            cursor.moveToFirst();
            DBOpenHelper.latestDateID = cursor.getInt(columnIndexMAXID);
            Log.d("makeNewDay", "----------------------------------latestDateID nuovo: " + (DBOpenHelper.latestDateID));
        }
        cursor.close();

    }

    private void newEntry( SQLiteDatabase wDb, ContentValues params) {

        params.put(DBOpenHelper.PROD_COL_DATE_FK_ID, DBOpenHelper.latestDateID);
        wDb.insert(DBOpenHelper.DB_TABLE_PROD, null, params);

        Log.d("newEntry", "----------------------------------OK");

    }

    private void updateDate( SQLiteDatabase wDb, String typeValue ) {

        String update = "UPDATE "+DBOpenHelper.DB_TABLE_CAL+" SET "+typeValue+" = "+typeValue+" + 1 "+
                "WHERE "+DBOpenHelper.CAL_COL_PK+" = "+ DBOpenHelper.latestDateID + " ;";
        wDb.execSQL(update);

        Log.d("updateDate", "----------------------------------OK");

    }

    private void getCalEntries(SQLiteDatabase rDb) {

        String query = "SELECT * FROM " + DBOpenHelper.CAL_COL_DATE + " ORDER BY " + DBOpenHelper.CAL_COL_PK + " DESC;";
        Cursor cursor = rDb.rawQuery(query, null);

        int columnIndexPK = cursor.getColumnIndex(DBOpenHelper.CAL_COL_PK),
                columnIndexDATE = cursor.getColumnIndex(DBOpenHelper.CAL_COL_DATE),
                columnIndexTYPE1 = cursor.getColumnIndex(DBOpenHelper.CAL_COL_TYPE1),
                columnIndexTYPE2 = cursor.getColumnIndex(DBOpenHelper.CAL_COL_TYPE2),
                columnIndexTYPE3 = cursor.getColumnIndex(DBOpenHelper.CAL_COL_TYPE3),
                columnIndexTYPE4 = cursor.getColumnIndex(DBOpenHelper.CAL_COL_TYPE4),
                columnIndexTYPE5 = cursor.getColumnIndex(DBOpenHelper.CAL_COL_TYPE5);

        while (cursor.moveToNext()) {
            Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getInt(columnIndexPK));
            Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getString(columnIndexDATE));
            Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getInt(columnIndexTYPE1));
            Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getInt(columnIndexTYPE2));
            Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getInt(columnIndexTYPE3));
            Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getInt(columnIndexTYPE4));
            Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getInt(columnIndexTYPE5));

        }
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
                updateDate(db, params[0].getAsString(DBOpenHelper.PROD_COL_TYPE));

                break;
            }
            case DBQueryManager.FETCH : {

                SQLiteDatabase db = myOpenHelper.getReadableDatabase();

                getCalEntries(db);
                /*
                String[] result_columns = new String[] {
                        DBOpenHelper.PROD_COL_NAME,
                        DBOpenHelper.PROD_COL_FAT,
                        DBOpenHelper.PROD_COL_DATE_FK_ID };
                String where = null;
                String whereArgs[] = null;
                String groupBy = null;
                String having = null;
                String order = null;

                SQLiteDatabase db = myOpenHelper.getReadableDatabase();
                Cursor cursor = db.query(DBOpenHelper.DB_TABLE_PROD, result_columns, where, whereArgs, groupBy, having, order);
                int columnIndexPROD_COL_NAME = cursor.getColumnIndex(DBOpenHelper.PROD_COL_NAME);
                int columnIndexPROD_COL_FAT = cursor.getColumnIndex(DBOpenHelper.PROD_COL_FAT);
                int columnIndexPROD_COL_DATE_FK_ID = cursor.getColumnIndex(DBOpenHelper.PROD_COL_DATE_FK_ID);
                if (columnIndexPROD_COL_NAME > -1) {
                    while(cursor.moveToNext()) {
                        DBOpenHelper.latestDay = cursor.getString(columnIndexPROD_COL_NAME);
                        Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getString(columnIndexPROD_COL_NAME));
                        Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getString(columnIndexPROD_COL_FAT));
                        Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getDouble(columnIndexPROD_COL_DATE_FK_ID));
                    }
                }
                cursor.close();
                */

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
