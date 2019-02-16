package com.jomifepe.addic7eddownloader.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import android.view.MenuItem;

import com.jomifepe.addic7eddownloader.R;

import java.io.File;

public class PreferencesActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, new GeneralPreferenceFragment()).commit();
        }
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static void bindPreferenceSummaryToValue(Preference preference, String defaultValue) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), defaultValue));
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return false;
    }

//    @Override
//    public boolean onIsMultiPane() {
//        return isXLargeTablet(this);
//    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragmentCompat.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat {
        private static final int REQUEST_CODE_OPEN_DIRECTORY = 1;
        private ListPreference prefGeneralDefaultScreen;
        private Preference prefSubtitlePath;
        private Context context;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            this.context = context;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            prefGeneralDefaultScreen = (ListPreference) findPreference(
                    getString(R.string.key_pref_general_default_screen));
            prefSubtitlePath = findPreference(
                    getString(R.string.key_pref_subtitle_save_location));
            prefSubtitlePath.setOnPreferenceClickListener(prefSubtitlePathClick);

            setDefaultPreferences();
        }

        private void setDefaultPreferences() {
            String defaultScreen = context.getResources()
                    .getStringArray(R.array.navigation_sections)[0];
            String downloadsDirectory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

            bindPreferenceSummaryToValue(prefGeneralDefaultScreen, defaultScreen);
            bindPreferenceSummaryToValue(prefSubtitlePath, downloadsDirectory);
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String rootKey) {
            setPreferencesFromResource(R.xml.pref_general, rootKey);
        }

        Preference.OnPreferenceClickListener prefSubtitlePathClick = preference -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY);
            return true;
        };

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == Activity.RESULT_OK) {
                try {
                    String folderPath = getESDirectoryAbsolutePath(data.getData());
                    PreferenceManager.getDefaultSharedPreferences(prefSubtitlePath.getContext())
                            .edit().putString(prefSubtitlePath.getKey(), folderPath).apply();
                    prefSubtitlePath.callChangeListener(folderPath);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        private String getESDirectoryAbsolutePath(Uri uri) throws NullPointerException {
            String[] pathParts = uri.getPath().split(":");
            String externalStoragePath = Environment.getExternalStorageDirectory().toString();
            if (pathParts.length > 1) {
                return externalStoragePath + File.separator + pathParts[1];
            }
            return externalStoragePath;
        }
    }
}
