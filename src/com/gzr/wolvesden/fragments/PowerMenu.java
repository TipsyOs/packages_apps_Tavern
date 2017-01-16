/*
 * Copyright (C) 2014 The Dirty Unicorns Project
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

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.internal.util.validus.ValidusUtils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.gzr.wolvesden.preference.CustomSeekBarPreference;

public class PowerMenu extends SettingsPreferenceFragment implements OnPreferenceChangeListener {
    private static final String KEY_ADVANCED_REBOOT = "advanced_reboot";
    private ListPreference mPowerMenuAnimations;
    private static final String KEY_POWERMENU_TORCH = "powermenu_torch";
    private static final String POWER_REBOOT_DIALOG_DIM = "power_reboot_dialog_dim";
    private static final String POWER_MENU_ANIMATIONS = "power_menu_animations";

    private ListPreference mAdvancedReboot;
    private SwitchPreference mPowermenuTorch;
    private CustomSeekBarPreference mPowerRebootDialogDim;
    private SwitchPreference mOnTheGoPowerMenu;

    private static final String POWER_MENU_ONTHEGO_ENABLED = "power_menu_onthego_enabled";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.powermenu);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mAdvancedReboot = (ListPreference) findPreference(KEY_ADVANCED_REBOOT);
        mAdvancedReboot.setValue(String.valueOf(Settings.Secure.getInt(
                getContentResolver(), Settings.Secure.ADVANCED_REBOOT, 1)));
        mAdvancedReboot.setSummary(mAdvancedReboot.getEntry());
        mAdvancedReboot.setOnPreferenceChangeListener(this);

        mPowermenuTorch = (SwitchPreference) findPreference(KEY_POWERMENU_TORCH);
        mPowermenuTorch.setOnPreferenceChangeListener(this);
        if (!ValidusUtils.deviceSupportsFlashLight(getActivity())) {
            prefScreen.removePreference(mPowermenuTorch);
        } else {
        mPowermenuTorch.setChecked((Settings.System.getInt(resolver,
                Settings.System.POWERMENU_TORCH, 0) == 1));
        }

        mPowerRebootDialogDim = (CustomSeekBarPreference) prefScreen.findPreference(POWER_REBOOT_DIALOG_DIM);
        int powerRebootDialogDim = Settings.System.getInt(resolver,
                Settings.System.POWER_REBOOT_DIALOG_DIM, 50);
        mPowerRebootDialogDim.setValue(powerRebootDialogDim / 1);
        mPowerRebootDialogDim.setOnPreferenceChangeListener(this);

        mPowerMenuAnimations = (ListPreference) findPreference(POWER_MENU_ANIMATIONS);
        mPowerMenuAnimations.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.POWER_MENU_ANIMATIONS, 0)));
        mPowerMenuAnimations.setSummary(mPowerMenuAnimations.getEntry());
        mPowerMenuAnimations.setOnPreferenceChangeListener(this);

        mOnTheGoPowerMenu = (SwitchPreference) findPreference(POWER_MENU_ONTHEGO_ENABLED);
        mOnTheGoPowerMenu.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_ONTHEGO_ENABLED, 0) == 1));
        mOnTheGoPowerMenu.setOnPreferenceChangeListener(this);

    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.WOLVESDEN;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mAdvancedReboot) {
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.ADVANCED_REBOOT,
                    Integer.valueOf((String) newValue));
            mAdvancedReboot.setValue(String.valueOf(newValue));
            mAdvancedReboot.setSummary(mAdvancedReboot.getEntry());
        } else if (preference == mPowermenuTorch) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWERMENU_TORCH, checked ? 1:0);
            return true;
        } else if (preference == mPowerRebootDialogDim) {
            int alpha = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_REBOOT_DIALOG_DIM, alpha * 1);
            return true;
        } else if (preference == mPowerMenuAnimations) {
            Settings.System.putInt(getContentResolver(), Settings.System.POWER_MENU_ANIMATIONS,
                    Integer.valueOf((String) newValue));
            mPowerMenuAnimations.setValue(String.valueOf(newValue));
            mPowerMenuAnimations.setSummary(mPowerMenuAnimations.getEntry());
            return true;
        } else if (preference == mOnTheGoPowerMenu) {
            boolean value = ((Boolean)newValue).booleanValue();
            Settings.System.putInt(getContentResolver(), Settings.System.POWER_MENU_ONTHEGO_ENABLED, value ? 1 : 0);
            return true;
        }
        return false;
    }
}