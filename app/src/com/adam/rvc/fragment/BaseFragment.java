package com.adam.rvc.fragment;

import android.app.Activity;
import android.content.Context;
import com.actionbarsherlock.app.SherlockFragment;

abstract class BaseFragment extends SherlockFragment {

    protected Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

}
