package com.adam.rvc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.adam.rvc.R;
import com.adam.rvc.activity.SettingsActivity;
import com.adam.rvc.view.org.holoeverywhere.PopupMenu;

public class ActionBarFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_action_bar, container, false);
        initButton(view);
        return view;
    }

    private void initButton(View view) {
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.options_button);
        imageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        showPopUpMenu(view);
    }

    private void showPopUpMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.settings_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
                startActivity(createSettingsIntent());
                return false;
            }
        });

        popupMenu.show();
    }

    private Intent createSettingsIntent() {
        return new Intent(context, SettingsActivity.class);
    }

}
