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

package com.vartmp7.stalker.ui.favorite;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vartmp7.stalker.component.NotLogged;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.repository.OrganizationsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public class FavoritesViewModel extends ViewModel {
    public static class FavoritesViewModelFactory implements ViewModelProvider.Factory{
        private OrganizationsRepository organizationsRepository;

        public FavoritesViewModelFactory(OrganizationsRepository organizationsRepository) {
            this.organizationsRepository= organizationsRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(FavoritesViewModel.class)){
                return (T) new FavoritesViewModel(organizationsRepository);
            }
            throw new IllegalArgumentException("Viewmodel not found!");
        }
    }

    private OrganizationsRepository orgRepo;
    private LiveData<List<Long>> mutableliveDataOrgIds;
    private LiveData<List<Organization>> liveDataOrganizations;
    @Getter(AccessLevel.PUBLIC)
    private MediatorLiveData<List<Organization>> favoriteOrganizations;
    private MutableLiveData<Boolean> organizationsQueryExhausted;
    private MutableLiveData<Boolean> firebaseQueryExhausted;
    private final Observer<Boolean> queryExhaustedObserver;
    private final Observer<List<Organization>> storageObserver;
    private final Observer<List<Long>> firebaseObserver;

    public FavoritesViewModel(OrganizationsRepository orgRepo) {
        this.orgRepo = orgRepo;
        this.favoriteOrganizations = new MediatorLiveData<>();
        this.favoriteOrganizations.setValue(orgRepo.getOrganizations().getValue());
        this.liveDataOrganizations = new MutableLiveData<>(new ArrayList<>());
        this.firebaseQueryExhausted = new MutableLiveData<>(false);
        this.organizationsQueryExhausted = new MutableLiveData<>(false);

        queryExhaustedObserver = aBoolean -> {
            if (firebaseQueryExhausted.getValue()==null || organizationsQueryExhausted.getValue()==null)
                return;
            if (firebaseQueryExhausted.getValue() && organizationsQueryExhausted.getValue()) {
                final List<Long> orgIds = mutableliveDataOrgIds.getValue();
                List<Organization> list = liveDataOrganizations.getValue()
                        .stream()
                        .filter(o -> orgIds.contains(o.getId()))
                        .collect(Collectors.toList());
                list.forEach(o-> orgRepo.updateOrganization(o.setFavorite(true)));
                favoriteOrganizations.postValue(list);
                favoriteOrganizations.removeSource(organizationsQueryExhausted);
                favoriteOrganizations.removeSource(firebaseQueryExhausted);

            }
        };

        storageObserver = organizzazioni -> organizationsQueryExhausted.setValue(true);
        firebaseObserver = organizzazioni -> firebaseQueryExhausted.setValue(true);

        refresh();
    }


    public void updateOrganizzazione(Organization org) {
        orgRepo.updateOrganization(org);
    }

    public void refresh() {
        int i = 0;
        this.firebaseQueryExhausted.setValue(false);
        this.organizationsQueryExhausted.setValue(false);
        this.liveDataOrganizations = orgRepo.getOrganizations();
        this.mutableliveDataOrgIds = orgRepo.getFavorites();

        this.favoriteOrganizations.addSource(liveDataOrganizations, storageObserver);
        this.favoriteOrganizations.addSource(mutableliveDataOrgIds,firebaseObserver);

        this.favoriteOrganizations.addSource(organizationsQueryExhausted, queryExhaustedObserver);
        this.favoriteOrganizations.addSource(firebaseQueryExhausted, queryExhaustedObserver);
    }

    public void removeFromPreferiti(Organization org) throws NotLogged {
        if(favoriteOrganizations.getValue()!=null)
            favoriteOrganizations.postValue(
                favoriteOrganizations.getValue()
                    .stream()
                    .filter(o->o.getId()!=org.getId())
                    .collect(Collectors.toList())
            );
        orgRepo.removeFavorite(org);
    }

}