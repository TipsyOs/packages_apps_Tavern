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
import com.android.internal.util.crdroid.AwesomeAnimationHelper;

import java.util.ArrayList;

import com.android.settings.R;

    public class Animations extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

        private static final String TAG = "Animations";

        private static final String ACTIVITY_OPEN = "activity_open";
        private static final String ACTIVITY_CLOSE = "activity_close";
        private static final String TASK_OPEN = "task_open";
        private static final String TASK_CLOSE = "task_close";
        private static final String TASK_MOVE_TO_FRONT = "task_move_to_front";
        private static final String TASK_MOVE_TO_BACK = "task_move_to_back";
        private static final String ANIMATION_DURATION = "animation_duration";
        private static final String WALLPAPER_OPEN = "wallpaper_open";
        private static final String WALLPAPER_CLOSE = "wallpaper_close";
        private static final String WALLPAPER_INTRA_OPEN = "wallpaper_intra_open";
        private static final String WALLPAPER_INTRA_CLOSE = "wallpaper_intra_close";
        private static final String TASK_OPEN_BEHIND = "task_open_behind";
        private static final String KEY_LISTVIEW_ANIMATION = "listview_animation";
        private static final String KEY_LISTVIEW_INTERPOLATOR = "listview_interpolator";
        private static final String KEY_TOAST_ANIMATION = "toast_animation";
        private static final String SCROLLINGCACHE_PREF = "pref_scrollingcache";
        private static final String SCROLLINGCACHE_PERSIST_PROP = "persist.sys.scrollingcache";
        private static final String SCROLLINGCACHE_DEFAULT = "1";

        ListPreference mActivityOpenPref;
        ListPreference mActivityClosePref;
        ListPreference mTaskOpenPref;
        ListPreference mTaskClosePref;
        ListPreference mTaskMoveToFrontPref;
        ListPreference mTaskMoveToBackPref;
        ListPreference mWallpaperOpen;
        ListPreference mWallpaperClose;
        ListPreference mWallpaperIntraOpen;
        ListPreference mWallpaperIntraClose;
        ListPreference mTaskOpenBehind;

        private int[] mAnimations;
        private String[] mAnimationsStrings;
        private String[] mAnimationsNum;
        private ListPreference mListViewAnimation;
        private ListPreference mListViewInterpolator;
        private ListPreference mToastAnimation;
        private ListPreference mScrollingCachePref;
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

            // System Animations
            mAnimations = AwesomeAnimationHelper.getAnimationsList();
            int animqty = mAnimations.length;
            mAnimationsStrings = new String[animqty];
            mAnimationsNum = new String[animqty];
            for (int i = 0; i < animqty; i++) {
                mAnimationsStrings[i] = AwesomeAnimationHelper.getProperName(getActivity().getApplicationContext(), mAnimations[i]);
                mAnimationsNum[i] = String.valueOf(mAnimations[i]);
            }

            mActivityOpenPref = (ListPreference) findPreference(ACTIVITY_OPEN);
            mActivityOpenPref.setOnPreferenceChangeListener(this);
            mActivityOpenPref.setSummary(getProperSummary(mActivityOpenPref));
            mActivityOpenPref.setEntries(mAnimationsStrings);
            mActivityOpenPref.setEntryValues(mAnimationsNum);

            mActivityClosePref = (ListPreference) findPreference(ACTIVITY_CLOSE);
            mActivityClosePref.setOnPreferenceChangeListener(this);
            mActivityClosePref.setSummary(getProperSummary(mActivityClosePref));
            mActivityClosePref.setEntries(mAnimationsStrings);
            mActivityClosePref.setEntryValues(mAnimationsNum);

            mTaskOpenPref = (ListPreference) findPreference(TASK_OPEN);
            mTaskOpenPref.setOnPreferenceChangeListener(this);
            mTaskOpenPref.setSummary(getProperSummary(mTaskOpenPref));
            mTaskOpenPref.setEntries(mAnimationsStrings);
            mTaskOpenPref.setEntryValues(mAnimationsNum);

            mTaskClosePref = (ListPreference) findPreference(TASK_CLOSE);
            mTaskClosePref.setOnPreferenceChangeListener(this);
            mTaskClosePref.setSummary(getProperSummary(mTaskClosePref));
            mTaskClosePref.setEntries(mAnimationsStrings);
            mTaskClosePref.setEntryValues(mAnimationsNum);

            mTaskMoveToFrontPref = (ListPreference) findPreference(TASK_MOVE_TO_FRONT);
            mTaskMoveToFrontPref.setOnPreferenceChangeListener(this);
            mTaskMoveToFrontPref.setSummary(getProperSummary(mTaskMoveToFrontPref));
            mTaskMoveToFrontPref.setEntries(mAnimationsStrings);
            mTaskMoveToFrontPref.setEntryValues(mAnimationsNum);

            mTaskMoveToBackPref = (ListPreference) findPreference(TASK_MOVE_TO_BACK);
            mTaskMoveToBackPref.setOnPreferenceChangeListener(this);
            mTaskMoveToBackPref.setSummary(getProperSummary(mTaskMoveToBackPref));
            mTaskMoveToBackPref.setEntries(mAnimationsStrings);
            mTaskMoveToBackPref.setEntryValues(mAnimationsNum);

            mWallpaperOpen = (ListPreference) findPreference(WALLPAPER_OPEN);
            mWallpaperOpen.setOnPreferenceChangeListener(this);
            mWallpaperOpen.setSummary(getProperSummary(mWallpaperOpen));
            mWallpaperOpen.setEntries(mAnimationsStrings);
            mWallpaperOpen.setEntryValues(mAnimationsNum);

            mWallpaperClose = (ListPreference) findPreference(WALLPAPER_CLOSE);
            mWallpaperClose.setOnPreferenceChangeListener(this);
            mWallpaperClose.setSummary(getProperSummary(mWallpaperClose));
            mWallpaperClose.setEntries(mAnimationsStrings);
            mWallpaperClose.setEntryValues(mAnimationsNum);

            mWallpaperIntraOpen = (ListPreference) findPreference(WALLPAPER_INTRA_OPEN);
            mWallpaperIntraOpen.setOnPreferenceChangeListener(this);
            mWallpaperIntraOpen.setSummary(getProperSummary(mWallpaperIntraOpen));
            mWallpaperIntraOpen.setEntries(mAnimationsStrings);
            mWallpaperIntraOpen.setEntryValues(mAnimationsNum);

            mWallpaperIntraClose = (ListPreference) findPreference(WALLPAPER_INTRA_CLOSE);
            mWallpaperIntraClose.setOnPreferenceChangeListener(this);
            mWallpaperIntraClose.setSummary(getProperSummary(mWallpaperIntraClose));
            mWallpaperIntraClose.setEntries(mAnimationsStrings);
            mWallpaperIntraClose.setEntryValues(mAnimationsNum);

            mTaskOpenBehind = (ListPreference) findPreference(TASK_OPEN_BEHIND);
            mTaskOpenBehind.setOnPreferenceChangeListener(this);
            mTaskOpenBehind.setSummary(getProperSummary(mTaskOpenBehind));
            mTaskOpenBehind.setEntries(mAnimationsStrings);
            mTaskOpenBehind.setEntryValues(mAnimationsNum);

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

            // Toast Animations
            mToastAnimation = (ListPreference) findPreference(KEY_TOAST_ANIMATION);
            mToastAnimation.setSummary(mToastAnimation.getEntry());
            int CurrentToastAnimation = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.TOAST_ANIMATION, 1);
            mToastAnimation.setValueIndex(CurrentToastAnimation); //set to index of default value
            mToastAnimation.setSummary(mToastAnimation.getEntries()[CurrentToastAnimation]);
            mToastAnimation.setOnPreferenceChangeListener(this);

            // Scrolling cache
            mScrollingCachePref = (ListPreference) prefSet.findPreference(SCROLLINGCACHE_PREF);
            mScrollingCachePref.setValue(SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP,
                    SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP, SCROLLINGCACHE_DEFAULT)));
            mScrollingCachePref.setOnPreferenceChangeListener(this);

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
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            boolean result = false;
            final String key = preference.getKey();
            if (preference == mActivityOpenPref) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[0], val);
            } else if (preference == mActivityClosePref) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[1], val);
            } else if (preference == mTaskOpenPref) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[2], val);
            } else if (preference == mTaskClosePref) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[3], val);
            } else if (preference == mTaskMoveToFrontPref) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[4], val);
            } else if (preference == mTaskMoveToBackPref) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[5], val);
            } else if (preference == mWallpaperOpen) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[6], val);
            } else if (preference == mWallpaperClose) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[7], val);
            } else if (preference == mWallpaperIntraOpen) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[8], val);
            } else if (preference == mWallpaperIntraClose) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[9], val);
            } else if (preference == mTaskOpenBehind) {
                int val = Integer.parseInt((String) newValue);
                result = Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.ACTIVITY_ANIMATION_CONTROLS[10], val);
            } else if (KEY_LISTVIEW_ANIMATION.equals(key)) {
                int value = Integer.parseInt((String) newValue);
                int index = mListViewAnimation.findIndexOfValue((String) newValue);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.LISTVIEW_ANIMATION,
                        value);
                mListViewAnimation.setSummary(mListViewAnimation.getEntries()[index]);
                mListViewInterpolator.setEnabled(value > 0);
                return true;
            } else if (KEY_LISTVIEW_INTERPOLATOR.equals(key)) {
                int value = Integer.parseInt((String) newValue);
                int index = mListViewInterpolator.findIndexOfValue((String) newValue);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.LISTVIEW_INTERPOLATOR,
                        value);
                mListViewInterpolator.setSummary(mListViewInterpolator.getEntries()[index]);
                return true;
            } else if (preference == mToastAnimation) {
                int index = mToastAnimation.findIndexOfValue((String) newValue);
                Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.TOAST_ANIMATION, (String) newValue);
                mToastAnimation.setSummary(mToastAnimation.getEntries()[index]);
                Toast.makeText(mContext, "Toast Test", Toast.LENGTH_SHORT).show();
                return true;
            } else if (preference == mScrollingCachePref) {
                if (newValue != null) {
                    SystemProperties.set(SCROLLINGCACHE_PERSIST_PROP, (String)newValue);
                return true;
                }
                return false;
            }

            // Come here, for the System Animations
            preference.setSummary(getProperSummary(preference));
            return result;
        }

        private String getProperSummary(Preference preference) {
            String mString = "";
            if (preference == mActivityOpenPref) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[0];
            } else if (preference == mActivityClosePref) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[1];
            } else if (preference == mTaskOpenPref) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[2];
            } else if (preference == mTaskClosePref) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[3];
            } else if (preference == mTaskMoveToFrontPref) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[4];
            } else if (preference == mTaskMoveToBackPref) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[5];
            } else if (preference == mWallpaperOpen) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[6];
            } else if (preference == mWallpaperClose) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[7];
            } else if (preference == mWallpaperIntraOpen) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[8];
            } else if (preference == mWallpaperIntraClose) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[9];
            } else if (preference == mTaskOpenBehind) {
                mString = Settings.System.ACTIVITY_ANIMATION_CONTROLS[10];
            }

            int mNum = Settings.System.getInt(getActivity().getContentResolver(), mString, 0);
            return mAnimationsStrings[mNum];
        }

    }
