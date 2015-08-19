package com.example.marioviti.checko;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements FragmentSwapper{

    private FragmentPool fgtPool;
    private ViewPager viewPager;
    private final int MENU_FRAG = 0;
    private final int ROOT_FRAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fgtPool = new FragmentPool(2);
        fgtPool.insertFragment(new MenuFragment());
        fgtPool.insertFragment(new RootFragment());

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        FragmentManager fm = getSupportFragmentManager();
        PagerAdapter adapterViewPager = new MyFragmentPagerAdapter(fm);
        viewPager.setAdapter(adapterViewPager);
    }

    /*
    Sistema di interfacciamento per Fragment annidati: La MainActivity riflette la chiamata al RootFragment.
    */
    @Override
    public boolean swapWith(int pos) {
        viewPager.setCurrentItem(ROOT_FRAG);
        return ((FragmentSwapper)(fgtPool.getAt(ROOT_FRAG))).swapWith(pos);
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private int fragmentCount = 2;

        public MyFragmentPagerAdapter ( FragmentManager fm ) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case MENU_FRAG:
                    return fgtPool.getAt(MENU_FRAG);
                case ROOT_FRAG:
                    return fgtPool.getAt(ROOT_FRAG);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragmentCount;
        }

    }
}
