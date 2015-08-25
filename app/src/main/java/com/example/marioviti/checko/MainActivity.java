package com.example.marioviti.checko;

import android.app.Dialog;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import databaseHandling.DBOpenHelper;
import labelAPI.LabelAPIRouter;
import labelAPI.LabelAPIServiceCallbacks;
import labelAPI.LabelAPIProtocol;

public class MainActivity extends AppCompatActivity implements FragmentSwapper, OnTopDialogLauncher, View.OnClickListener, LabelAPIServiceCallbacks{

    private ViewPager viewPager;
    private static final int MENU_FRAG = 0;
    private static final int ROOT_FRAG = 1;
    private static final int CALEDAR_FRAG = 2;
    private static int PAG_NUM = 3;
    private static FragmentPool fgtPool;
    private LabelAPIRouter labelAPIroute;
    private Dialog mainDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ( savedInstanceState == null) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            Log.d("onCreate", "---------------------------METRICS: "+metrics.toString());
        }

        //ROUTER SESSION

        //INITIATE DB
        DBOpenHelper  myOpenHelper = new DBOpenHelper (MainActivity.this, DBOpenHelper.DB_NAME, null, DBOpenHelper.DB_V);
        myOpenHelper.onCreate( myOpenHelper.getWritableDatabase());

        //CONNECTION SESSION
        labelAPIroute= new LabelAPIRouter(this,myOpenHelper,"mc896havn4wp7rf73yu5sxxs");
        mainDialog = new Dialog(MainActivity.this);

        //!ROUTER SESSION

        //FRAGMENT SESSION

        initiatePool();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentManager fm = getSupportFragmentManager();
        PagerAdapter adapterViewPager = new MyFragmentPagerAdapter(fm);
        viewPager.setAdapter(adapterViewPager);

        //!FRAGMENT SESSION

        Log.d("onCreate", "---------------------------MAIN_ACTIVITY");
    }

    private static void initiatePool() {

        fgtPool = new FragmentPool(PAG_NUM);
        fgtPool.insertFragment(MenuFragment.newInstance("menu_fragment", MENU_FRAG ));
        fgtPool.insertFragment(RootFragment.newInstance("root_fragment", ROOT_FRAG ));
        fgtPool.insertFragment(CalendarFragment.newInstance("calendar_fragment", CALEDAR_FRAG ));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("onConfigurationChanged", "---------------------------MAIN_ACTIVITY_ORIENTATION_LANDSCAPE: " + newConfig.toString());
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("onConfigurationChanged", "---------------------------MAIN_ACTIVITY_ORIENTATION_PORTRAIT: " + newConfig.toString());
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean("first_start_config"))
            savedInstanceState.putBoolean("first_start_config",false);
        Log.d("onSaveInstanceState", "---------------------------MAIN_ACTIVITY");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d("onDestroy", "---------------------------MAIN_ACTIVITY");
    }

    // FragmentSwapper: Metodo di interfacciamento fra ROOT_FRAG e MainActiviy.
    // La MainActiviy comprendendo il pageAdapeter può voltare le pagine mentre il ROOT fragment ne cambia il contenuto.
    @Override
    public boolean swapWith(int pos) {

        viewPager.setCurrentItem(ROOT_FRAG);
        if(fgtPool.getAt(ROOT_FRAG)==null) {
            return ((FragmentSwapper)fgtPool.insertFragmentAtandReturn(RootFragment.newInstance("root_fragment", ROOT_FRAG), ROOT_FRAG)).swapWith(pos);
        }
        return ((FragmentSwapper)(fgtPool.getAt(ROOT_FRAG))).swapWith(pos);
    }

    // OnTopDialogLauncher: gestione fragments annidiati: callback per visualizzazione dialog onTop
    @Override
    public void lauchDialog() {

        if (!labelAPIroute.hasSessionStarted())
            labelAPIroute.startHttpTask(LabelAPIProtocol.SESSION_CREATE_REQ);

        this.mainDialog.setTitle("Dialog Title");
        this.mainDialog.setContentView(R.layout.dialog_view);
        TextView text = (TextView)  this.mainDialog.findViewById(R.id.dialog_text_view);
        text.setText("This is the text in my dialog");
        Button btn = (Button)  this.mainDialog.findViewById(R.id.dialog_button);
        btn.setOnClickListener(this);
        this.mainDialog.show();
    }

    // INTERFACCIAMENTO: listener per bottone del dialog
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case(R.id.dialog_button) : {

                String GTIN = ((EditText) this.mainDialog.findViewById(R.id.dialog_editText)).getText().toString();
                labelAPIroute.createSessionArrayURL(GTIN);
                labelAPIroute.startHttpTask(LabelAPIProtocol.SESSION_ARRAY_REQ);
                break;
            }
        }
    }

    // LabelAPIServiceCallbacks
    @Override
    public void onReceivedNullResponse() {

        TextView text = (TextView) this.mainDialog.findViewById(R.id.dialog_text_view);
        if(text!=null)
            text.setText("Codice non valido, prova un'altro");

    }

    @Override
    public void onSessionExpired() {

        labelAPIroute.sessionHasStarted=false;
        labelAPIroute.startHttpTask(LabelAPIProtocol.SESSION_CREATE_REQ);
    }

    @Override
    public void onHttpConnectionError() {

        TextView text = (TextView) this.mainDialog.findViewById(R.id.dialog_text_view);
        if(text!=null)
            text.setText("Errore Connessione, connessione non presente");
    }

    // CLASSE PRIVATA: gestione dei fragment ritenuti dall'activity
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private int fragmentCount = PAG_NUM;

        public MyFragmentPagerAdapter ( FragmentManager fm ) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            return fgtPool.getAt(pos);
        }

        public int getItemPosition (Object object) {
            return POSITION_UNCHANGED;
        }

        @Override
        public int getCount() {
            return fragmentCount;
        }

    }
}
