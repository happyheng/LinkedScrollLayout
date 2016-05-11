package com.happyheng.androidlinkedscrolllayout.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.happyheng.androidlinkedscrolllayout.R;

/**
 * 测试的Fragment
 * Created by liuheng on 16/5/9.
 */
public class BaseFragment extends Fragment implements TopListener {

    private ListView mListView;
    private Adapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.framgnt_base, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.main_lv);
        mAdapter = new Adapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean isTop() {

        int position = mListView.getFirstVisiblePosition();
        if (position == 0) {
            View firstView = mListView.getChildAt(0);

            if (firstView.getTop() == 0) {
                return true;
            }
        }


        return false;
    }

    private class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_base_fragment, parent, false);
            }

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }

}
