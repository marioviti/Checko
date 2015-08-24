package labelAPI;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by marioviti on 22/08/15.
 */
public class LabelAPIHttpTask extends AsyncTask<String,String,JSONObject> {

    private LabelAPIInterface caller;
    private URL url;
    private volatile int status;
    private int taskType;
    private int taskID;

    public LabelAPIHttpTask(URL url, LabelAPIInterface caller, int taskType) {

        this.taskID = LabelAPIprotocol.getIncTaskID();
        this.caller = caller;
        this.status = LabelAPIprotocol.TASK_STARTED;
        this.taskType = taskType;
        this.url = url;
    }

    JSONObject elaborateReq (URL url) throws java.net.ConnectException{

        Log.d("LabelAPIHttpTask", "------------------------------URL: "+ url.toString());
        InputStream is = null;
        JSONObject jsonObject = null;
        BufferedReader r;
        StringBuilder total = null;
        String temp = "";

        try {
            URLConnection conn = url.openConnection();
            Map mp = conn.getHeaderFields();
            Object[] ks = conn.getHeaderFields().keySet().toArray();
            //List<String> immutablelist = Collections.unmodifiableList((List<String>) mp.get(ks[0]));
            String RET_CODE = mp.get(ks[0]).toString();
            Log.d("LabelAPIHttpTask", "------------------------------HTTP: "+ ks[0] + " -> " + RET_CODE);

            if(!RET_CODE.contains("200")){
                Log.d("LabelAPIHttpTask", "------------------------------ERRORE CONNESSIONE");
                throw new java.net.ConnectException();
            }

            is = (InputStream) conn.getContent();
            r =  new BufferedReader(new InputStreamReader(is));
            total = new StringBuilder();
            while((temp = r.readLine())!=null) {
                total.append(temp);
            }
            jsonObject = new JSONObject(total.toString());
        }
        catch ( JSONException e ) {
            e.printStackTrace(); status = LabelAPIprotocol.TASK_ERROR; LabelAPIprotocol.ERR_STACK.putString("LabelAPIHttpReq_"+taskID, "JSONException");
        }
        catch ( IOException e ) {
            e.printStackTrace(); status = LabelAPIprotocol.TASK_ERROR; LabelAPIprotocol.ERR_STACK.putString("LabelAPIHttpReq_"+taskID, "IOException");
        }

        if(status ==  LabelAPIprotocol.TASK_STARTED)
            this.status = LabelAPIprotocol.TASK_ALL_DONE;

        return jsonObject;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject res = new JSONObject();
        try {
            JSONObject app = elaborateReq(this.url);
            if(app!=null){
                res.put("result",LabelAPIprotocol.TASK_ERR_CODE_OK);
                res.put("values",app);
            } else {
                res.put("result", LabelAPIprotocol.TASK_ERR_CODE_NO_RES);
            }

        }catch ( java.net.ConnectException e1) {
            try {
                res.put("result",LabelAPIprotocol.TASK_ERR_CODE_NO_CONNECTION);
            }
            catch ( JSONException eIn2) {
                eIn2.printStackTrace();
            }
        }
        catch ( JSONException e2) {
            e2.printStackTrace();
        }
        return res;
    }

    @Override
    protected void onPostExecute(JSONObject result) { caller.manageHttpRes(result, taskType); }

}
