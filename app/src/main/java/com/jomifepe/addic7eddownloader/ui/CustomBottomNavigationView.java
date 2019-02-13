package com.jomifepe.addic7eddownloader.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

public class CustomBottomNavigationView
        extends BottomNavigationView
        implements CoordinatorLayout.AttachedBehavior {

    public CustomBottomNavigationView(Context context) {
        super(context);
    }

    public CustomBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return new NavMoveUpwardBehavior();
    }
}
