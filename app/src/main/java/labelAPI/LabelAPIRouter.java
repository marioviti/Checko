package labelAPI;

import android.util.Log;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * SESSION CREATE EXAMPLE
 * http://api.foodessentials.com/createsession?uid=demoUID_01&devid=demoDev_01&appid=demoApp_01&f=json&api_key=mc896havn4wp7rf73yu5sxxs
 *
 * ARRAY REQ
 * http://api.foodessentials.com/labelarray?u=016000264601&sid=bc613ef6-c372-4303-b8e5-1a7a13fe7759&n=10&s=0&f=json&api_key=mc896havn4wp7rf73yu5sxxs
 *
 * Created by marioviti on 21/08/15.
 */

public class LabelAPIRouter implements LabelAPIInterface {

    private URL current_url;
    private String sessionID;
    private boolean sessionHasStarted = false;
    private static LabelAPIHttpReq httpReqTask;
    LabelAPIServiceCallbacks caller;

    public LabelAPIRouter(LabelAPIServiceCallbacks caller, String APIKEY) {
        LabelAPIprotocol.API_KEY="api_key="+APIKEY;
        this.caller = caller;
        createSessionStartURL();
    }

    public void createSessionStartURL() {

        try {
            current_url = new URL(
                    LabelAPIprotocol.URL_BASE+LabelAPIprotocol.URL_COMM_START_SESSION+"?"+LabelAPIprotocol.API_DETAILS+"&"+LabelAPIprotocol.API_KEY);

        }catch (MalformedURLException e){
            e.printStackTrace();
            Log.d("LabelApiHandler", "URL malformato");
        }

    }

    public void createSessionArrayURL(String GTIN) {

        try {
            current_url = new URL(
                    LabelAPIprotocol.URL_BASE+LabelAPIprotocol.URL_COMM_ARRAY+"?u="+GTIN+"&sid="+this.sessionID+"&n=1&s=0&f=json&"+LabelAPIprotocol.API_KEY);

        }catch (MalformedURLException e){
            e.printStackTrace();
            Log.d("LabelApiHandler", "URL malformato");
        }

    }

    public void startTask (int task) {
       new LabelAPIHttpReq( current_url, this, task ).execute();
    }


    //Callback chiamata dal Task a fine esecuzione per la gestione dell'output
    @Override
    public void manageRes( JSONObject res, int resType ) {

        if(res!=null) {

            switch (resType) {
                case LabelAPIprotocol.SESSION_CREATE_REQ: {
                    Log.d("LabelApiHandler", res.toString());
                    try {
                        sessionHasStarted = true;
                        this.sessionID = res.get("session_id").toString();
                        Log.d("LabelApiHandler", "------------------------------------------------------session_id=" + sessionID);

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case LabelAPIprotocol.SESSION_ARRAY_REQ: {
                    Log.d("LabelApiHandler",res.toString());
                    Log.d("LabelApiHandler", "------------------------------------------------------session_id=" + sessionID);
                    break;
                }
            }
        }else {
            caller.onReceivedNullResponse();
        }

    }

    public boolean hasSessionStarted() {
        return sessionHasStarted;
    }
}
