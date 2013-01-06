package com.rvc.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ComboListener implements ActionListener {

    private final ComboCallback comboCallback;

    public ComboListener(ComboCallback comboCallback) {
        this.comboCallback = comboCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        comboCallback.onSelectionChanged(cb.getSelectedIndex());
    }

    public interface ComboCallback {

        void onSelectionChanged(int index);

    }

}
