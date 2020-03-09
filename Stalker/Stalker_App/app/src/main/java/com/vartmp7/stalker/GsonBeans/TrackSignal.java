package com.vartmp7.stalker.GsonBeans;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.vartmp7.stalker.MainActivity;

public class TrackSignal {
    private static final String TAG="com.vartmp7.stalker.GsonBeans.TrackSignal";
    // Annotazione expose usato indicare quale campo devo venir serializzato
    private long idOrganization;
    private long idPlace=0;
    @Expose
    private boolean entered=false;
    @Expose
    private boolean authenticated=false;
    @Expose
    private long uid_number;
    @Expose
    private String username;
    @Expose
    private String surname;
    @Expose
    private String date_time;

    public TrackSignal(boolean en,boolean au, long uid, String user, String sur, String date) {
        entered = en;
        authenticated = au;
        uid_number = uid;
        username = user;
        surname = sur;
        date_time = date;
    }
    public TrackSignal setIdPlace(long idPlace) {
        this.idPlace = idPlace;
        return this;
    }
    public String getUrlToPost(){
        return MainActivity.SERVER + "organizations/" + idOrganization
                + "/places/" + idPlace+ "/tracks";
    }


    public long getIdOrganization() {
        return idOrganization;
    }

    public TrackSignal setIdOrganization(long idOrganization) {
        this.idOrganization = idOrganization;
        return this;
    }

    public boolean isEntered() {
        return entered;
    }

    public TrackSignal setEntered(boolean entered) {
        this.entered = entered;
        return this;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public TrackSignal setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        return this;
    }

    public long getUid_number() {
        return uid_number;
    }

    public TrackSignal setUid_number(long uid_number) {
        this.uid_number = uid_number;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public TrackSignal setUsername(String username) {
        Log.d(TAG, "setUsername: "+username);
        this.username = username;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public TrackSignal setSurname(String surname) {

        Log.d(TAG, "setUsername: "+surname);
        this.surname = surname;
        return this;
    }

    public String getDate_time() {
        return date_time;
    }
    public TrackSignal setDate_time(String date_time) {
        this.date_time = date_time;
        return this;
    }

    public TrackSignal() {
    }



    public long getIdPlace() {
        return idPlace;
    }

}