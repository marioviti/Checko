package databaseHandling;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

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

    @Override
    protected ContentValues doInBackground(ContentValues... params) {
        switch (task) {
            case DBQueryManager.INSERT : {

                myOpenHelper.getWritableDatabase();
                //ContentValues newValues = new ContentValues();
                //newValues.put("MESSAGE", "ciao");
                // Insert the row into your table
                Log.d("DBTransactionTasko exe", "------------------------------------------------------" + params[0].toString());
                SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                db.insert(DBOpenHelper.DB_TABLE, null, params[0]);

                break;
            }
            case DBQueryManager.FETCH : {

                String[] result_columns = new String[] {
                        DBOpenHelper.COL_NAME,
                        DBOpenHelper.COL_DATE,
                        DBOpenHelper.COL_PROT };
                String where = null;
                String whereArgs[] = null;
                String groupBy = null;
                String having = null;
                String order = null;

                SQLiteDatabase db = myOpenHelper.getReadableDatabase();
                Cursor cursor = db.query(DBOpenHelper.DB_TABLE, result_columns, where, whereArgs, groupBy, having, order);
                int columnIndexCOL_NAME = cursor.getColumnIndex(DBOpenHelper.COL_NAME);
                int columnIndexCOL_DATE = cursor.getColumnIndex(DBOpenHelper.COL_DATE);
                int columnIndexCOL_PROT = cursor.getColumnIndex(DBOpenHelper.COL_PROT);
                if (columnIndexCOL_NAME > -1) {
                    while(cursor.moveToNext()) {
                        Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getString(columnIndexCOL_NAME));
                        Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getString(columnIndexCOL_DATE));
                        Log.d("FETCH_RES", "----------------------------------estratto: " + cursor.getDouble(columnIndexCOL_PROT));
                    }
                }
                cursor.close();

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
