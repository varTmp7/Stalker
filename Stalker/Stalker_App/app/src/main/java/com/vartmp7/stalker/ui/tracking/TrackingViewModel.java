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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.repository.OrganizationsRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public class TrackingViewModel extends ViewModel {

    @Setter(AccessLevel.PUBLIC) @Getter(AccessLevel.PUBLIC)
    private OrganizationsRepository repository;

    public LiveData<List<Organization>> getOrganizations() {
        return repository.getOrganizations();
    }

    public void updateOrganization(Organization o) {
        repository.updateOrganization(o);
    }

    public boolean activeAllTrackingOrganization(boolean active){
        List<Organization> l=repository.getOrganizations().getValue().stream().filter(Organization::isTracking).collect(Collectors.toList());
        AtomicBoolean toReturn = new AtomicBoolean(false);
        l.forEach(organizzazione ->{
            if (organizzazione.isLogged()||!organizzazione.getType().equalsIgnoreCase(Organization.PRIVATE)){
                organizzazione.setTrackingActive(active);
            }else{
                toReturn.set(true);
            }
        } );
        repository.updateOrganizations(l);
        return toReturn.get();
    }

    public void init(OrganizationsRepository repository) {
        this.repository = repository;
    }

    public void removeFavorite(Organization o) {
        repository.removeFavorite(o);
    }
    public void updateOrganizations(List<Organization> list){
        repository.updateOrganizations(list);
    }

    public void addFavorite(Organization o)  {
        repository.addFavorite(o);
    }

}