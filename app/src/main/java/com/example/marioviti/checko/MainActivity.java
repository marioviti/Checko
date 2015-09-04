package com.example.marioviti.checko;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import Support.SupporHolder;
import databaseHandling.DBOpenHelper;
import databaseHandling.DBQueryManager;
import labelAPI.LabelAPIRouter;
import labelAPI.LabelAPIServiceCallbacks;
import labelAPI.LabelAPIHolder;


/**
 *  OVERVIEW: MainActivity, sottoclasse della AppCompatActivity, implementa le interfacce che contengono le callback ai vari fragment annidati
 */

public class MainActivity extends AppCompatActivity implements FragmentSwapper, OnTopDialogLauncher, View.OnClickListener, LabelAPIServiceCallbacks{

    private ViewPager viewPager;
    private MyFragmentPagerAdapter pagerAdapter;
    private static int PAG_NUM = 3;
    private LabelAPIRouter labelAPIroute;
    private Dialog mainDialog, tut1dialog, tut2dialog;
    private DBOpenHelper myOpenHelper;

    /**
     * OVERVIEW: Creazione/ripristino del database con l'uso dell'helper.
     * Creazione della sessione per l'accesso al servizio LabelAPI.
     *
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

        // CONNECTION SESSION
        myOpenHelper.debug = true;
        myOpenHelper = new DBOpenHelper(MainActivity.this, DBOpenHelper.DB_NAME, null, DBOpenHelper.DB_V);
        labelAPIroute = new LabelAPIRouter(this, myOpenHelper, "mc896havn4wp7rf73yu5sxxs");
        mainDialog = new Dialog(MainActivity.this);

        // RESTORE STATE
        if (savedInstanceState == null) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            //Log.d("onCreate", "---------------------------FIRST ACCESS METRICS: " + metrics.toString());

        }else {

            SupporHolder.latestDay = savedInstanceState.getString("latestDay");
            SupporHolder.latestDayID = savedInstanceState.getInt("latestDateID");
            SupporHolder.currentDay = savedInstanceState.getString("currentDay");
            SupporHolder.currentDayID = savedInstanceState.getInt("currentDayID");
            SupporHolder.currentCacheDayID = savedInstanceState.getInt("currentCacheDayID");
            SupporHolder.lastCacheDayID = savedInstanceState.getInt("lastCacheDayID");
            SupporHolder.currentPage = savedInstanceState.getInt("currentPage");
        }

        labelAPIroute.sync(); /** updateUI() viene chiamato come callback dopo il labelAPIroute.sync() */
        //Log.d("onCreate", "---------------------------MAIN_ACTIVITY");
    }

    /**
     * usato per il refresh delle date
     */
    public void sync() {
        CalendarFragment.clearCalendar();
        labelAPIroute.sync();
    }

    /**
     * updateUI() viene chiamato come callback dopo il sync
     */
    private void updateUI() {

        if(viewPager==null) {

            viewPager = (ViewPager) findViewById(R.id.view_pager);
        }
        FragmentManager fm = getSupportFragmentManager();
        pagerAdapter = new MyFragmentPagerAdapter(fm,PAG_NUM);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(PAG_NUM);
        viewPager.setCurrentItem(SupporHolder.currentPage, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Log.d("onConfigurationChanged", "---------------------------MAIN_ACTIVITY_ORIENTATION_LANDSCAPE: " + newConfig.toString());
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Log.d("onConfigurationChanged", "---------------------------MAIN_ACTIVITY_ORIENTATION_PORTRAIT: " + newConfig.toString());
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("latestDay", SupporHolder.latestDay);
        savedInstanceState.putInt("latestDateID", SupporHolder.latestDayID);
        savedInstanceState.putString("currentDay", SupporHolder.currentDay);
        savedInstanceState.putInt("currentDayID", SupporHolder.currentDayID);
        savedInstanceState.putInt("currentCacheDayID", SupporHolder.currentCacheDayID);
        savedInstanceState.putInt("lastCacheDayID", SupporHolder.lastCacheDayID);
        savedInstanceState.putInt("currentPage", SupporHolder.currentPage);

        //Log.d("onSaveInstanceState", "---------------------------MAIN_ACTIVITY");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mani_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        //Log.d("onDestroy", "---------------------------MAIN_ACTIVITY");
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
            labelAPIroute.startHttpTask(LabelAPIHolder.SESSION_ARRAY_REQ);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Errore scansione, nessun dato ricevuto", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * OVERVIEW: metodo di gestione del Dialog. Lancia in backgorund la eichiesta di una nuova sessione
     * se non è stata creata.
     *
     * @param type
     */
    @Override
    public void lauchDialog(int type) {

        if (!labelAPIroute.hasSessionStarted())
            labelAPIroute.startHttpTask(LabelAPIHolder.SESSION_CREATE_REQ);

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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case(R.id.dialog_button): {

                String GTIN = ((EditText) this.mainDialog.findViewById(R.id.dialog_editText)).getText().toString();
                labelAPIroute.createSessionArrayURL(GTIN);
                labelAPIroute.startHttpTask(LabelAPIHolder.SESSION_ARRAY_REQ);
                break;
            }
            case(R.id.dialog_scan_button): {

                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
                break;
            }
            case (R.id.tutorial_textView_dismiss): {
                tut1dialog.dismiss();
                tut2dialog = new Dialog(MainActivity.this);
                tut2dialog.setContentView(R.layout.tutorial_layout_2);
                tut2dialog.setTitle("I sommari");
                tut2dialog.show();
                TextView capito = (TextView)tut2dialog.findViewById(R.id.tutorial_textView_dismiss2);
                capito.setOnClickListener(this);

                break;
            }
            case (R.id.tutorial_textView_dismiss2): {
                tut2dialog.dismiss();
                break;
            }
        }
    }

    /**
     * Overview: Callback fornita per il LabelAPIRouter.
     * Chiamata al ritorno dal task di refresh del SupporHolder.
     */
    @Override
    public void onRefreshedData(int pos, int task) {

        if(pos!=-1) {

            if(task== DBQueryManager.NEW_DATE) {
                updateUI();
            }else {
                RootFragment.swapInnerFragmentWith(pos, false);
                CalendarFragment.updateDayEntry(SupporHolder.currentCacheDayID);
            }
        }
    }

    @Override
    public void onSync() {
        updateUI();
    }

    @Override
    public void onFirstAccess() {

        tut1dialog = new Dialog(MainActivity.this);
        tut1dialog.setTitle("Benvenuto!");
        tut1dialog.setContentView(R.layout.tutorial_layout);
        TextView capito = (TextView)tut1dialog.findViewById(R.id.tutorial_textView_dismiss);
        capito.setOnClickListener(this);
        tut1dialog.show();
    }

    /**
     * Overview: Callback fornita per il LabelAPIRouter.
     * Chiamata al ritorno dal task di connessione del client HTTP.
     * Il task ha ricevuto dati che possono essere visualizzati.
     */
    @Override
    public void onReceivedData() {

        this.mainDialog.dismiss();
        viewPager.setCurrentItem(SupporHolder.ROOT_FRAG);
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
     * Il task HTTP ha ricevuto un errore <= codice risposta!= 200, OK
     */
    @Override
    public void onHttpConnectionError() {

        TextView text = (TextView) this.mainDialog.findViewById(R.id.dialog_text_view);
        if(text!=null)
            text.setText("Errore Connessione non presente");
    }

    @Override
    public void onSessionExpired() {

        labelAPIroute.startHttpTask(LabelAPIHolder.SESSION_CREATE_REQ);
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
    public boolean swapWith(int pos,boolean withScroll) {

        if(withScroll)
            viewPager.setCurrentItem(SupporHolder.ROOT_FRAG);
        return RootFragment.swapInnerFragmentWith(pos, withScroll);

    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public FragmentManager fragmentManager;
        private int fragmentCount;

        public MyFragmentPagerAdapter (FragmentManager fm,int pagNum) {

            super(fm);
            this.fragmentManager = fm;
            this.fragmentCount = pagNum;
        }

        @Override
        public Fragment getItem(int pos) {

            Fragment f = null;
            switch (pos) {
                case SupporHolder.MENU_FRAG: {
                    f = MenuFragment.newInstance("menu_fragment", SupporHolder.MENU_FRAG);
                    break;
                }
                case SupporHolder.ROOT_FRAG: {
                    f = RootFragment.newInstance("root_fragment", SupporHolder.ROOT_FRAG);
                    break;
                }
                case SupporHolder.CALEDAR_FRAG: {
                    f = CalendarFragment.newInstance("calendar_fragment", SupporHolder.CALEDAR_FRAG);
                    break;
                }
            }
            return f;
        }

        @Override
        public int getCount() {
            return fragmentCount;
        }
    }
}
