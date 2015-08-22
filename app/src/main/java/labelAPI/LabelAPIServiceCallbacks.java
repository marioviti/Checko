package labelAPI;

/**
 * Created by marioviti on 22/08/15.
 */
public interface LabelAPIServiceCallbacks {

    public void onReceivedNullResponse();

    public void onSessionExpired();

    public void onHttpConnectionError();
}
