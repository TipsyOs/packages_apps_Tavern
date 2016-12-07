/*
 * Copyright (C) 2010 The Android Open Source Project
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

import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import com.android.settings.R;

    public class Animations extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

        private static final String TAG = "Animations";

        private static final String KEY_LISTVIEW_ANIMATION = "listview_animation";
        private static final String KEY_LISTVIEW_INTERPOLATOR = "listview_interpolator";

        private ListPreference mListViewAnimation;
        private ListPreference mListViewInterpolator;

        private Context mContext;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            createCustomView();
        }

        private PreferenceScreen createCustomView() {
            mContext = getActivity();
            ContentResolver resolver = getActivity().getContentResolver();

            addPreferencesFromResource(R.xml.animations);
            PreferenceScreen prefSet = getPreferenceScreen();

            // ListView Animations
            mListViewAnimation = (ListPreference) prefSet.findPreference(KEY_LISTVIEW_ANIMATION);
            int listviewanimation = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.LISTVIEW_ANIMATION, 0);
            mListViewAnimation.setValue(String.valueOf(listviewanimation));
            mListViewAnimation.setSummary(mListViewAnimation.getEntry());
            mListViewAnimation.setOnPreferenceChangeListener(this);

            mListViewInterpolator = (ListPreference) prefSet.findPreference(KEY_LISTVIEW_INTERPOLATOR);
            int listviewinterpolator = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.LISTVIEW_INTERPOLATOR, 0);
            mListViewInterpolator.setValue(String.valueOf(listviewinterpolator));
            mListViewInterpolator.setSummary(mListViewInterpolator.getEntry());
            mListViewInterpolator.setOnPreferenceChangeListener(this);
            mListViewInterpolator.setEnabled(listviewanimation > 0);

            return prefSet;
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object objValue) {
            final String key = preference.getKey();
            if (KEY_LISTVIEW_ANIMATION.equals(key)) {
                int value = Integer.parseInt((String) objValue);
                int index = mListViewAnimation.findIndexOfValue((String) objValue);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.LISTVIEW_ANIMATION,
                        value);
                mListViewAnimation.setSummary(mListViewAnimation.getEntries()[index]);
                mListViewInterpolator.setEnabled(value > 0);
            }
            if (KEY_LISTVIEW_INTERPOLATOR.equals(key)) {
                int value = Integer.parseInt((String) objValue);
                int index = mListViewInterpolator.findIndexOfValue((String) objValue);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.LISTVIEW_INTERPOLATOR,
                        value);
                mListViewInterpolator.setSummary(mListViewInterpolator.getEntries()[index]);
            }
            return false;
        }
    }
