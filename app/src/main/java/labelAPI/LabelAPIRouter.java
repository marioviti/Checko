package labelAPI;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import databaseHandling.DBOpenHelper;
import databaseHandling.DBQueryManager;
import databaseHandling.DBTransactionTasko;

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
    public boolean sessionHasStarted = false;
    private static LabelAPIHttpTask httpReqTask;
    LabelAPIServiceCallbacks caller;
    DBOpenHelper dbOpener;

    public LabelAPIRouter(LabelAPIServiceCallbacks caller, DBOpenHelper dbOpener, String APIKEY) {

        LabelAPIprotocol.API_KEY="api_key="+APIKEY;
        this.caller = caller;
        this.dbOpener = dbOpener;
        createSessionStartURL();
    }

    public void createSessionStartURL() {
        try { current_url = new URL(LabelAPIprotocol.URL_BASE+LabelAPIprotocol.URL_COMM_START_SESSION+"?"+LabelAPIprotocol.API_DETAILS+"&"+LabelAPIprotocol.API_KEY);
        }catch (MalformedURLException e){ e.printStackTrace(); Log.d("LabelApiHandler", "------------------------------------------------------URL malformato"); }
    }

    public void createSessionArrayURL(String GTIN) {
        try { current_url = new URL(LabelAPIprotocol.URL_BASE+LabelAPIprotocol.URL_COMM_ARRAY+"?u="+GTIN+"&sid="+this.sessionID+"&n=1&s=0&f=json&"+LabelAPIprotocol.API_KEY);
        }catch (MalformedURLException e){ e.printStackTrace(); Log.d("LabelApiHandler", "------------------------------------------------------URL malformato"); }
    }

    public void startHttpTask (int task) { new LabelAPIHttpTask( current_url, this, task ).execute(); }

    public void startDBTask (ContentValues values, int task) { new DBTransactionTasko( this, this.dbOpener, task ).execute(values); }

    public boolean hasSessionStarted() {
        return sessionHasStarted;
    }

    @Override
    public void manageHttpRes( JSONObject res, int resType ) {

        try {
            // da java 7 si possono fare questi switch case, non lo sapevo
            String errCode = res.getString("result");
            switch(errCode){
                case LabelAPIprotocol.TASK_ERR_CODE_OK:{
                    switchResTask(res,resType);
                    Log.d("manageHttpRes","------------------------------------------------------" + errCode);
                    break;
                }
                case LabelAPIprotocol.TASK_ERR_CODE_NO_RES: {
                    caller.onReceivedNullResponse();
                    Log.d("manageHttpRes", "------------------------------------------------------" + errCode);
                    break;
                }
                case LabelAPIprotocol.TASK_ERR_CODE_NO_CONNECTION: {
                    caller.onHttpConnectionError();
                    Log.d("manageHttpRes", "------------------------------------------------------" + errCode);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void switchResTask (JSONObject res, int resType) {

        ContentValues contentValues;
        switch (resType) {
            case LabelAPIprotocol.SESSION_CREATE_REQ: {
                try {
                    sessionHasStarted = true;
                    JSONObject values = (JSONObject)res.get("values");
                    this.sessionID = values.get("session_id").toString();
                    Log.d("LabelApiHandler", "------------------------------------------------------session_id=" + sessionID);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case LabelAPIprotocol.SESSION_ARRAY_REQ: {
                try {
                    JSONObject product = (JSONObject) ((JSONArray) ((JSONObject)res.get("values")).get("productsArray")).get(0);
                    contentValues = fillValues (product);
                    startDBTask(contentValues,DBQueryManager.INSERT);

                }catch(JSONException e) {
                    e.printStackTrace();
                    Log.d("LabelApiHandler", "------------------------------------------------------PROBLEMI CONNESSIONE ARRAY");
                }
                break;
            }
        }
    }

    private ContentValues fillValues (JSONObject product) {

        ContentValues values = null;
        try {
            values = new ContentValues();
            Log.d("fillValues","-----------------------------------------------------"+product.toString(4));
            JSONArray nutrients = (JSONArray) product.get("nutrients");
            values.put(DBOpenHelper.COL_UPC, product.getString("upc"));
            values.put(DBOpenHelper.COL_NAME, product.getString("product_name"));
            values.put(DBOpenHelper.COL_TYPE,"pescio");
            String nutrient_name;
            for (int i = 0; i < nutrients.length(); i++) {
                nutrient_name = (((JSONObject)nutrients.get(i)).getString("nutrient_name"));
                switch (nutrient_name) {
                    case "Calories":{
                        values.put(DBOpenHelper.COL_CAL,(((JSONObject)nutrients.get(i)).getDouble("nutrient_value")));
                        break;
                    }
                    case "Protein":{
                        values.put(DBOpenHelper.COL_PROT,(((JSONObject)nutrients.get(i)).getDouble("nutrient_value")));
                        break;
                    }
                    case "Total Carbohydrate":{
                        values.put(DBOpenHelper.COL_CARB,(((JSONObject)nutrients.get(i)).getDouble("nutrient_value")));
                        break;
                    }
                    case "Total Fat":{
                        values.put(DBOpenHelper.COL_FAT,(((JSONObject)nutrients.get(i)).getDouble("nutrient_value")));
                        break;
                    }
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    public void manageQueryRes(ContentValues res, int task) {
        if(task == DBQueryManager.INSERT)
            startDBTask(null,DBQueryManager.FETCH);
    }
}
