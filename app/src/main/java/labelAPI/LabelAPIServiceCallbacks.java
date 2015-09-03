package labelAPI;

/**
 * Created by marioviti on 22/08/15.
 */
public interface LabelAPIServiceCallbacks {

    public void onRefreshedData(int pos, int task);

    public void onReceivedData();

    public void onReceivedNullResponse();

    public void onSessionExpired();

    public void onSync();

    public void onFirstAccess();

    public void onHttpConnectionError();
}
