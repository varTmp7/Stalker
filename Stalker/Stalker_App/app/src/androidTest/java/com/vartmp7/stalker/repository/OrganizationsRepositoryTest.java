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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.vartmp7.stalker.TestUtil;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.datamodel.TrackRecord;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class OrganizationsRepositoryTest {
    private final String TAG = "com.vartmp7.stalker.OrganizationsRepositoryTest";
    private OrganizationsRepository orgRepo;
    private LifecycleOwner lifecycleOwner;
    //private TestObserver observer;
    private List<Organization> firsts;
    private List<Organization> fromWeb;
    private Storage storage;
    private FavoritesSource favoritesSource;
    private Obtainer obtainer;


    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        firsts = Arrays.asList(
                new Organization().setId(1).setName("asd").setTracking(false),
                new Organization().setId(2).setName("lol").setTracking(false),
                new Organization().setId(3).setName("lll").setTracking(false)
        );
        final MutableLiveData<List<Organization>> localLiveData = new MutableLiveData<>();
        storage = Mockito.mock(Storage.class);

        obtainer = Mockito.mock(Obtainer.class);
        fromWeb = Arrays.asList(
                new Organization().setId(1).setName("changed").setTracking(false),
                new Organization().setId(2).setName("lol").setTracking(true),
                new Organization().setId(37).setName("new org").setTracking(false)
        );

        when(obtainer.getOrganizations()).then((Answer<LiveData<List<Organization>>>) invocation -> {
            MutableLiveData<List<Organization>> liveData = new MutableLiveData<>();
            liveData.postValue(fromWeb);
            return liveData;
        });
        favoritesSource = Mockito.mock(FavoritesSource.class);
        orgRepo = new OrganizationsRepository(storage, obtainer, favoritesSource);
        lifecycleOwner = TestUtil.mockLifecycleOwner();
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private Observer<List<Organization>> orgObserver;

    @Test
    public void testGetOrganizations() {
        List<Organization> expected = firsts;
        when(storage.getLocalOrganizations()).then((Answer<LiveData<List<Organization>>>) invocation -> {
            MutableLiveData<List<Organization>> liveData = new MutableLiveData<>();
            liveData.postValue(firsts);
            return liveData;
        });
        doNothing().when(orgObserver).onChanged(anyList());
        orgRepo.getOrganizations().observe(lifecycleOwner, orgObserver);
        verify(orgObserver).onChanged(expected);
    }

    @Test
    public void testUpdateOrganization() {
        Organization toUpdate = new Organization().setId(0);
        doNothing().when(storage).updateOrganization(any(Organization.class));
        orgRepo.updateOrganization(toUpdate);
        verify(storage).updateOrganization(toUpdate);
    }

    @Test
    public void testUpdateOrganizations() {
        List<Organization> organizations = Arrays.asList(
                new Organization().setId(1), new Organization().setId(2)
        );
        doNothing().when(storage).updateOrganizations(anyList());
        orgRepo.updateOrganizations(organizations);
        verify(storage).updateOrganizations(organizations);
    }

    @Mock
    private Observer<List<Long>> favObserver;

    @Test
    public void testGetFavorites() {
        List<Long> favorites = Arrays.asList(1L, 2L, 3L, 45L, 1L);
        when(favoritesSource.getFavoriteOrganizationID()).then((Answer<LiveData<List<Long>>>) invocation -> {
            MutableLiveData<List<Long>> liveData = new MutableLiveData<>();
            liveData.postValue(favorites);
            return liveData;
        });
        doNothing().when(favObserver).onChanged(any());
        orgRepo.getFavorites().observe(lifecycleOwner, favObserver);
        verify(favObserver).onChanged(favorites);
    }

    @Mock
    private Observer<List<TrackRecord>> historyObserver;

    @Test
    public void testGetTrackHistory() {
        List<TrackRecord> trackRecords = Arrays.asList(
                new TrackRecord().setEntered(true).setOrgName("Math UNIPD").setPlaceName("Torre Archimede"),
                new TrackRecord().setEntered(false).setOrgName("Math UNIPD").setPlaceName("Torre Archimede"),
                new TrackRecord().setEntered(true).setOrgName("Math UNIPD").setPlaceName("Paolotti")
        );
        when(obtainer.getTrackRecords()).then((Answer<LiveData<List<TrackRecord>>>) invocation -> {
            MutableLiveData<List<TrackRecord>> liveData = new MutableLiveData<>();
            liveData.postValue(trackRecords);
            return liveData;
        });
        doNothing().when(historyObserver).onChanged(anyList());
        orgRepo.getTrackHistory().observe(lifecycleOwner, historyObserver);
        verify(historyObserver).onChanged(trackRecords);

    }

    @Test
    public void testUpdateTrackRecords() {
        List<Organization> organizations = Arrays.asList(
                new Organization().setId(1),
                new Organization().setId(10)
        );
        doNothing().when(obtainer).updateTrackRecords(anyList());
        orgRepo.updateTrackRecords(organizations);
        verify(obtainer).updateTrackRecords(organizations);
    }

    @Test
    public void testRefresh() {
        orgRepo.refreshOrganizations();
        doNothing().when(storage).updateOrganizationInfo(anyList());
        verify(storage).updateOrganizationInfo(fromWeb);
    }

    @Test
    public void testAddFavorite() {
        Organization toAdd = new Organization().setId(0).setFavorite(false);
        Organization expected = new Organization().setId(toAdd.getId()).setFavorite(true);

        doNothing().when(favoritesSource).addOrganization(any());
        doAnswer(invoker -> {
            assertEquals(toAdd.isFavorite(), expected.isFavorite());
            return null;
        }).when(storage).updateOrganization(toAdd);
        // doNothing().when(storage).updateOrganization(any(Organization.class));
        orgRepo.addFavorite(toAdd);
        verify(favoritesSource).addOrganization(expected.getId());
        verify(storage).updateOrganization(expected);


    }

    @Test
    public void testRemoveFromFavorite() {
        Organization toRemove = new Organization().setId(0).setFavorite(true);
        Organization expected = new Organization().setId(toRemove.getId()).setFavorite(false);

        doNothing().when(favoritesSource).removeOrganization(any());
        doAnswer(invoker -> {
            assertEquals(expected.isFavorite(), toRemove.isFavorite());
            return null;
        }).when(storage).updateOrganization(any(Organization.class));
        orgRepo.removeFavorite(toRemove);
        verify(favoritesSource).removeOrganization(toRemove.getId());
        verify(storage).updateOrganization(toRemove);

    }
}


