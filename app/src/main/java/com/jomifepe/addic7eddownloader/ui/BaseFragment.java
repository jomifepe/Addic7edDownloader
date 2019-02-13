package com.jomifepe.addic7eddownloader.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;

import com.jomifepe.addic7eddownloader.ui.listener.FragmentLoadListener;
import com.jomifepe.addic7eddownloader.util.Util;

public abstract class BaseFragment extends Fragment {
    protected View view;
    private FragmentLoadListener loadListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentLoadListener) {
            loadListener = (FragmentLoadListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentLoadListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.loadListener = null;
    }

    public Context getViewContext() {
        return view.getContext();
    }

    protected void onFragmentLoad() {
        if (loadListener != null) {
            loadListener.onFragmentLoad();
        }
    }

    public void setLoadListener(FragmentLoadListener listener) {
        this.loadListener = listener;
    }

    protected void handleException(Exception e, String message) {
        longMessage(message);
        Util.Log.logD(view.getContext(), e.getMessage());
    }

    protected void handleException(Exception e, @StringRes int message) {
        longMessage(message);
        Util.Log.logD(view.getContext(), e.getMessage());
    }

    protected void shortMessage(String message) {
        Snackbar.make(getActivity().getWindow().getDecorView(), message, Snackbar.LENGTH_SHORT).show();
    }

    protected void shortMessage(@StringRes int message) {
        Snackbar.make(getActivity().getWindow().getDecorView(), message, Snackbar.LENGTH_SHORT).show();
    }

    protected void longMessage(String message) {
        Snackbar.make(getActivity().getWindow().getDecorView(), message, Snackbar.LENGTH_LONG).show();
    }

    protected void longMessage(@StringRes int message) {
        Snackbar.make(getActivity().getWindow().getDecorView(), message, Snackbar.LENGTH_LONG).show();
    }

    protected void runOnUiThread(Runnable runnable) {
        try {
            if (isAdded()) {
                getActivity().runOnUiThread(runnable);
            }
        } catch (Exception e) {
            Util.Log.logD(view.getContext(), e.getMessage());
        }
    }
}
