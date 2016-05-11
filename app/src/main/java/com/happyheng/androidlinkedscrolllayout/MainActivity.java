package com.happyheng.androidlinkedscrolllayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.happyheng.androidlinkedscrolllayout.fragment.BaseFragment;
import com.happyheng.androidlinkedscrolllayout.layout.LinkedScrollLayout;
import com.happyheng.androidlinkedscrolllayout.layout.adapter.LinkedScrollAdapter;

public class MainActivity extends FragmentActivity {

    private LinkedScrollLayout mLinkedLayout;
    private Adapter mAdapter;

    private static final int FRAGMENT_COUNT = 3;
    private BaseFragment[] mFragments = new BaseFragment[FRAGMENT_COUNT];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLinkedLayout = (LinkedScrollLayout) findViewById(R.id.main_lsl);

        mAdapter = new Adapter(getSupportFragmentManager());
        mLinkedLayout.setAdapter(mAdapter);
    }


    /**
     * LinkedScrollLayout所需的Adapter
     */
    private class Adapter extends LinkedScrollAdapter {

        public Adapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {

            mFragments[position] = new BaseFragment();
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }

        /**
         * 判断指定Fragment是否滑动到顶部的接口
         *
         * @param num
         * @return
         */
        @Override
        public boolean isFragmentTop(int num) {

            return mFragments[num].isTop();
        }
    }
}
