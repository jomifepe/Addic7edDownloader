package com.jomifepe.addic7eddownloader.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jomifepe.addic7eddownloader.util.Util;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);
    }

    protected abstract int getLayoutResourceId();

    protected void handleException(Exception e, @StringRes int message) {
        showMessage(message);
        Util.Log.logD(this, e.getMessage());
    }

    protected void handleException(View view, Exception e, @StringRes int message) {
        showMessage(view, message);
        Util.Log.logD(this, e.getMessage());
    }

    protected void showMessage(String message) {
        Snackbar.make(this.getWindow().getDecorView(), message, Snackbar.LENGTH_LONG).show();
    }

    protected void showMessage(@StringRes int message) {
        Snackbar.make(this.getWindow().getDecorView(), message, Snackbar.LENGTH_LONG).show();
    }

    protected void showMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    protected void showMessage(View view, @StringRes int message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
