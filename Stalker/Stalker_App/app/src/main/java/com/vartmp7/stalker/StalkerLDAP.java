package com.vartmp7.stalker;

import android.util.Log;

import androidx.annotation.NonNull;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class StalkerLDAP {
    private static final String TAG = "com.vartmp7.stalker.StalkerLDAP";

    private LDAPConnection connection;
    private BindResult result;

    private String bindDN;
    private String bindPassword;
    private String serverAddress;
    private int serverPort;

    private SearchResultEntry entry;

    public StalkerLDAP(String serverAddress, int port, String binDn, String password) {
        this.serverAddress = serverAddress;
        this.serverPort = port;
        this.bindDN = binDn;
        this.bindPassword = password;

    }

    public void bind() throws LDAPException, ExecutionException, InterruptedException {
        this.connection = new LDAPConnection(serverAddress, serverPort);
        FutureTask<BindResult> bindFutureTask = new FutureTask<>(() -> connection.bind(bindDN, bindPassword));
        new Thread(bindFutureTask).start();
        this.result = bindFutureTask.get();


    }
    public void search() throws ExecutionException, InterruptedException {
        FutureTask<SearchResultEntry> searchFutureTask = new FutureTask<>(() -> connection.getEntry(bindDN));
        new Thread(searchFutureTask).start();
        this.entry = searchFutureTask.get();
//        Log.d(TAG, "uid:" + entry.getAttributeValue("uid"));
//        Log.d(TAG, "uidNumber:" + entry.getAttributeValue("uidNumber"));
//        Log.d(TAG, "connect: " + entry.getAttributeValue("givenName"));
//
//        Log.d(TAG, "connect: "+result.getResultCode().toString());

        this.connection.close();
    }
    public boolean isLogged(){
        if (this.result != null && this.entry != null && this.result.getResultCode().toString().equalsIgnoreCase("0"))
            return true;
        return false;
    }

    public String getSurname() {
        return entry.getAttributeValue("givenName");
    }

    public String getUsername() {
        return entry.getAttributeValue("User Name");
    }


    public String getUid() {
        return entry.getAttributeValue("uid");

    }

    public String getUidNumber() {
        return entry.getAttributeValue("uidNumber");
    }

    /**
     * restiruisce il sn (non so che sia)
     * @return
     */
    public String getSn() {
        return entry.getAttributeValue("sn");
    }

    public String getCn() {
        return entry.getAttributeValue("cn");
    }

    public SearchResultEntry getSearchResultEntry(){
        return entry;
    }

}
