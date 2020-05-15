/*
 * MIT License
 *
 * Copyright (c) 2020 VarTmp7
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vartmp7.stalker.ui.tracking;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.vartmp7.stalker.BuildConfig;
import com.vartmp7.stalker.R;
import com.vartmp7.stalker.StalkerApplication;
import com.vartmp7.stalker.Tools;
import com.vartmp7.stalker.component.StalkerServiceCallback;
import com.vartmp7.stalker.component.StalkerTrackingService;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.injection.components.OrganizationsRepositoryComponent;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public class TrackingFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String PLACE_MSG = "PLACE_MSG";
    private final static int TRACKING_MSG_CODE = 1;
    private final static int TRACKING_STOP_MSG_CODE = 2;
    private final static int TRACKING_INITIALIZING_MSG_CODE = 3;
    private final static int TRACKING_NOT_IN_PLACE_MSG_CODE = 4;
    private final static int TRACKING_UPDATE_TIMER = 5;


    private final static String MSG_CODE = "MSG_CODE";
    private TrackingViewModel trackingViewModel;
    private List<Organization> organizationToTrack;
    private TrackingViewAdapter mAdapter;
    private TextView tvCurrentStatus;
    private Handler handler = new StalkerHandler(this);
    private Chronometer tvTimer;
    private LinearLayout llChrometer;
    private StalkerServiceCallback callback = new StalkerServiceCallback(handler) {
        @Override
        public void stopTracking() {
            handler.sendEmptyMessage(TRACKING_STOP_MSG_CODE);
        }

        @Override
        public void onNewLocation(@NotNull String message) {
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putInt(MSG_CODE, TRACKING_MSG_CODE);
            b.putString(PLACE_MSG, message);
            msg.setData(b);
            handler.sendMessage(msg);

        }

        @Override
        public void notInAnyPlace() {
            handler.sendEmptyMessage(TRACKING_NOT_IN_PLACE_MSG_CODE);
        }
    };

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // A reference to the service used to get location updates.
    private StalkerTrackingService mService = null;

    // booleano per indicare se il service è bound.
    private boolean mBound = false;
    // pulsanti per attivare/ disattivare i tracciamenti .
    private Button mRequestLocationUpdatesButton;
    private Button mRemoveLocationUpdatesButton;

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        long lastPlaceId = sharedPreferences.getLong(StalkerTrackingService.LAST_PLACE_ID, -1);
        if (lastPlaceId != -1) {
            long timeBase = sharedPreferences.getLong(StalkerTrackingService.CHRONOMETER_KEY, -1);
            if (timeBase != -1) {
                tvTimer.setBase(timeBase);
                tvTimer.start();
            }
        }
        if (mService != null)
            mService.setCallback(callback);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mService != null) {
            mService.setCallback(null);
        }
    }

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StalkerTrackingService.LocalBinder binder = (StalkerTrackingService.LocalBinder) service;
            mService = binder.getService();
            mService.requestLocationUpdates();
            mService.updateOrganizations(organizationToTrack);
            mService.setCallback(callback);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService.setCallback(null);
            mService = null;
            mBound = false;
        }
    };


    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle b) {
        root = inflater.inflate(R.layout.fragment_tracking, container, false);

        tvCurrentStatus = root.findViewById(R.id.tvCurrentStatus);
        tvTimer = root.findViewById(R.id.tvTimer);
        llChrometer= root.findViewById(R.id.llChrometer);
        mRequestLocationUpdatesButton = root.findViewById(R.id.btnStartAll);
        mRemoveLocationUpdatesButton = root.findViewById(R.id.btnStopAll);


        RecyclerView recyclerView = root.findViewById(R.id.trackingRecycleView);

        trackingViewModel = new ViewModelProvider(requireActivity()).get(TrackingViewModel.class);
        OrganizationsRepositoryComponent organizationsRepositoryComponent = ((StalkerApplication) getActivity().getApplication()).getOrganizationsRepositoryComponent();
        trackingViewModel.init(organizationsRepositoryComponent.organizationsRepository());
        updateOrganizationToTrack();
        mAdapter = new TrackingViewAdapter(getContext(), trackingViewModel);
        trackingViewModel.getOrganizations().observe(getViewLifecycleOwner(),
                this::onTrackingOrganizationChanged);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Organization o = mAdapter.getOrganizationAt(viewHolder.getAdapterPosition());
                o.setTracking(false);
                o.setTrackingActive(false);
                trackingViewModel.updateOrganization(o);
                mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                Toast.makeText(requireContext(), R.string.organizzazione_rimossa, Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        if (Tools.isUserLogged(requireContext()))
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    Organization o = mAdapter.getOrganizationAt(viewHolder.getAdapterPosition());
                    int msg;
                    if (o.isFavorite()) {
                        trackingViewModel.removeFavorite(o);
                    }else{
                        trackingViewModel.addFavorite(o);
                    }
                    mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());

                    msg = o.isFavorite() ? R.string.organizzazione_added_to_favorite :
                            R.string.organizzazione_removed_from_favorite;


                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }).attachToRecyclerView(recyclerView);
//        else
//            Toast.makeText(requireContext(), R.string.devi_loggarti, Toast.LENGTH_SHORT).show();
        return root;
    }

    private void updateOrganizationToTrack() {
        organizationToTrack = trackingViewModel
                .getOrganizations()
                .getValue()
                .stream()
                .filter(Organization::isTracking)
                .filter(Organization::isTrackingActive)
                .collect(Collectors.toList());
        if (mService != null)
            mService.updateOrganizations(organizationToTrack);
    }

    private void onTrackingOrganizationChanged(@NotNull List<Organization> list) {
        List<Organization> organizationsReadyToTrack = list.stream().filter(Organization::isTracking).collect(Collectors.toList());
        mAdapter.setList(organizationsReadyToTrack);
        organizationToTrack = organizationsReadyToTrack.stream().filter(Organization::isTrackingActive).collect(Collectors.toList());

        if (mService != null) {
            mService.updateOrganizations(organizationToTrack);
        }
        if (organizationsReadyToTrack.stream().anyMatch(Organization::isTrackingActive)){
            llChrometer.setVisibility(View.VISIBLE);
            Tools.setRequestingLocationUpdates(requireContext(), true);
        }
        else{
            llChrometer.setVisibility(View.GONE);
            Tools.setRequestingLocationUpdates(requireContext(), false);
        }

        if (list.stream().anyMatch(Organization::isTrackingActive) && mService == null)
            requireContext().bindService(new Intent(requireContext(), StalkerTrackingService.class), mServiceConnection,
                    Context.BIND_AUTO_CREATE);

        mRemoveLocationUpdatesButton.setEnabled((long) organizationToTrack.size() > 0);
        mRequestLocationUpdatesButton.setEnabled(organizationsReadyToTrack.stream().anyMatch(o -> !o.isTrackingActive()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRequestLocationUpdatesButton.setOnClickListener(view1 -> {
            if (checkPermissions()) {
                if (trackingViewModel.activeAllTrackingOrganization(true)) {
                    Toast.makeText(requireContext(), R.string.organizazzioni_private_no_loogato, Toast.LENGTH_LONG).show();
                }
                tvCurrentStatus.setText(R.string.initializing_tracking);
            } else {
                requestPermissions();
            }
        });
        mRemoveLocationUpdatesButton.setOnClickListener(view12 -> {
            trackingViewModel.activeAllTrackingOrganization(false);
            tvCurrentStatus.setText(R.string.tracking_terminated);
        });

        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState(Tools.requestingLocationUpdates(requireContext()));

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can ic_exit foreground mode.


    }


    @Override
    public void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .registerOnSharedPreferenceChangeListener(this);

    }


    @Override
    public void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.

            requireContext().unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
//            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(root,
                    getString(R.string.app_necessita_di_permessi),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> {
                        // Request permission
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                    })
                    .show();
        } else {
//            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
//        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
//                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                if (trackingViewModel.activeAllTrackingOrganization(true))
                    Toast.makeText(requireContext(), R.string.organizazzioni_private_no_loogato, Toast.LENGTH_LONG).show();

//                mService.updateOrganizations(organizationToTrack);
//                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                setButtonsState(false);
                Snackbar.make(
                        root,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        })
                        .show();
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @NotNull String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(Tools.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(Tools.KEY_REQUESTING_LOCATION_UPDATES,
                    false));
        }
        if (s.equalsIgnoreCase(StalkerTrackingService.CHRONOMETER_KEY)) {
            long chronometerBase = sharedPreferences.getLong(StalkerTrackingService.CHRONOMETER_KEY,
                    SystemClock.elapsedRealtime());
            if (chronometerBase == -1) {
                tvTimer.setBase(SystemClock.elapsedRealtime());
                tvTimer.stop();
            } else {
                tvTimer.setBase(chronometerBase);
                tvTimer.start();
            }
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            mRequestLocationUpdatesButton.setEnabled(false);
            mRemoveLocationUpdatesButton.setEnabled(true);
        } else {
            mRequestLocationUpdatesButton.setEnabled(true);
            mRemoveLocationUpdatesButton.setEnabled(false);
        }
//        updateOrganizationToTrack();
    }


    public static class StalkerHandler extends Handler {
        WeakReference<TrackingFragment> reference;

        StalkerHandler(TrackingFragment reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(@NotNull Message msg) {
            super.handleMessage(msg);
            TrackingFragment fragment = reference.get();
            Bundle b = msg.getData();
            //gestione dei empty message
            switch (msg.what) {
                case TRACKING_INITIALIZING_MSG_CODE:
                    fragment.tvCurrentStatus.setText(R.string.initializing_tracking);
                    break;
                case TRACKING_STOP_MSG_CODE:
                    fragment.tvCurrentStatus.setText(R.string.nessun_organizzazione_ti_sta_stalkerando);
                    break;
                case TRACKING_NOT_IN_PLACE_MSG_CODE:

                    fragment.tvCurrentStatus.setText(R.string.non_presente_nei_luoghi_tracciati);
                default:
            }
            //gestione degli messaggi non empty, se sono più di un caso, allora può essere convertito in switch
            int code = b.getInt(MSG_CODE, -1);
            switch (code) {
                case TRACKING_MSG_CODE:
                    fragment.tvCurrentStatus.setText(b.getString(PLACE_MSG));
                    break;
                case TRACKING_UPDATE_TIMER:
//                    fragment.
            }
        }
    }
}
