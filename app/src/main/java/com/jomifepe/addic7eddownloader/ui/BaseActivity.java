package com.jomifepe.addic7eddownloader.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!(this instanceof MainActivity)) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        }
        return super.onKeyLongPress(keyCode, event);
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
