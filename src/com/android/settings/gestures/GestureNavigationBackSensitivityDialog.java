/*
 * Copyright (C) 2019 The Android Open Source Project
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
 * limitations under the License
 */

package com.android.settings.gestures;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.om.IOverlayManager;
import android.os.Bundle;
import android.os.ServiceManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

/**
 * Dialog to set the back gesture's sensitivity in Gesture navigation mode.
 */
public class GestureNavigationBackSensitivityDialog extends InstrumentedDialogFragment {
    private static final String TAG = "GestureNavigationBackSensitivityDialog";
    private static final String KEY_BACK_SENSITIVITY = "back_sensitivity";
    private static final String KEY_PILL = "pill";
    private static final String KEY_BACK_DEAD_Y_ZONE = "back_dead_y_zone";

    public static void show(SystemNavigationGestureSettings parent, int sensitivity, boolean pill,
            int backDeadYZoneMode) {
        if (!parent.isAdded()) {
            return;
        }

        final GestureNavigationBackSensitivityDialog dialog =
                new GestureNavigationBackSensitivityDialog();
        final Bundle bundle = new Bundle();
        bundle.putInt(KEY_BACK_SENSITIVITY, sensitivity);
        bundle.putBoolean(KEY_PILL, pill);
        bundle.putInt(KEY_BACK_DEAD_Y_ZONE, backDeadYZoneMode);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(parent, 0);
        dialog.show(parent.getFragmentManager(), TAG);
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.SETTINGS_GESTURE_NAV_BACK_SENSITIVITY_DLG;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.dialog_back_gesture_sensitivity, null);
        final SeekBar sensitivitySeekBar = view.findViewById(R.id.back_sensitivity_seekbar);
        final Switch pillSwitch = view.findViewById(R.id.pill_switch);
        final SeekBar backDeadzoneSeekbar = view.findViewById(R.id.back_deadzone_seekbar);
        sensitivitySeekBar.setProgress(getArguments().getInt(KEY_BACK_SENSITIVITY));
        pillSwitch.setChecked(getArguments().getBoolean(KEY_PILL));
        backDeadzoneSeekbar.setProgress(getArguments().getInt(KEY_BACK_DEAD_Y_ZONE));
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.back_sensitivity_dialog_title_cust)
                .setMessage(R.string.back_sensitivity_dialog_message_cust)
                .setView(view)
                .setPositiveButton(R.string.okay, (dialog, which) -> {
                    int sensitivity = sensitivitySeekBar.getProgress();
                    boolean pill = pillSwitch.isChecked();
                    getArguments().putInt(KEY_BACK_SENSITIVITY, sensitivity);
                    getArguments().putBoolean(KEY_PILL, pill);
                    SystemNavigationGestureSettings.setBackSensitivity(getActivity(),
                            getOverlayManager(), sensitivity, pill);
                    int backDeadYZoneMode = backDeadzoneSeekbar.getProgress();
                    getArguments().putInt(KEY_BACK_DEAD_Y_ZONE, backDeadYZoneMode);
                    SystemNavigationGestureSettings.setBackDeadYZone(getActivity(),
                            backDeadYZoneMode);
                })
                .create();
    }

    private IOverlayManager getOverlayManager() {
        return IOverlayManager.Stub.asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
    }
}
