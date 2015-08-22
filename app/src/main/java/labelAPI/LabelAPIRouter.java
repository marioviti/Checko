package labelAPI;

import android.util.Log;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by marioviti on 22/08/15.
 */
public class LabelAPIRouter implements LabelAPIInterface {

    private URL current_url;
    private String sessionID;
    public boolean sessionHasStarted = false;
    private static LabelAPIHttpReq httpReqTask;

    public LabelAPIRouter(String APIKEY) {
        LabelAPIprotocol.API_KEY="api_key="+APIKEY;
        createSessionURL();
    }

    private void createSessionURL() {

        try {
            current_url = new URL(
                    LabelAPIprotocol.URL_BASE+LabelAPIprotocol.URL_COMM_START_SESSION+"?"+LabelAPIprotocol.API_DETAILS+"&"+LabelAPIprotocol.API_KEY);

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

        switch (resType) {
            case LabelAPIprotocol.SESSION_CREATE_REQ: {
                Log.d("LabelApiHandler", res.toString());
                try {
                    sessionHasStarted = true;
                    sessionID = res.get("session_id").toString();
                    Log.d("LabelApiHandler", "------------------------------------------------------session_id=" + sessionID);

                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case LabelAPIprotocol.SESSION_ARRAY_REQ: {
                Log.d("LabelApiHandler",res.toString());
                break;
            }
        }

    }
}
