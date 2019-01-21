package com.jomifepe.addic7eddownloader.ui;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.jomifepe.addic7eddownloader.util.LogUtil;

public abstract class BaseFragment extends Fragment {
    protected View view;

    protected Context getViewContext() {
        return view.getContext();
    }

    protected void handleException(Exception e, String message) {
        showMessage(message);
        LogUtil.logD(view.getContext(), e.getMessage());
    }

    protected void handleException(Exception e, @StringRes int message) {
        showMessage(message);
        LogUtil.logD(view.getContext(), e.getMessage());
    }

    protected void showMessage(String message) {
        Snackbar.make(getActivity().getWindow().getDecorView(), message, Snackbar.LENGTH_LONG).show();
    }

    protected void showMessage(@StringRes int message) {
        Snackbar.make(getActivity().getWindow().getDecorView(), message, Snackbar.LENGTH_LONG).show();
    }

    protected void runOnUiThread(Runnable runnable) {
        try {
            if (isAdded()) {
                getActivity().runOnUiThread(runnable);
            }
        } catch (Exception e) {
            LogUtil.logD(view.getContext(), e.getMessage());
        }
    }
}
