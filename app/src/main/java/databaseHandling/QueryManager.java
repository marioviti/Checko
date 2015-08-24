package databaseHandling;

import android.content.ContentValues;

/**
 * Created by marioviti on 24/08/15.
 */
public interface QueryManager {

    int INSERT = 0;
    int FETCH = 1;

    void manageQuery(ContentValues res, int task);
}
