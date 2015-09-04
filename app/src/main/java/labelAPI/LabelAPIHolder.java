package labelAPI;

import android.os.Bundle;

/**
 * Created by marioviti on 22/08/15.
 */
public class LabelAPIHolder {

        public final static String URL_BASE = "http://api.foodessentials.com/";
        public static String API_KEY;
        public final static String URL_COMM_START_SESSION = "createsession";
        public final static String URL_COMM_ARRAY = "labelarray";
        public final static String API_DETAILS = "uid=demoUID_01&devid=demoDev_01&appid=demoApp_01&f=json";
        public final static int SESSION_CREATE_REQ = 0;
        public final static int SESSION_ARRAY_REQ = 1;
        public final static int TASK_ALL_DONE = 2;
        public final static int TASK_STARTED = 3;
        public final static int TASK_ERROR = 4;
        public final static String TASK_ERR_CODE_OK = "OK";
        public final static String TASK_ERR_CODE_NO_CONNECTION = "NO CONNECTION";
        public final static String TASK_ERR_CODE_NO_RES = "NO RES";

        public static Bundle ERR_STACK = new Bundle();
        private static int taskID = 0;

        synchronized public static int getIncTaskID() {
            return taskID ++;
        }

        public static int getTaskID() {
            return taskID;
        }
}
