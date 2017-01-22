/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
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

package com.gzr.tavern.tabs;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

public class MultiTasking extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "MultiTasking";

    private static final String KEY_GESTURE_ANYWHERE = "gesture_anywhere";

    private PreferenceScreen mGesturesAnywhere;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.multitasking);

        ContentResolver resolver = getActivity().getContentResolver();

        /* GesturesAnywhere depends on the overlay.
         * config_gesture_settings_enabled is defined in Settings/res/values/config.xml
         * and default is FALSE, devices need to enable overlay
         */
        Resources resources = getResources();
        if (!resources.getBoolean(R.bool.config_gesture_settings_enabled)) {
            PreferenceScreen prefSet = getPreferenceScreen();
                mGesturesAnywhere = (PreferenceScreen)prefSet.findPreference(KEY_GESTURE_ANYWHERE);
                prefSet.removePreference(mGesturesAnywhere);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.TAVERN;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return true;
    }

}

