/*
 * Copyright (C) 2015 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gzr.wolvesden.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;

import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.internal.logging.MetricsProto.MetricsEvent;

public class VisualizerSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW =
            "visualizer_show";
    private static final String PREF_USE_CUSTOM_COLOR =
            "visualizer_use_custom_color";
    private static final String PREF_COLOR =
            "visualizer_color";

    private static final int WHITE = 0xffffffff;
    private static final int TEAL = 0xff009688;

    private SwitchPreference mShow;
    private SwitchPreference mUseCustomColor;
    private ColorPickerPreference mColor;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.visualizer_settings);
        mResolver = getContentResolver();

        final boolean show = Settings.System.getInt(mResolver,
               Settings.Secure.LOCKSCREEN_VISUALIZER_ENABLED, 0) == 1;

        final boolean useCustomColor = Settings.System.getInt(mResolver,
               Settings.System.LOCK_SCREEN_VISUALIZER_USE_CUSTOM_COLOR, 0) == 1;

        mShow = (SwitchPreference) findPreference(PREF_SHOW);
        mShow.setChecked(show);
        mShow.setOnPreferenceChangeListener(this);

        if (show) {
            mUseCustomColor = (SwitchPreference) findPreference(PREF_USE_CUSTOM_COLOR);
            mUseCustomColor.setChecked(useCustomColor);
            mUseCustomColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_USE_CUSTOM_COLOR);
        }

        if (show && useCustomColor) {
            mColor =
                    (ColorPickerPreference) findPreference(PREF_COLOR);
            int intColor = Settings.System.getInt(mResolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR, WHITE);
            mColor.setNewPreviewColor(intColor);
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColor.setSummary(hexColor);
            int DEFAULT = 0xff009688;
            mColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_COLOR);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mShow) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.Secure.LOCKSCREEN_VISUALIZER_ENABLED,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mUseCustomColor) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_USE_CUSTOM_COLOR,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
       return MetricsEvent.WOLVESDEN;
   }
}
