/*
 * Copyright (C) 2017 The ABC rom
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
package com.gzr.tavern.fragments;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.provider.Settings;

import com.gzr.tavern.preference.CustomSeekBarPreference;
import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class QsRowsColumns extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private CustomSeekBarPreference mSysuiQqsCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.qs_rows_columns);
        final ContentResolver resolver = getActivity().getContentResolver();

        int value = Settings.Secure.getInt(resolver, Settings.Secure.QQS_COUNT, 6);
        mSysuiQqsCount = (CustomSeekBarPreference) findPreference("sysui_qqs_count_key");
        mSysuiQqsCount.setValue(value);
        mSysuiQqsCount.setOnPreferenceChangeListener(this);

    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.TAVERN;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mSysuiQqsCount) {
            int SysuiQqsCount = (Integer) newValue;
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.QQS_COUNT, SysuiQqsCount * 1);
            return true;
        }
        return false;
    }
}
