package com.example.marioviti.checko;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import Support.SupporHolder;
import databaseHandling.DBOpenHelper;
import labelAPI.LabelAPIRouter;
import labelAPI.LabelAPIServiceCallbacks;
import labelAPI.LabelAPIProtocol;


/**
 *  OVERVIEW: MainActivity, sottoclasse della AppCompatActivity, implementa le interfacce che contengono le callback ai vari fragment annidati
 */
public class MainActivity extends AppCompatActivity implements FragmentSwapper, OnTopDialogLauncher, View.OnClickListener, LabelAPIServiceCallbacks{

    private ViewPager viewPager;
    private static final int MENU_FRAG = 0;
    private static final int ROOT_FRAG = 1;
    private static final int CALEDAR_FRAG = 2;
    private static int PAG_NUM = 3;
    private static SolidFragmentPool fgtPool;
    private LabelAPIRouter labelAPIroute;
    private Dialog mainDialog;

    /**
     * OVERVIEW: Creazione/ripristino del database con l'uso dell'helper.
     * Creazione della sessione per l'accesso al servizio LabelAPI.
     * Inizializza la Pool di Fragment.
     * Restore dello stato utilizzando le variabili di stato primitive nel Bundle.
     *
     * MODIFIES: SupportHolder via LabelAPIRouter
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBOpenHelper myOpenHelper = new DBOpenHelper(MainActivity.this, DBOpenHelper.DB_NAME, null, DBOpenHelper.DB_V);

        //CONNECTION SESSION
        labelAPIroute = new LabelAPIRouter(this, myOpenHelper, "mc896havn4wp7rf73yu5sxxs");
        mainDialog = new Dialog(MainActivity.this);

        //RESTORE STATE
        if (savedInstanceState == null) {
            SupporHolder.si = null;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            Log.d("onCreate", "---------------------------FIRST ACCESS METRICS: " + metrics.toString());

        }else {
            SupporHolder.latestDay = savedInstanceState.getString("latestDay");
            SupporHolder.latestDayID = savedInstanceState.getInt("latestDateID");
            SupporHolder.currentDay = savedInstanceState.getString("currentDay");
            SupporHolder.currentDayID = savedInstanceState.getInt("currentDayID");
            SupporHolder.currentChaceDayID = savedInstanceState.getInt("currentChaceDayID");
        }

        labelAPIroute.sync();

        //FRAGMENT SESSION
        initiatePool();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentManager fm = getSupportFragmentManager();
        PagerAdapter adapterViewPager = new MyFragmentPagerAdapter(fm);
        viewPager.setAdapter(adapterViewPager);

        Log.d("onCreate", "---------------------------MAIN_ACTIVITY");
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

        savedInstanceState.putString("latestDay", SupporHolder.latestDay);
        savedInstanceState.putInt("latestDateID", SupporHolder.latestDayID);
        savedInstanceState.putString("currentDay", SupporHolder.currentDay);
        savedInstanceState.putInt("currentDayID", SupporHolder.currentDayID);
        savedInstanceState.putInt("currentChaceDayID", SupporHolder.currentChaceDayID);

        Log.d("onSaveInstanceState", "---------------------------MAIN_ACTIVITY");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d("onDestroy", "---------------------------MAIN_ACTIVITY");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {

            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            String GTIN = scanContent;
            labelAPIroute.createSessionArrayURL(GTIN);
            labelAPIroute.startHttpTask(LabelAPIProtocol.SESSION_ARRAY_REQ);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Errore scansione, nessun dato ricevuto", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    // OnTopDialogLauncher: gestione fragments annidiati: callback per visualizzazione dialog onTop
    @Override
    public void lauchDialog(int type) {

        if (!labelAPIroute.hasSessionStarted())
            labelAPIroute.startHttpTask(LabelAPIProtocol.SESSION_CREATE_REQ);

        this.mainDialog.setTitle("Inserisci un nuovo alimento");
        this.mainDialog.setContentView(R.layout.dialog_view);
        TextView text = (TextView)  this.mainDialog.findViewById(R.id.dialog_text_view);
        text.setText("Inserisci il codice maunalmente o scannerizza un codice a barre");
        Button btn1 = (Button)  this.mainDialog.findViewById(R.id.dialog_button);
        Button bt2 = (Button)  this.mainDialog.findViewById(R.id.dialog_scan_button);
        btn1.setOnClickListener(this);
        bt2.setOnClickListener(this);
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
            case(R.id.dialog_scan_button) : {
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
            }
        }
    }

    /**
     * Overview: Callback fornita per il LabelAPIRouter.
     * Chiamata al ritorno dal task di refresh del SupporHolder.
     */
    @Override
    public void onRefreshedData(int pos) {
        if(pos!=-1) {
            Log.d("onRefreshedData", "----------------------------data refreshed");
            ((FragmentSwapper) (fgtPool.getAt(ROOT_FRAG))).swapWith(pos);
            boolean calendarUpdate= ((CalendarFragment)(fgtPool.getAt(CALEDAR_FRAG))).updateDayEntry(SupporHolder.currentChaceDayID);
            if(!calendarUpdate) {
                fgtPool.insertFragmentAtandReturn(CalendarFragment.newInstance("calendar_fragment", CALEDAR_FRAG), CALEDAR_FRAG);
            }
        }
    }

    /**
     * Overview: Callback fornita per il LabelAPIRouter.
     * Chiamata al ritorno dal task di connessione del client HTTP.
     * Il task ha ricevuto dati che possono essere visualizzati.
     */
    @Override
    public void onReceivedData() {
        this.mainDialog.dismiss();
        viewPager.setCurrentItem(ROOT_FRAG);
    }

    /**
     * Overview: Callback fornita per il LabelAPIRouter.
     * Chiamata al ritorno dal task di connessione del client HTTP.
     * Il task NON ha ricevuto dati che possono essere visualizzati.
     */
    @Override
    public void onReceivedNullResponse() {

        TextView text = (TextView) this.mainDialog.findViewById(R.id.dialog_text_view);
        if(text!=null)
            text.setText("Codice non valido, prova un'altro");
    }

    /**
     * Overview: Callback fornita per il LabelAPIRouter.
     * La sessione non è più valida, ne viene richiesta una nuova.
     */
    @Override
    public void onSessionExpired() {
        labelAPIroute.startHttpTask(LabelAPIProtocol.SESSION_CREATE_REQ);
    }

    /**
     * Overview: Callback fornita per il LabelAPIRouter.
     * Il task HTTP ha ricevuto un errore <= codice risposta!= 200, OK
     */
    @Override
    public void onHttpConnectionError() {
        TextView text = (TextView) this.mainDialog.findViewById(R.id.dialog_text_view);
        if(text!=null)
            text.setText("Errore Connessione non presente");
    }

    private Dialog tutorial;
    private int pages = 0;

    @Override
    public void onFirstAccess() {
        tutorial = new Dialog(MainActivity.this);
        tutorial.setTitle("Benvenuto!");
        tutorial.setContentView(R.layout.tutorial_layout);
        TextView text = (TextView)  tutorial.findViewById(R.id.tutorial_textView);
        text.setText("Benvenuto in Chefcheck\n\n AVANTI ->");
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages ++;
                if(pages == 1)
                    ((TextView)v).setText("Per inserire un alimento tenere\npremuto uno dei 5 tasti\n\n FINE");
                if(pages == 2)
                    tutorial.dismiss();
            }
        });
        tutorial.show();
    }



    private static void initiatePool() {

        fgtPool = new SolidFragmentPool(PAG_NUM);
        fgtPool.insertFragment(MenuFragment.newInstance("menu_fragment", MENU_FRAG ));
        fgtPool.insertFragment(RootFragment.newInstance("root_fragment", ROOT_FRAG ));
        fgtPool.insertFragment(CalendarFragment.newInstance("calendar_fragment", CALEDAR_FRAG ));
    }

    /**
     * OVERVIEW: implementazione del metodo fornito dall'interfaccia FragmentSwapper:
     * FragmentSwapper: è un interfaccia usata in cascata, il chiamante che implementa l'interfaccia
     * si interfaccia con una classe che implementa a sua volta l'interfaccia.
     *
     * Metodo di interfacciamento fra ROOT_FRAG e MainActiviy.
     * La MainActiviy comprendendo il pageAdapeter può voltare le pagine mentre il ROOT fragment ne cambia il contenuto.
     */
    @Override
    public boolean swapWith(int pos) {

        viewPager.setCurrentItem(ROOT_FRAG);
        if(fgtPool.getAt(ROOT_FRAG)==null)
            fgtPool.insertFragmentAtandReturn(RootFragment.newInstance("root_fragment", ROOT_FRAG), ROOT_FRAG);

        return ((FragmentSwapper)(fgtPool.getAt(ROOT_FRAG))).swapWith(pos);
    }

    /**
     * OVERVIEW: Sottoclasse del FragmentPagerAdapter. gestione dei fragment ritenuti dall'activity
     * con l'uso della fragmentPool
     */
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
            return fgtPool.getCurr();
        }

        @Override
        public int getCount() {
            return fragmentCount;
        }

    }
}
