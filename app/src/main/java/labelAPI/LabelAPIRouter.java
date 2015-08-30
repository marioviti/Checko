package labelAPI;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import Support.SupporHolder;
import databaseHandling.DBOpenHelper;
import databaseHandling.DBQueryManager;
import databaseHandling.DBTransactionAsyncTask;

/**
 *
 * SESSION CREATE EXAMPLE
 * http://api.foodessentials.com/createsession?uid=demoUID_01&devid=demoDev_01&appid=demoApp_01&f=json&api_key=mc896havn4wp7rf73yu5sxxs
 *
 * ARRAY REQ
 * http://api.foodessentials.com/labelarray?u=
 * 016000264601
 * &sid=bc613ef6-c372-4303-b8e5-1a7a13fe7759&n=10&s=0&f=json&api_key=mc896havn4wp7rf73yu5sxxs
 *
 * Created by marioviti on 21/08/15.
 */

// http://api.foodessentials.com/labelarray?u=016000264601&sid=bc613ef6-c372-4303-b8e5-1a7a13fe7759&n=10&s=0&f=json&api_key=mc896havn4wp7rf73yu5sxxs

public class LabelAPIRouter implements LabelAPIInterface, DBQueryManager {

    private URL current_url;
    private String sessionID;
    private boolean sessionHasStarted = false;
    LabelAPIServiceCallbacks caller;
    DBOpenHelper dbOpener;

    public LabelAPIRouter(LabelAPIServiceCallbacks caller, DBOpenHelper dbOpener, String APIKEY) {

        LabelAPIProtocol.API_KEY="api_key="+APIKEY;
        this.caller = caller;
        this.dbOpener = dbOpener;
        createSessionStartURL();
    }

    public boolean hasSessionStarted() {

        return sessionHasStarted;
    }

    public void createSessionStartURL() {

        try { current_url = new URL(LabelAPIProtocol.URL_BASE+ LabelAPIProtocol.URL_COMM_START_SESSION+"?"+ LabelAPIProtocol.API_DETAILS+"&"+ LabelAPIProtocol.API_KEY);
        }catch (MalformedURLException e){ e.printStackTrace(); Log.d("LabelApiHandler", "------------------------------------------------------URL malformato"); }

    }

    public void createSessionArrayURL(String GTIN) {

        try { current_url = new URL(LabelAPIProtocol.URL_BASE+ LabelAPIProtocol.URL_COMM_ARRAY+"?u="+GTIN+"&sid="+this.sessionID+"&n=1&s=0&f=json&"+ LabelAPIProtocol.API_KEY);
        }catch (MalformedURLException e){ e.printStackTrace(); Log.d("LabelApiHandler", "------------------------------------------------------URL malformato"); }

    }

    public void startHttpTask (int task) {

        if(task == LabelAPIProtocol.SESSION_CREATE_REQ)
            sessionHasStarted = false;
        new LabelAPIHttpTask( current_url, this, task ).execute();
    }

    @Override
    public void manageHttpRes( JSONObject res, int resType ) {

        try {
            String errCode = res.getString("result");
            switch(errCode){
                case LabelAPIProtocol.TASK_ERR_CODE_OK:{
                    switchResTask(res, resType);
                    if(resType!=LabelAPIProtocol.SESSION_CREATE_REQ)
                        caller.onReceivedData();
                    break;
                }
                case LabelAPIProtocol.TASK_ERR_CODE_NO_RES: {
                    caller.onReceivedNullResponse();
                    break;
                }
                case LabelAPIProtocol.TASK_ERR_CODE_NO_CONNECTION: {
                    caller.onHttpConnectionError();
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ContentValues fillValues (JSONObject product) {

        ContentValues values = null;
        try {
            values = new ContentValues();
            JSONArray nutrients = (JSONArray) product.get("nutrients");
            values.put(DBOpenHelper.PROD_COL_UPC, product.getString("upc"));
            values.put(DBOpenHelper.PROD_COL_NAME, product.getString("product_name"));
            values.put(DBOpenHelper.PROD_COL_TYPE, SupporHolder.globalTypeVariable);
            String nutrient_name;
            for (int i = 0; i < nutrients.length(); i++) {
                nutrient_name = (((JSONObject)nutrients.get(i)).getString("nutrient_name"));
                switch (nutrient_name) {
                    case "Calories":{
                        values.put(DBOpenHelper.PROD_COL_CAL,(((JSONObject)nutrients.get(i)).getDouble("nutrient_value")));
                        break;
                    }
                    case "Protein":{
                        values.put(DBOpenHelper.PROD_COL_PROT,(((JSONObject)nutrients.get(i)).getDouble("nutrient_value")));
                        break;
                    }
                    case "Total Carbohydrate":{
                        values.put(DBOpenHelper.PROD_COL_CARB,(((JSONObject)nutrients.get(i)).getDouble("nutrient_value")));
                        break;
                    }
                    case "Total Fat":{
                        values.put(DBOpenHelper.PROD_COL_FAT,(((JSONObject)nutrients.get(i)).getDouble("nutrient_value")));
                        break;
                    }
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return values;
    }

    private void switchResTask (JSONObject res, int resType) {

        ContentValues contentValues;
        switch (resType) {
            case LabelAPIProtocol.SESSION_CREATE_REQ: {
                try {
                    // SESSION STARTED
                    JSONObject values = (JSONObject)res.get("values");
                    this.sessionID = values.get("session_id").toString();
                    this.sessionHasStarted = true;

                    Log.d("LabelApiHandler", "------------------------------------------------------session_id=" + sessionID);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case LabelAPIProtocol.SESSION_ARRAY_REQ: {
                try {
                    JSONObject product = (JSONObject) ((JSONArray) ((JSONObject)res.get("values")).get("productsArray")).get(0);
                    contentValues = fillValues (product);
                    startDBTask(contentValues,DBQueryManager.INSERT);

                }catch(JSONException e) {
                    e.printStackTrace();
                    Log.d("LabelApiHandler", "------------------------------------------------------PROBLEMI RISPOSTA ARRAY");
                }
                break;
            }
        }
    }

    public void startDBTask (ContentValues values, int task) {
        new DBTransactionAsyncTask( this, this.dbOpener, task ).execute(values);
    }

    @Override
    public void manageQueryRes(Object res, int task) {

        if(task == DBQueryManager.INSERT) {
            // refresh data
            startDBTask(null, DBQueryManager.REFRESH_FETCH);
        }
        if(task == DBQueryManager.REFRESH_FETCH) {
            //Signal upper UI using the LabelAPIServiceCallbacks
            caller.onRefreshedData(SupporHolder.globalTypeVariable);
            if(SupporHolder.currentChaceDayID==-1)
                caller.onFirstAccess();
        }
    }

    public void sync() {
        startDBTask(null,DBQueryManager.REFRESH_FETCH);
    }
}
