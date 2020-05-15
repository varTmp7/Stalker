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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.vartmp7.stalker.TestUtil;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.datamodel.PolygonPlace;
import com.vartmp7.stalker.datamodel.TrackRecord;
import com.vartmp7.stalker.repository.OrganizationsRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class HistoryViewModelTest {
    private List<TrackRecord> trackRecords;

    private HistoryViewModel viewModel;
    private OrganizationsRepository orgRepo;
    private LifecycleOwner lifecycleOwner = TestUtil.mockLifecycleOwner();
    @Mock
    private Observer<List<TrackRecord>> trackRecordsObserver;
    @Mock
    private Observer<List<Organization>> organizationsObserver;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private List<Organization> organizations;

    @Before
    public void setup() {
        orgRepo = mock(OrganizationsRepository.class);

        organizations = Arrays.asList(
                new Organization().setId(1).setLogged(true).setPlaces(Arrays.asList()),
                new Organization().setId(2).setLogged(false),
                new Organization().setId(3).setLogged(true)
        );
        for (int i=0;i<organizations.size();i++){
            Organization org = organizations.get(i);
            PolygonPlace place = new PolygonPlace();
            place.setId(i);
            place.setOrgId(org.getId());
            org.setPlaces(Collections.singletonList(place));
        }
        when(orgRepo.getOrganizations()).then(invoker->{
            MutableLiveData<List<Organization>> liveOrganizations = new MutableLiveData<>();
            liveOrganizations.setValue(organizations);
            return liveOrganizations;
        });

        trackRecords = Arrays.asList(
                new TrackRecord().setPlaceId(1),
                new TrackRecord().setPlaceId(2),
                new TrackRecord().setPlaceId(3)
        );
        when(orgRepo.getTrackHistory()).then(invoker->{
            MutableLiveData<List<TrackRecord>> liveTrackRecords = new MutableLiveData<>();
            liveTrackRecords.setValue(trackRecords);
            return liveTrackRecords;
        });

        viewModel = new HistoryViewModel(orgRepo);
        viewModel.initData(orgRepo);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetTrackRecords(){
        doNothing().when(trackRecordsObserver).onChanged(anyList());
        viewModel.getTrackRecords().observe(lifecycleOwner,trackRecordsObserver);
        verify(trackRecordsObserver).onChanged(trackRecords);
    }

    @Test
    public void testGetOrganizations(){
        doNothing().when(organizationsObserver).onChanged(anyList());
        viewModel.getOrganizations().observe(lifecycleOwner,organizationsObserver);
        verify(organizationsObserver).onChanged(organizations);
    }

    @Test
    public void testUpdateTrackRecords(){
        doNothing().when(orgRepo).updateTrackRecords(anyList());
        List<Organization> orgToUpdateHistory = organizations.stream()
                .filter(Organization::isLogged)
                .collect(Collectors.toList());
        viewModel.updateTrackHistories();
        verify(orgRepo).updateTrackRecords(orgToUpdateHistory);
     }

}
