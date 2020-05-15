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

package com.vartmp7.stalker.ui.history;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.datamodel.TrackRecord;
import com.vartmp7.stalker.repository.OrganizationsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public class HistoryViewModel extends ViewModel {
    static public class HistoryViewModelFactory implements ViewModelProvider.Factory{
        private OrganizationsRepository repo;

        public HistoryViewModelFactory(OrganizationsRepository repo) {
            this.repo = repo;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(HistoryViewModel.class)){
                HistoryViewModel historyViewModel = new HistoryViewModel(repo);
                return (T) historyViewModel;
            }
            throw new IllegalArgumentException("View model not found");
        }
    }
    public enum Message {notLogged, noRecords, hasRecords}

    private OrganizationsRepository orgRepo;
    private MutableLiveData<Message> messageStatus;
    private MutableLiveData<List<String>> placeNames;
    private MutableLiveData<List<TrackRecord>> trackrecords;

    public void initData(OrganizationsRepository organizationsRepository){
        orgRepo= organizationsRepository;
    }
    public HistoryViewModel(OrganizationsRepository organizationsRepository) {
        messageStatus = new MutableLiveData<>(Message.notLogged);
        orgRepo = organizationsRepository;
        placeNames = new MutableLiveData<>(new ArrayList<>());
        trackrecords = new MutableLiveData<>(new ArrayList<>());
        orgRepo.getOrganizations().observeForever(organizations -> {
            if (organizations.stream().anyMatch(Organization::isLogged))
                messageStatus.setValue(Message.noRecords);
            else
                messageStatus.setValue(Message.notLogged);
        });

        trackrecords.observeForever(records -> {
            if (records.size() != 0)
                messageStatus.setValue(Message.hasRecords);
            else
                messageStatus.setValue(Message.noRecords);
        });

        orgRepo.getTrackHistory().observeForever(records -> {
            trackrecords.setValue(records);
            List<String> collect = new ArrayList<>();
            collect.add("Selezione un luogo");
            collect.addAll(records.stream().map(TrackRecord::getPlaceName).distinct().collect(Collectors.toList()));
            placeNames.setValue(collect);
        });
    }

    void setFilterByName(String name) {
        trackrecords.setValue(orgRepo.getTrackHistory().getValue().stream().filter(t -> t.getPlaceName().equals(name)).collect(Collectors.toList()));
    }

    void removeFilter() {
        trackrecords.setValue(orgRepo.getTrackHistory().getValue());
    }

    public LiveData<List<Organization>> getOrganizations() {
        return orgRepo.getOrganizations();
    }

    void updateTrackHistories() {
        orgRepo.updateTrackRecords(orgRepo.getOrganizations().getValue().stream().filter(Organization::isLogged).collect(Collectors.toList()));
    }


    LiveData<List<TrackRecord>> getTrackRecords() {
        return trackrecords;
    }

    LiveData<Message> getMessageStatus() {
        return messageStatus;
    }

    LiveData<List<String>> getPlaceNames() {
        return placeNames;
    }
}