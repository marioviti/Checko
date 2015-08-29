package databaseHandling;

import android.content.ContentValues;

/**
 * Created by marioviti on 24/08/15.
 */
public interface DBQueryManager {

    int INSERT = 0;
    int REFRESH_FETCH = 1;
    void manageQueryRes(Object res, int task);
}
