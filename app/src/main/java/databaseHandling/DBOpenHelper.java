package databaseHandling;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marioviti on 23/08/15.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "LabelApiDatabaseV1.db";
    public static int DB_V = 1;
    public static final String DB_TABLE = "products";
    public static final  String COL_PK = "c_id";
    public static final String COL_TYPE = "c_type";
    public static final String COL_UPC = "upc";
    public static final String COL_NAME = "product_name";
    public static final String COL_DATE = "c_date";
    public static final String COL_CARB = "Total_Carbohydrate";
    public static final String COL_PROT = "Protein";
    public static final String COL_FAT = "Total_Fat";
    public static final String COL_CAL = "Calories";

    private String DATABASE_DELETE = "DROP TABLE IF EXISTS " + DB_TABLE + " ;";
    private String DATABASE_CREATE =  "CREATE TABLE " + DB_TABLE +
            " (" +
            COL_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_UPC + " TEXT NOT NULL, " +
            COL_TYPE + " TEXT NOT NULL, " +
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
        db.execSQL(DATABASE_DELETE);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
