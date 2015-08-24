package databaseHandling;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marioviti on 23/08/15.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "LabelApiDatabase.db";
    public static String DB_TABLE = "prodotto";
    public static String COL_PK = "_ID";
    public static String COL_UPC =  "upc";
    public static String COL_NAME = "product_name";
    public static String COL_DATE = "_DATE";
    public static String COL_CARB = "Total Carbohydrate";
    public static String COL_PROT = "Protein";
    public static String COL_FAT = "Total Fat";
    public static String COL_CAL = "Calories";


    private String DATABASE_CREATE =  "CREATE TABLE " + DB_TABLE +
            " (" + COL_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_UPC + " TEXT NOT NULL, " +
            COL_NAME + " TEXT, " +
            COL_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            COL_CARB + " FLOAT, " +
            COL_PROT + " FLOAT, " +
            COL_FAT + " FLOAT, " +
            COL_CAL + " FLOAT);";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
