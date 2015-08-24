package databaseHandling;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by marioviti on 24/08/15.
 */

public class DBtransactionHandler extends AsyncTask<String, String, ContentValues> {

    private QueryManager caller;
    private int task;
    private DBOpenHelper myOpenHelper;


    public DBtransactionHandler(QueryManager activity, DBOpenHelper myOpenHelper ,int task){
        this.task = task;
        this.caller = activity;
        this.myOpenHelper = myOpenHelper;
    }

    @Override
    protected ContentValues doInBackground(String... params) {
        switch (task) {
            case QueryManager.INSERT : {

                myOpenHelper.getWritableDatabase();
                ContentValues newValues = new ContentValues();

                newValues.put("MESSAGE", "ciao");
                // Insert the row into your table
                SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                db.insert(DBOpenHelper.DB_NAME, null, newValues);

                break;
            }
            case QueryManager.FETCH : {

                String[] result_columns = new String[] { DBOpenHelper.COL_PK, DBOpenHelper.COL_DATE };
                String where = null;
                String whereArgs[] = null;
                String groupBy = null;
                String having = null;
                String order = null;

                SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                Cursor cursor = db.query(DBOpenHelper.DB_TABLE,
                        result_columns, where,
                        whereArgs, groupBy, having, order);

                int columnIndex = cursor.getColumnIndex(DBOpenHelper.COL_DATE);
                if (columnIndex > -1) {
                    while(cursor.moveToNext())
                        Log.d("RES", "----------------------------------estratto:" + cursor.getString(columnIndex));
                }

                break;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(ContentValues result) {
        caller.manageQuery(result, task);
    }
}
