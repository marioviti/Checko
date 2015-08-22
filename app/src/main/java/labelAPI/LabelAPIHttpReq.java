package labelAPI;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by marioviti on 22/08/15.
 */
public class LabelAPIHttpReq  extends AsyncTask<String,String,JSONObject> {

    private LabelAPIInterface caller;
    private URL url;
    private volatile int status;
    private int taskType;
    private int taskID;

    public LabelAPIHttpReq(URL url, LabelAPIInterface caller , int taskType) {

        this.taskID = LabelAPIprotocol.getIncTaskID();
        this.caller = caller;
        this.status = LabelAPIprotocol.TASK_STARTED;
        this.taskType = taskType;
        this.url = url;
    }

    JSONObject elaborateReq(URL url) {

        InputStream is = null;
        JSONObject jsonObject = null;
        BufferedReader r;
        StringBuilder total = null;
        String temp = "";

        try {

            is = (InputStream) url.getContent();
            r =  new BufferedReader(new InputStreamReader(is));
            total = new StringBuilder();

            while((temp = r.readLine())!=null) {
                total.append(temp);
            }
            jsonObject = new JSONObject(total.toString());
        }
        catch ( JSONException e) {
            //e.printStackTrace();
            status = LabelAPIprotocol.TASK_ERROR;
            LabelAPIprotocol.ERR_STACK.putString("LabelAPIHttpReq_"+taskID, "JSONException");
        }
        catch ( IOException e ) {
            //e.printStackTrace();
            status = LabelAPIprotocol.TASK_ERROR;
            LabelAPIprotocol.ERR_STACK.putString("LabelAPIHttpReq_"+taskID, "IOException");
        }

        if(status ==  LabelAPIprotocol.TASK_STARTED)
            this.status = LabelAPIprotocol.TASK_ALL_DONE;

        return jsonObject;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return elaborateReq(this.url);
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        caller.manageRes(result, taskType);
    }

}
