package com.example.marioviti.checko;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements FragmentSwapper, OnTopDialogLauncher{

    private ViewPager viewPager;
    private final int MENU_FRAG = 0;
    private final int ROOT_FRAG = 1;
    private MenuFragment menuFragment;
    private RootFragment rootFragmnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        FragmentManager fm = getSupportFragmentManager();
        PagerAdapter adapterViewPager = new MyFragmentPagerAdapter(fm);
        viewPager.setAdapter(adapterViewPager);

        Log.d("onCreate", "---------------------------MAIN_ACTIVITY");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "---------------------------MAIN_ACTIVITY");
    }

    /*
    Sistema di interfacciamento per Fragment annidati: La MainActivity riflette la chiamata al RootFragment.
    */
    @Override
    public boolean swapWith(int pos) {
        viewPager.setCurrentItem(ROOT_FRAG);
        if(rootFragmnet==null) {
            rootFragmnet = RootFragment.newInstance( "root_fragment", ROOT_FRAG );
        }
        return ((FragmentSwapper)(rootFragmnet)).swapWith(pos);
    }

    @Override
    public void lauchDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("Dialog Title");
        dialog.setContentView(R.layout.dialog_view);
        TextView text = (TextView)dialog.findViewById(R.id.dialog_text_view);
        text.setText("This is the text in my dialog");
        dialog.show();
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private int fragmentCount = 2;

        public MyFragmentPagerAdapter ( FragmentManager fm ) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case MENU_FRAG: {
                    menuFragment = MenuFragment.newInstance( "menu_fragment", MENU_FRAG );
                    return menuFragment;
                }
                case ROOT_FRAG: {
                    rootFragmnet = RootFragment.newInstance( "root_fragment", ROOT_FRAG );
                    return rootFragmnet;
                }
                default:
                    return null;
            }
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
