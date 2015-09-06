package databaseHandling;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by marioviti on 23/08/15.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "LabelApiDatabaseV1.db";
    public static int DB_V = 31;

    public static final String DB_TABLE_PROFILE = "profile";
    public static final String PROF_COL_PK = "p_id";
    public static final String PROF_COL_BMR = "BMR";

    public static final String DB_TABLE_CAL = "calendar";
    public static final String CAL_COL_PK = "c_id";
    public static final String CAL_COL_DATE = "c_date";
    public static final String CAL_COL_TYPE0 = "type0";
    public static final String CAL_COL_TYPE1 = "type1";
    public static final String CAL_COL_TYPE2 = "type2";
    public static final String CAL_COL_TYPE3 = "type3";
    public static final String CAL_COL_TYPE4 = "type4";

    public static final String DB_TABLE_PROD = "product";
    public static final String PROD_COL_PK = "p_id";
    public static final String PROD_COL_UPC = "upc";
    public static final String PROD_COL_NAME = "product_name";
    public static final String PROD_COL_TYPE = "p_type";
    public static final String PROD_COL_CARB = "Total_Carbohydrate";
    public static final String PROD_COL_PROT = "Protein";
    public static final String PROD_COL_FAT = "Total_Fat";
    public static final String PROD_COL_CAL = "Calories";
    public static final String PROD_COL_DATE_FK_ID = "p_date_id";
    public static boolean debug = false;

    private String DATABASE_DELETE_PROF = "DROP TABLE IF EXISTS " + DB_TABLE_PROFILE + " ;";
    private String DATABASE_DELETE_PROD = "DROP TABLE IF EXISTS " + DB_TABLE_PROD + " ;";
    private String DATABASE_DELETE_CAL = "DROP TABLE IF EXISTS " + DB_TABLE_CAL + " ;";

    private String DATABASE_CREATE_PROFILE=  "CREATE TABLE " + DB_TABLE_PROFILE +
            " (" +
            PROF_COL_PK + " INTEGER PRIMARY KEY , " +
            PROF_COL_BMR + " FLOAT DEFAULT 2000 ); " ;

    private String DATABASE_CREATE_CAL=  "CREATE TABLE " + DB_TABLE_CAL +
            " (" +
            CAL_COL_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CAL_COL_DATE + " DATETIME DEFAULT CURRENT_DATE, " +
            CAL_COL_TYPE0 + " INTEGER DEFAULT 0, " +
            CAL_COL_TYPE1 + " INTEGER DEFAULT 0, " +
            CAL_COL_TYPE2 + " INTEGER DEFAULT 0, " +
            CAL_COL_TYPE3 + " INTEGER DEFAULT 0, " +
            CAL_COL_TYPE4 + " INTEGER DEFAULT 0 );";

    private String DATABASE_CREATE_PROD =  "CREATE TABLE " + DB_TABLE_PROD +
            " (" +
            PROD_COL_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PROD_COL_UPC + " TEXT NOT NULL, " +
            PROD_COL_TYPE + " INTEGER NOT NULL, " +
            PROD_COL_NAME + " TEXT, " +
            PROD_COL_CARB + " FLOAT DEFAULT 0, "+
            PROD_COL_PROT + " FLOAT DEFAULT 0, "+
            PROD_COL_FAT + " FLOAT DEFAULT 0, "+
            PROD_COL_CAL + " FLOAT DEFAULT 0, "+
            PROD_COL_DATE_FK_ID + " INTEGER, " +
            "FOREIGN KEY("+PROD_COL_DATE_FK_ID+") REFERENCES "+DB_TABLE_CAL+"("+CAL_COL_PK+") ON DELETE CASCADE);";


    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE_PROFILE);
        db.execSQL(DATABASE_CREATE_CAL);
        db.execSQL(DATABASE_CREATE_PROD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DATABASE_DELETE_PROF);
        db.execSQL(DATABASE_DELETE_CAL);
        db.execSQL(DATABASE_DELETE_PROD);
        db.execSQL(DATABASE_CREATE_PROFILE);
        db.execSQL(DATABASE_CREATE_CAL);
        db.execSQL(DATABASE_CREATE_PROD);

        if(debug) {
            createDummyData(db);
        }

        //Log.d("onUpgrade", "---------------database Upgraded");
    }

    private void createDummyData(SQLiteDatabase db) {
        String query = filldate("08","01");
        db.execSQL(query);
        query = filldate("08","02");db.execSQL(query);
        query = filldate("08","03");db.execSQL(query);
        query = filldate("08","04");db.execSQL(query);
        query = filldate("08","05");db.execSQL(query);
        query = filldate("08","06");db.execSQL(query);
        query = filldate("08","07");db.execSQL(query);
        query = filldate("08","08");db.execSQL(query);
        query = filldate("08","09");db.execSQL(query);
        query = filldate("08","10");db.execSQL(query);
        query = filldate("08","11");db.execSQL(query);
        query = filldate("08","12");db.execSQL(query);
        query = filldate("09","10");db.execSQL(query);
        query = filldate("09","11");db.execSQL(query);
        query = filldate("09","12");db.execSQL(query);

        query = fillProd(1,1);
        db.execSQL(query);
        db.execSQL(query);
        query = fillProd(1,2);
        db.execSQL(query);
        query = fillProd(1,4);
        db.execSQL(query);
        query = fillProd(2,0);
        db.execSQL(query);
        query = fillProd(2,2);
        db.execSQL(query);
        query = fillProd(2,3);
        db.execSQL(query);

    }

    private String fillProd(int d,int i) {
        return "INSERT INTO " + DB_TABLE_PROD +
                "(upc, product_name, p_type, Total_Carbohydrate, Protein, Total_Fat, Calories, p_date_id)" +
                " VALUES ('1234567890', 'food', '"+i+"', '10.0', '5.0', '5.0', '10.0' , '"+d+"')";
    }

    private String filldate(String d,String i) {
        return "INSERT INTO " + DB_TABLE_CAL +
                " (c_date, type0, type1, type2, type3, type4  )"+
                " VALUES ( '2015-"+d+"-"+i+"','1','0','1','1','0');";
    }
}
