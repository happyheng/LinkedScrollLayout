package com.happyheng.androidlinkedscrolllayout.layout.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 联动Layout的Adapter
 * Created by liuheng on 16/5/11.
 */
public abstract class LinkedScrollAdapter extends FragmentPagerAdapter{

    public LinkedScrollAdapter(FragmentManager manager){
        super(manager);
    }


    //指定的Fragment是否是滑动到最顶部
    public abstract boolean isFragmentTop(int num);
}
