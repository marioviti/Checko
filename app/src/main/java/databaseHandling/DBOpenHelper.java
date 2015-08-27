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
    public static final String DB_TABLE_PROD = "products";
    public static final String PROD_COL_PK = "p_id";
    public static final String PROD_COL_UPC = "upc";
    public static final String PROD_COL_TYPE = "p_type";
    public static final String PROD_COL_NAME = "product_name";
    public static final String PROD_COL_DATE_FK_ID = "p_date_id";
    public static final String PROD_COL_CARB = "Total_Carbohydrate";
    public static final String PROD_COL_PROT = "Protein";
    public static final String PROD_COL_FAT = "Total_Fat";
    public static final String PROD_COL_CAL = "Calories";

    public static final String DB_TABLE_CAL = "calendar";
    public static final String CAL_COL_PK = "c_id";
    public static final String CAL_COL_DATE = "c_date";
    public static final String CAL_COL_TYPE1 = "type1";
    public static final String CAL_COL_TYPE2 = "type2";
    public static final String CAL_COL_TYPE3 = "type3";
    public static final String CAL_COL_TYPE4 = "type4";
    public static final String CAL_COL_TYPE5 = "type5";

    public static String latestDay = "";
    public static int latestDateID = -1;

    private String DATABASE_DELETE_PROD = "DROP TABLE IF EXISTS " + DB_TABLE_PROD + " ;";
    private String DATABASE_DELETE_CAL = "DROP TABLE IF EXISTS " + DB_TABLE_CAL + " ;";

    private String DATABASE_CREATE_PROD =  "CREATE TABLE " + DB_TABLE_PROD +
            " (" +
            PROD_COL_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PROD_COL_UPC + " TEXT NOT NULL, " +
            PROD_COL_TYPE + " TEXT NOT NULL, " +
            PROD_COL_NAME + " TEXT, " +
            PROD_COL_DATE_FK_ID + " INTEGER, " +
            PROD_COL_CARB + " FLOAT, " +
            PROD_COL_PROT + " FLOAT, " +
            PROD_COL_FAT + " FLOAT, " +
            PROD_COL_CAL + " FLOAT, "+
            "FOREIGN KEY("+PROD_COL_DATE_FK_ID+") REFERENCES "+DB_TABLE_CAL+"("+CAL_COL_PK+") ON DELETE CASCADE);";

    private String DATABASE_CREATE_CAL=  "CREATE TABLE " + DB_TABLE_CAL +
            " (" +
            CAL_COL_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CAL_COL_DATE + " DATETIME DEFAULT CURRENT_DATE, " +
            CAL_COL_TYPE1 + " INTEGER DEFAULT 0, " +
            CAL_COL_TYPE2 + " INTEGER DEFAULT 0, " +
            CAL_COL_TYPE3 + " INTEGER DEFAULT 0, " +
            CAL_COL_TYPE4 + " INTEGER DEFAULT 0, " +
            CAL_COL_TYPE5 + " INTEGER DEFAULT 0 );";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_DELETE_PROD);
        db.execSQL(DATABASE_DELETE_CAL);
        db.execSQL(DATABASE_CREATE_PROD);
        db.execSQL(DATABASE_CREATE_CAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
