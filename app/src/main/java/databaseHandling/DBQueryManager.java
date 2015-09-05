package databaseHandling;

import android.content.ContentValues;

/**
 * Created by marioviti on 24/08/15.
 */
public interface DBQueryManager {

    int INSERT = 0;
    int REFRESH_FETCH_SYNC = 2;
    int NEW_DATE = 3;
    void manageQueryRes(Object res, int task);
}
