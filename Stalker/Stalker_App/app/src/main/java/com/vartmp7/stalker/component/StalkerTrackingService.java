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

package com.vartmp7.stalker.component;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.vartmp7.stalker.MainActivity;
import com.vartmp7.stalker.R;
import com.vartmp7.stalker.Tools;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.datamodel.PolygonPlace;
import com.vartmp7.stalker.datamodel.TrackSignal;
import com.vartmp7.stalker.datamodel.placecomponent.Coordinate;
import com.vartmp7.stalker.repository.RestApiService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vartmp7.stalker.component.StalkerNotificationManager.EXTRA_STARTED_FROM_NOTIFICATION;
import static com.vartmp7.stalker.component.StalkerNotificationManager.NOTIFICATION_ID;

public class StalkerTrackingService extends Service {
    private static final String PACKAGE_NAME = "com.vartmp7.stalker.StalkerTrackingService";

    private static final String TAG = StalkerTrackingService.class.getSimpleName();
    private final Binder mBinder = new LocalBinder();
    private boolean mChangingConfiguration = false;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private StalkerNotificationManager stalkerNotificationManager;

    private CallBack serviceCallback;
    private List<Organization> organizations;
    private TrackRequestCreator creator;
    private Location mLocation;
    private NotificationManager mNotificationManager;
    private Coordinate currentCoordinate;
    private PolygonPlace currentPlace = null;
    private RestApiService restApiService;
    private Organization currentOrganization = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        creator = new TrackRequestCreator(new StalkerStepCounter(sensorManager, stepSensor));
        mLocationRequest =  creator.getMostPrecise();
        getLastLocation();
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Tools.setRequestingLocationUpdates(this, false);
//            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL_SERVER)
                .client(Tools.getUnsafeOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        restApiService = retrofit.create(RestApiService.class);



        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());


        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        stalkerNotificationManager = new StalkerNotificationManager(this, mNotificationManager);
    }

    @Override
    public int onStartCommand(@NotNull Intent intent, int flags, int startId) {
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            Log.e(TAG, "onStartCommand: a");
            removeLocationUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        stopForeground(true);
        mChangingConfiguration = false;
        lastMessage="";
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        lastMessage="";
        mChangingConfiguration = false;
        super.onRebind(intent);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        Log.d(PACKAGE_NAME, "onUnbind: !mChangingConfiguration "
                + (!mChangingConfiguration));
        Log.d(PACKAGE_NAME, "onUnbind:  Tools.requestingLocationUpdates(this) "
                + (Tools.requestingLocationUpdates(this)));
        if (!mChangingConfiguration && Tools.requestingLocationUpdates(this)) {
            Log.i(PACKAGE_NAME, "Starting foreground service");
            startForeground(NOTIFICATION_ID, stalkerNotificationManager.getNotification(getDisplayMessage(currentOrganization, currentPlace)));

            //            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    public void requestLocationUpdates() {
//        Log.i(TAG, "Requesting location updates");
        Tools.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), StalkerTrackingService.class));
    }

    public void removeLocationUpdates() {
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Tools.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Tools.setRequestingLocationUpdates(this, true);
        }
    }


    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            onNewLocation(task.getResult());
                        } else {
//                                Log.w(TAG, "Failed to get location.");
                        }
                    });
        } catch (SecurityException unlikely) {
//            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    public boolean serviceIsRunningInForeground(@NotNull Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            String className = getClass().getName();
            String otherClasseName = service.service.getClassName();
            if (className.equals(otherClasseName)) {
              //  Log.d("serviceIsRunningInForeground", "FOUND match");
                if (service.foreground) {
                //    Log.d(TAG, "Service is running foreground");
                    return true;
                }
               // else Log.d(TAG, "Service is NOT running foreground");

            }
        }
        return false;
    }

    public void setCallback(CallBack callback) {
        this.serviceCallback = callback;
        if (callback != null) {
            callback.onNewLocation(getDisplayMessage(currentOrganization, currentPlace));
        }
    }


    public void updateOrganizations(@NotNull List<Organization> organizations) {
        this.organizations = organizations;
        //quando non ci sono organizzazioni con isTrackingActive == true e
        //è diverso da null, allora mostro il messaggio di nessun organizzazione ti sta tracciando!
        if (serviceCallback != null && organizations.size()==0) {
            serviceCallback.stopTracking();}

            if (organizations.size() == 0) {
                updateChronometerBase(-1, -1);
            }


        if (currentOrganization != null) {
            Optional<Organization> optionalOrg = organizations.stream().filter(org -> org.getId() == currentOrganization.getId()).findAny();

            if (optionalOrg.isPresent()) {
                Organization organization = optionalOrg.get();
                TrackSignal anonymousSignal = new TrackSignal()
                        .setAuthenticated(false)
                        .setIdOrganization(organization.getId())
                        .setIdPlace(currentPlace.getId());

                TrackSignal authSignal = new TrackSignal()
                        .setAuthenticated(true)
                        .setIdPlace(currentPlace.getId())
                        .setIdOrganization(organization.getId())
                        .setUsername(organization.getPersonalCn())
                        .setPassword(organization.getLdapPassword());

                if (organization.isAnonymous() != currentOrganization.isAnonymous()) {
                    if (organization.isAnonymous()) {
                        sendSignal(authSignal.setEntered(false).setDateTime(getFormattedTime()));
                        sendSignal(anonymousSignal.setEntered(true).setDateTime(getFormattedTime()));
                    } else {
                        sendSignal(anonymousSignal.setEntered(false).setDateTime(getFormattedTime()));
                        sendSignal(authSignal.setEntered(true).setDateTime(getFormattedTime()));
                    }
                    updateChronometerBase(currentPlace.getId(), SystemClock.elapsedRealtime());
                } else if (organization.isLogged() != currentOrganization.isLogged()) {
                    if (organization.isLogged()) {
                        sendSignal(anonymousSignal.setEntered(false).setDateTime(getFormattedTime()));
                        sendSignal(authSignal.setEntered(true).setDateTime(getFormattedTime()));
                    } else {
                        sendSignal(authSignal.setEntered(false).setDateTime(getFormattedTime()));
                        sendSignal(anonymousSignal.setEntered(true).setDateTime(getFormattedTime()));
                    }

                    updateChronometerBase(currentPlace.getId(), SystemClock.elapsedRealtime());
                }
                currentOrganization = new Organization(organization);
            } else {
                TrackSignal trackSignal = new TrackSignal().setIdOrganization(currentOrganization.getId())
                        .setEntered(false)
                        .setIdPlace(currentPlace.getId())
                        .setDateTime(getFormattedTime())
                        .setAuthenticated(currentOrganization.isLogged() && !currentOrganization.isAnonymous());
                if (trackSignal.isAuthenticated()) {
                    trackSignal.setAuthenticated(true).setUsername(currentOrganization.getPersonalCn()).setPassword(currentOrganization.getLdapPassword());
                }
                currentOrganization = null;
                currentPlace = null;
                sendSignal(trackSignal);
                if (serviceCallback != null)
                    serviceCallback.notInAnyPlace();
                updateChronometerBase(-1, -1);
            }
        } else {
            if (mLocation != null) {
                Coordinate coordinate = new Coordinate(mLocation.getLatitude(), mLocation.getLongitude());
                onLocationsChanged(coordinate);
            }
        }

    }

    private void onLocationsChanged(@NotNull Coordinate newCoordinate) {
        currentCoordinate = newCoordinate;
        List<PolygonPlace> places = new ArrayList<>();
        TrackSignal trackSignal = new TrackSignal();
        if (organizations == null) return;
        organizations.forEach(elOrganizations -> {
            List<PolygonPlace> orgPlaces = elOrganizations.getPlaces();
            orgPlaces.forEach(e -> e.setOrgId(elOrganizations.getId()));
            places.addAll(orgPlaces);
        });
        Optional<PolygonPlace> opti = places.stream().filter(polygonPlace -> polygonPlace.isInside(currentCoordinate)).findFirst();
        trackSignal.setDateTime(getFormattedTime());

        if (opti.isPresent()) {
            PolygonPlace place = opti.get();

            Optional<Organization> optionalOrganization = organizations.stream().filter(organization -> organization.getId() == place.getOrgId()).findFirst();
            Organization newOrganization;
            if (optionalOrganization.isPresent())
                newOrganization = optionalOrganization.get();
            else
                return;
            if (currentPlace == null) {
//                Log.d(TAG, "onLocationsChanged: prevPlace ==null");
                currentPlace = place;
                currentOrganization = new Organization(newOrganization);
                trackSignal.setIdPlace(currentPlace.getId())
                        .setEntered(true)
                        .setAuthenticated(currentOrganization.isLogged() && !currentOrganization.isAnonymous())
                        .setIdOrganization(place.getOrgId());
                if (!currentOrganization.isAnonymous() && currentOrganization.isLogged())
                    trackSignal.setUsername(currentOrganization.getPersonalCn())
                            .setPassword(currentOrganization.getLdapPassword());
                if (serviceCallback != null)
                    serviceCallback.onNewLocation(getDisplayMessage(currentOrganization, currentPlace));
                sendSignal(trackSignal);
                updateChronometerBase(currentPlace.getId(), SystemClock.elapsedRealtime());
            } else if (currentPlace.getId() != place.getId()) {
//                Log.d(TAG, "onLocationsChanged: prevPlace !=null");
                trackSignal.setEntered(false)
                        .setIdPlace(currentPlace.getId());
                sendSignal(trackSignal);

                currentOrganization = new Organization(newOrganization);
                trackSignal.setEntered(true)
                        .setIdPlace(place.getId())
                        .setAuthenticated(!currentOrganization.isAnonymous() && currentOrganization.isLogged())
                        .setIdOrganization(place.getOrgId());
                if (!currentOrganization.isAnonymous())
                    trackSignal.setUsername(currentOrganization.getPersonalCn())
                            .setPassword(currentOrganization.getLdapPassword());
                sendSignal(trackSignal);
                currentPlace = place;
                updateChronometerBase(currentPlace.getId(), SystemClock.elapsedRealtime());
            }
//            else {}
            mLocationRequest = creator.getNewRequest(-1);
            //todo sarebbero da decommentare le due righe sotto, ma vengono tenuti per la dimostrazione

//            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,Looper.myLooper());

        } else {
            if (currentPlace != null) {
                Log.d(TAG, "onLocationsChanged: opti.isPresent = false");
                trackSignal.setEntered(false)
                        .setAuthenticated(!currentOrganization.isAnonymous() && currentOrganization.isLogged())
                        .setIdOrganization(currentOrganization.getId())
                        .setIdPlace(currentPlace.getId());
                if (!currentOrganization.isAnonymous() && currentOrganization.isLogged())
                    trackSignal.setUsername(currentOrganization.getPersonalCn())
                            .setPassword(currentOrganization.getLdapPassword());
                sendSignal(trackSignal);
                currentPlace = null;
                currentOrganization = null;
            }
            updateChronometerBase(-1, -1);
//            else{
//                Log.d(TAG, "onLocationsChanged() called with: ");
//            }

            OptionalDouble min = places.stream().mapToDouble(p -> p.distanceTo(currentCoordinate)).min();
            if (min.isPresent()) {
                double distance = min.getAsDouble();
                LocationRequest newRequest = creator.getNewRequest(distance);
//                if (newRequest.getPriority() != mLocationRequest.getPriority()) {
//                    mLocationRequest = newRequest;
////                    todo sarebbero da decommentare le due righe sotto, la looper si rompe, e non funziona
//                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,Looper.myLooper());
//                }
            }
        }

    }

    public void onNewLocation(@NotNull Location location) {
//        Log.d("TAG", "onNewLocation: " + location.getLongitude() + " " + location.getLatitude());
        mLocation = location;
        Coordinate currentCoordinate = new Coordinate(location.getLatitude(), location.getLongitude());
        onLocationsChanged(currentCoordinate);
        if (serviceCallback != null) {
//            Log.d(TAG, "onNewLocation: calling back");
            serviceCallback.onNewLocation(getDisplayMessage(currentOrganization, currentPlace));
        }
        displayNotification(getDisplayMessage(currentOrganization, currentPlace));
//            Toast.makeText(context, "new Location", Toast.LENGTH_SHORT).show();
    }

    @NotNull
    private String getFormattedTime() {
        SimpleDateFormat format = new SimpleDateFormat("Y-M-d hh:mm:ss");
        Date date = Calendar.getInstance(Locale.getDefault()).getTime();
        String formattedDate = format.format(date);
        return formattedDate.replace(" ", "T");
    }


    @NotNull
    private String getDisplayMessage(Organization org, PolygonPlace place) {
        if (org != null && place != null) {
            String orgName = org.getName();
            String placeName = place.getName();
            return getString(R.string.sei_in_tale_dei_tali, placeName, orgName);
        }
        if (organizations != null && organizations.size() != 0)
            return getString(R.string.non_presente_nei_luoghi_tracciati);
        return getString(R.string.nessun_organizzazione_ti_sta_stalkerando);
    }

    private String lastMessage="";
    private void displayNotification(String message) {
//        Log.d(PACKAGE_NAME, "displayNotifica: " + serviceIsRunningInForeground(this));
        if (serviceIsRunningInForeground(this) && !lastMessage.equalsIgnoreCase(message)) {
            stalkerNotificationManager.notify(stalkerNotificationManager.getNotification(message));
            lastMessage= message;
        }

    }

    private void sendSignal(@NotNull TrackSignal signal) {
        Log.d("SIGNAL", "sendSignal() called with: signal = [" + signal + "]");
        restApiService.tracking(signal.getIdOrganization(), signal.getIdPlace(), signal).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NotNull retrofit2.Call<Void> call, @NotNull Response<Void> response) {
//                Log.d(TAG, "onResponse: " + response.toString());
//                Log.d(TAG, "onResponse: RESPONSE");
            }

            @Override
            public void onFailure(@NotNull retrofit2.Call<Void> call, @NotNull Throwable t) {
//                Log.d(TAG, "onFailure: " + t.getMessage());
            }

        });

    }

    public static final String CHRONOMETER_KEY = "chronometer_key";
    public static final String LAST_PLACE_ID = "last_place_id";

    public void updateChronometerBase(long placeId, long time) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long lastPlaceId = sharedPreferences.getLong(LAST_PLACE_ID, -1);

        if (placeId == -1) {
            sharedPreferences.edit().putLong(LAST_PLACE_ID, placeId).putLong(CHRONOMETER_KEY, -1).apply();
            return;
        }
        // id dell'ultimo place viene modificato solo se si cambia da un place ad un'altra,
        // mentre se si passa da modalità loggato a non loggato, id non viene modificato,
        // quindi il tempo di permanenza in un luogo non riparte.
        // si potrebbe modificare in modo che riparta.
        if (lastPlaceId != placeId) {
            sharedPreferences.edit().putLong(LAST_PLACE_ID, placeId).putLong(CHRONOMETER_KEY, time).apply();
        }
    }

    public class LocalBinder extends Binder {
        public StalkerTrackingService getService() {
            return StalkerTrackingService.this;
        }

    }


}
