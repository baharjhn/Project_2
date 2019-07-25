package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;

public class dialog implements Runnable {
    Context activity;
    public dialog(Context activity){
        this.activity = activity;
    }

    @Override
    public void run() {
        Dialog d = new Dialog(activity);
        d.setContentView(R.layout.custom_layout);
        d.show();
    }
}
