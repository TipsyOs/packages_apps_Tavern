/*
 * Copyright (C) 2016 The Pure Nexus Project
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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class CarrierLabelSettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String SHOW_CARRIER_LABEL = "status_bar_show_carrier";
    private static final String CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String STATUS_BAR_CARRIER_FONT_STYLE = "status_bar_carrier_font_style";
    private static final String STATUS_BAR_CARRIER_COLOR = "status_bar_carrier_color";

    static final int DEFAULT_STATUS_CARRIER_COLOR = 0xffffffff;

    private PreferenceScreen mCustomCarrierLabel;
    private ListPreference mShowCarrierLabel;
    private String mCustomCarrierLabelText;
    private ListPreference mStatusBarCarrierFontStyle;	
    private ColorPickerPreference mCarrierColorPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.carrierlabel_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mShowCarrierLabel = (ListPreference) findPreference(SHOW_CARRIER_LABEL);
        int showCarrierLabel = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_SHOW_CARRIER, 1);
        mShowCarrierLabel.setValue(String.valueOf(showCarrierLabel));
        mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntry());
        mShowCarrierLabel.setOnPreferenceChangeListener(this);

        mCustomCarrierLabel = (PreferenceScreen) findPreference(CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();

        mStatusBarCarrierFontStyle = (ListPreference) findPreference(STATUS_BAR_CARRIER_FONT_STYLE);
        mStatusBarCarrierFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarCarrierFontStyle.setValue(Integer.toString(Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CARRIER_FONT_STYLE, 0)));
        mStatusBarCarrierFontStyle.setSummary(mStatusBarCarrierFontStyle.getEntry());

        mCarrierColorPicker = (ColorPickerPreference) findPreference(STATUS_BAR_CARRIER_COLOR);
        mCarrierColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER_COLOR, DEFAULT_STATUS_CARRIER_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCarrierColorPicker.setSummary(hexColor);
        mCarrierColorPicker.setNewPreviewColor(intColor);

    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.TAVERN;
    }

    private void updateCustomLabelTextSummary() {
        mCustomCarrierLabelText = Settings.System.getString(
            getContentResolver(), Settings.System.CUSTOM_CARRIER_LABEL);

        if (TextUtils.isEmpty(mCustomCarrierLabelText)) {
            mCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomCarrierLabel.setSummary(mCustomCarrierLabelText);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
	ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mShowCarrierLabel) {
            int showCarrierLabel = Integer.valueOf((String) newValue);
            int index = mShowCarrierLabel.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.
                STATUS_BAR_SHOW_CARRIER, showCarrierLabel);
            mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarCarrierFontStyle) {
                int val = Integer.parseInt((String) newValue);
                int index = mStatusBarCarrierFontStyle.findIndexOfValue((String) newValue);
                Settings.System.putInt(resolver,
                        Settings.System.STATUS_BAR_CARRIER_FONT_STYLE, val);
                mStatusBarCarrierFontStyle.setSummary(mStatusBarCarrierFontStyle.getEntries()[index]);
                return true;
        } else if (preference == mCarrierColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER_COLOR, intHex);
            return true;
         }
         return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference.getKey().equals(CUSTOM_CARRIER_LABEL)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(TextUtils.isEmpty(mCustomCarrierLabelText) ? "" : mCustomCarrierLabelText);
            input.setSelection(input.getText().length());
            alert.setView(input);
            alert.setPositiveButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString().trim();
                            Settings.System.putString(resolver, Settings.System.CUSTOM_CARRIER_LABEL, value);
                            updateCustomLabelTextSummary();
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_CUSTOM_CARRIER_LABEL_CHANGED);
                            getActivity().sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
        }
        return super.onPreferenceTreeClick(preference);
    }
}
