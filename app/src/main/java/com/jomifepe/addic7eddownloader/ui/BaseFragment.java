package com.jomifepe.addic7eddownloader.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.ui.listener.FragmentLoadListener;
import com.jomifepe.addic7eddownloader.util.Util;

public abstract class BaseFragment extends Fragment {
    protected View view;
    private FragmentLoadListener loadListener;
    protected Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentLoadListener) {
            loadListener = (FragmentLoadListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentLoadListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutResourceId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        onCreateViewActions(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.loadListener = null;
    }



    protected abstract void onCreateViewActions(@NonNull LayoutInflater inflater,
                                                @Nullable ViewGroup container,
                                                @Nullable Bundle savedInstanceState);

    protected abstract int getLayoutResourceId();

    public Context getViewContext() {
        return view.getContext();
    }

    protected void onFragmentLoad() {
        if (loadListener != null) {
            loadListener.onFragmentLoad();
        }
    }

    private View getNavigation() throws NullPointerException {
        return getActivity().findViewById(R.id.bnv_main_navigation);
    }

    private View getDecorView() throws NullPointerException {
        return getActivity().getWindow().getDecorView();
    }

    protected void handleException(Exception e, String message) {
        if (isAdded()) {
            longMessage(message);
            Util.Log.logD(view.getContext(), e.getMessage());
        }
    }

    protected void handleException(Exception e, @StringRes int message) {
        if (isAdded()) {
            longMessage(message);
            Util.Log.logD(view.getContext(), e.getMessage());
        }
    }

    private void showMessage(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    private void showMessage(View view, @StringRes int message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    protected void shortMessage(String message) {
        if (isAdded()) {
            try {
                View navigation = getNavigation();
                if (navigation != null) {
                    showMessage(navigation, message, Snackbar.LENGTH_SHORT);
                } else {
                    showMessage(getDecorView(), message, Snackbar.LENGTH_SHORT);
                }
            } catch (Exception e) {
                showMessage(getDecorView(), message, Snackbar.LENGTH_SHORT);
            }
        }
    }

    protected void shortMessage(@StringRes int message) {
        if (isAdded()) {
            shortMessage(getString(message));
        }
    }

    protected void longMessage(String message) {
        if (isAdded()) {
            try {
                View navigation = getNavigation();
                if (navigation != null) {
                    showMessage(navigation, message, Snackbar.LENGTH_LONG);
                } else {
                    showMessage(getDecorView(), message, Snackbar.LENGTH_LONG);
                }
            } catch (Exception e) {
                showMessage(getDecorView(), message, Snackbar.LENGTH_LONG);
            }
        }
    }

    protected void longMessage(@StringRes int message) {
        if (isAdded()) {
            longMessage(getString(message));
        }
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
