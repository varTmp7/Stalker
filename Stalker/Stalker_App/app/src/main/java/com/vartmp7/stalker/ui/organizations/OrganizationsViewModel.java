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

package com.vartmp7.stalker.ui.organizations;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.repository.OrganizationsRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
class OrganizationsViewModel extends ViewModel {
    private OrganizationsRepository orgRepo;
    @Getter(AccessLevel.PUBLIC)
    private MutableLiveData<List<Organization>> organizationList;

    OrganizationsViewModel(OrganizationsRepository orgRepo) {
        this.orgRepo = orgRepo;
        organizationList = new MutableLiveData<>(new ArrayList<>());
        orgRepo.getOrganizations().observeForever(organizations ->
                organizationList.setValue(organizations.stream().filter(o->!o.isTracking()).collect(Collectors.toList())));
    }

    void updateOrganizzazione(Organization o){
        orgRepo.updateOrganization(o);
    }
    void addOrganizationToTrack(@NotNull Organization o){
        orgRepo.updateOrganization(o.setTracking(true));
    }

    void refresh() {
        orgRepo.refreshOrganizations();
    }

    public static class OrganizationViewModelFactory implements ViewModelProvider.Factory{
        private OrganizationsRepository repo;

        OrganizationViewModelFactory(OrganizationsRepository repo) {
            this.repo = repo;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(OrganizationsViewModel.class)){
                return (T) new OrganizationsViewModel(repo);
            }
            throw new IllegalArgumentException("View model not found!");
        }
    }
}
