/*
 * Copyright (C) 2020 shagbag913
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

package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.widget.Switch;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

public class AmbientDisplaySwitchPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin, SwitchBar.OnSwitchChangeListener {

    private static final String KEY = "ambient_display_switch";

    private Context mContext;
    private SwitchBar mSwitch;
    private int mAodState;

    public AmbientDisplaySwitchPreferenceController(Context context) {
        super(context);
        mContext = context;
        mAodState = getAodState();
    }

    @Override
    public String getPreferenceKey() {
        return KEY;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        if (isAvailable()) {
            LayoutPreference pref = screen.findPreference(getPreferenceKey());
            if (pref != null) {
                mSwitch = pref.findViewById(R.id.switch_bar);
                if (mSwitch != null) {
                    mSwitch.addOnSwitchChangeListener(this);
                    mSwitch.show();
                }
            }
        }
    }

    public void setChecked(boolean isChecked) {
        if (mSwitch != null) {
            mSwitch.setChecked(isChecked);
        }
    }

    private int getAodState() {
        int aodState = Settings.Secure.getIntForUser(mContext.getContentResolver(),
                Settings.Secure.DOZE_ALWAYS_ON, 0, UserHandle.USER_CURRENT);
        if (aodState == 0) {
            aodState = Settings.Secure.getIntForUser(mContext.getContentResolver(),
                    Settings.Secure.DOZE_ON_CHARGE, 0, UserHandle.USER_CURRENT) == 1 ? 2 : 0;
        }
        return aodState;
    }

    @Override
    public void updateState(Preference preference) {
        setChecked(mAodState == 0 ? false : true);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        if (!isChecked) {
            Settings.Secure.putIntForUser(mContext.getContentResolver(),
                    Settings.Secure.DOZE_ALWAYS_ON, 0, UserHandle.USER_CURRENT);
            Settings.System.putIntForUser(mContext.getContentResolver(),
                    Settings.Secure.DOZE_ON_CHARGE, 0, UserHandle.USER_CURRENT);
        } else if (isChecked && mAodState == 0) {
            Settings.Secure.putIntForUser(mContext.getContentResolver(),
                    Settings.Secure.DOZE_ALWAYS_ON, 1, UserHandle.USER_CURRENT);
        }
        mAodState = getAodState();
    }
}
