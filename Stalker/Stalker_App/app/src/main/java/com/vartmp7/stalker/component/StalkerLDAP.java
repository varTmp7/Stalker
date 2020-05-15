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

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 *
 * Classe viene usato per gestire la connessione e l'autenticazione LDAP utilizzando la libreria Unboundid LDAP
 */
public class StalkerLDAP {
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

    public String getCn() {
        return entry.getAttributeValue("cn");
    }

    public SearchResultEntry getSearchResultEntry(){
        return entry;
    }

}
