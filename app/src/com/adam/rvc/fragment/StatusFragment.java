package com.adam.rvc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.adam.rvc.R;
import com.adam.rvc.receiver.StatusUpdateReceiver;

public class StatusFragment extends BaseFragment implements StatusUpdateReceiver.OnStatusUpdated {

    private final StatusUpdateReceiver statusReceiver;

    private TextView statusText;

    public StatusFragment() {
        statusReceiver = new StatusUpdateReceiver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(statusReceiver, statusReceiver.getIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(statusReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        initText(view);
        return view;
    }

    private void initText(View view) {
        statusText = (TextView) view.findViewById(R.id.status_text);
    }

    @Override
    public void onStatusUpdate(String message) {
        statusText.setText(message);
    }

}
