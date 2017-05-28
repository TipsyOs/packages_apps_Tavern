/*
 * Copyright (C) 2017 GZR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gzr.tavern.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;

import com.android.settings.R;
import com.gzr.tavern.preference.CustomSeekBarPreference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsProto.MetricsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatusBarLogo extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarLogo";

    private LinearLayout mView;

    private static final String STATUS_BAR_LOGO_POSITION = "status_bar_logo_position";
    private static final String STATUS_BAR_LOGO_STYLE = "status_bar_logo_style";
    private static final String STATUS_BAR_LOGO_LOCATION = "status_bar_logo_location";
    private static final String STATUS_BAR_LOGO_SIZE = "status_bar_logo_size";

    private ListPreference mStatusBarLogoPosition;
    private ListPreference mStatusBarLogoStyle;
    private ListPreference mStatusBarLogoLoc;
    private CustomSeekBarPreference mStatusBarLogoSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_logo);

        ContentResolver resolver = getActivity().getContentResolver();
        mStatusBarLogoPosition = (ListPreference) findPreference(STATUS_BAR_LOGO_POSITION);
        int logoShow = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_TIPSY_LOGO_POSITION, 0,
                UserHandle.USER_CURRENT);
        mStatusBarLogoPosition.setValue(String.valueOf(logoShow));
        mStatusBarLogoPosition.setSummary(mStatusBarLogoPosition.getEntry());
        mStatusBarLogoPosition.setOnPreferenceChangeListener(this);

        mStatusBarLogoStyle = (ListPreference) findPreference(STATUS_BAR_LOGO_STYLE);
        int logoStyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_TIPSY_LOGO_STYLE, 0,
                UserHandle.USER_CURRENT);
        mStatusBarLogoStyle.setValue(String.valueOf(logoStyle));
        mStatusBarLogoStyle.setSummary(mStatusBarLogoStyle.getEntry());
        mStatusBarLogoStyle.setOnPreferenceChangeListener(this);

        mStatusBarLogoLoc = (ListPreference) findPreference(STATUS_BAR_LOGO_LOCATION);
        int logoLoc = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_TIPSY_LOGO_LOCATION, 0,
                UserHandle.USER_CURRENT);
        mStatusBarLogoLoc.setValue(String.valueOf(logoLoc));
        mStatusBarLogoLoc.setSummary(mStatusBarLogoLoc.getEntry());
        mStatusBarLogoLoc.setOnPreferenceChangeListener(this);

        mStatusBarLogoSize = (CustomSeekBarPreference) findPreference(STATUS_BAR_LOGO_SIZE);
        mStatusBarLogoSize.setValue(Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_TIPSY_LOGO_SIZE, 20));
        mStatusBarLogoSize.setOnPreferenceChangeListener(this);

        updateLogoOptions();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarLogoPosition) {
            int logoShow = Integer.valueOf((String) newValue);
            int index = mStatusBarLogoPosition.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                    resolver, Settings.System.STATUS_BAR_TIPSY_LOGO_POSITION, logoShow,
                    UserHandle.USER_CURRENT);
            mStatusBarLogoPosition.setSummary(
                    mStatusBarLogoPosition.getEntries()[index]);
            updateLogoOptions();
            return true;
        } else if (preference == mStatusBarLogoStyle) {
            int logoStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarLogoStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                    resolver, Settings.System.STATUS_BAR_TIPSY_LOGO_STYLE, logoStyle,
                    UserHandle.USER_CURRENT);
            mStatusBarLogoStyle.setSummary(
                    mStatusBarLogoStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarLogoLoc) {
            int logoLoc = Integer.valueOf((String) newValue);
            int index = mStatusBarLogoLoc.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                    resolver, Settings.System.STATUS_BAR_TIPSY_LOGO_LOCATION, logoLoc,
                    UserHandle.USER_CURRENT);
            mStatusBarLogoLoc.setSummary(
                    mStatusBarLogoLoc.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarLogoSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_TIPSY_LOGO_SIZE, width);
            return true;
        }
        return false;
    }

    private void updateLogoOptions() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.STATUS_BAR_TIPSY_LOGO_POSITION, 0) == 0) {
            mStatusBarLogoStyle.setEnabled(false);
            mStatusBarLogoLoc.setEnabled(false);
            mStatusBarLogoSize.setEnabled(false);
        } else {
            mStatusBarLogoStyle.setEnabled(true);
            mStatusBarLogoLoc.setEnabled(true);
            mStatusBarLogoSize.setEnabled(true);

        }
    }

     @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.TAVERN;
    }
}
