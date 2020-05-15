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

package com.vartmp7.stalker.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.datamodel.TrackRecord;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class OrganizationsRepository {
    private Storage storage;
    private Obtainer obtainer;
    private FavoritesSource organizationFavoritesSource;

    public LiveData<List<TrackRecord>> getTrackHistory(){
          return obtainer.getTrackRecords();
    }
    public void updateTrackRecords(List<Organization> org){
        obtainer.updateTrackRecords(org);
    }



    public void updateOrganization(Organization o) {
        storage.updateOrganization(o);
    }

    public void updateOrganizations(List<Organization> l) {
        storage.updateOrganizations(l);
    }

    @Inject
    public OrganizationsRepository(Storage orgsLocalSource, Obtainer orgsWebSource) {
        this.storage = orgsLocalSource;
        this.obtainer = orgsWebSource;
    }

    public OrganizationsRepository(Storage orgsLocalSource, Obtainer orgsWebSource, FavoritesSource fa) {
        this.storage = orgsLocalSource;
        this.obtainer = orgsWebSource;
        this.organizationFavoritesSource = fa;
    }

    public void setOrganizationFavoritesSource(FavoritesSource organizationFavoritesSource) {
        this.organizationFavoritesSource = organizationFavoritesSource;
    }

    public LiveData<List<Organization>> getOrganizations() {
        LiveData<List<Organization>> fromLocal = storage.getLocalOrganizations();
        if (organizationFavoritesSource != null) {
            organizationFavoritesSource.refresh();
        }

        return fromLocal;
    }

    public LiveData<List<Long>> getFavorites() {
        return organizationFavoritesSource.getFavoriteOrganizationID();
    }

    public void addFavorite(@NotNull Organization org) {

            org.setFavorite(true);
            updateOrganization(org);
            organizationFavoritesSource.addOrganization(org.getId());

    }

    public void removeFavorite(@NotNull Organization org) {

            org.setFavorite(false);
            updateOrganization(org);
            organizationFavoritesSource.removeOrganization(org.getId());

    }

    private final Observer<List<Organization>> observer = new Observer<List<Organization>>() {
        @Override
        public void onChanged(List<Organization> organizzazioni) {
//            Log.d(TAG, "onChanged: aggiornare org");
            storage.updateOrganizationInfo(organizzazioni);
        }
    };

    public void refreshOrganizations() {
        LiveData<List<Organization>> resultFromWebCall = obtainer.getOrganizations();
        resultFromWebCall.observeForever(observer);

    }

    /*
    metodi più specifici, se in futuro si rendessero disponibili delle API più specifiche.
    Potrebbero avere senso per occupare meno banda e alleggerire il carico lato server,

    param: lista degli id delle organizzazioni
    List<Organizzazione> getOrganizzazioni(List<String> organizationIds);

    param: id di un'organizzazione
    Organizzazione getOrganizzazione(List<String> organizationId);
    */

}
