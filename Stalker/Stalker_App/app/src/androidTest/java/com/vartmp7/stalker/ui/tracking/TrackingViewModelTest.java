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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.vartmp7.stalker.TestUtil;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.repository.OrganizationsRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrackingViewModelTest {
    private static final String TAG="com.vartmp7.stalker.TrackingViewModelTest";

    private TrackingViewModel viewModel ;
    private OrganizationsRepository orgRepo;
    private LifecycleOwner lifecycleOwner;

    private List<Organization> organizationsToGet;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup(){
        viewModel = new TrackingViewModel();
        orgRepo = mock(OrganizationsRepository.class);
        lifecycleOwner = TestUtil.mockLifecycleOwner();

        organizationsToGet = Arrays.asList(
                new Organization().setId(1).setTracking(true),
                new Organization().setId(2).setTracking(false)
        );

        MutableLiveData<List<Organization>> liveOrganizations = new MutableLiveData<>();
        when(orgRepo.getOrganizations()).then(invoker->{
            liveOrganizations.postValue(organizationsToGet);
            return liveOrganizations;
        });
        viewModel.init(orgRepo);
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private Observer<List<Organization>> observer;

    @Test
    public void testGetOrganizations(){
        doNothing().when(observer).onChanged(anyList());
        viewModel.getOrganizations().observe(lifecycleOwner,observer);
        verify(observer).onChanged(organizationsToGet);
    }

    @Test
    public void testUpdateOrganization(){
        Organization o = new Organization().setId(1);
        doNothing().when(orgRepo).updateOrganization(any());
        viewModel.updateOrganization(o);
        verify(orgRepo).updateOrganization(o);
    }

    @Test
    public void testUpdateOrganizations(){
        List<Organization> organizations = Arrays.asList(
                new Organization().setId(1), new Organization().setId(2)
        );
        doNothing().when(orgRepo).updateOrganizations(anyList());
        viewModel.updateOrganizations(organizations);
        verify(orgRepo).updateOrganizations(organizations);
    }

    @Test
    public void testAddFavorite(){
        Organization o = new Organization().setId(1);

            doNothing().when(orgRepo).addFavorite(any());
            viewModel.addFavorite(o);
            verify(orgRepo).addFavorite(o);

    }

    @Test
    public void testRemoveFavorite(){
        Organization o = new Organization().setId(1);

            doNothing().when(orgRepo).removeFavorite(any());
            viewModel.removeFavorite(o);
            verify(orgRepo).removeFavorite(o);

    }
    @Test
    public void testActiveAllTrackingOrganization(){
        doNothing().when(orgRepo).updateOrganizations(anyList());
        viewModel.activeAllTrackingOrganization(true);
        ArrayList<Organization> toSet = organizationsToGet.stream().filter(Organization::isTracking).collect(Collectors.toCollection(ArrayList::new));
        toSet.forEach(o->o.setTrackingActive(true));
        verify(orgRepo).updateOrganizations(toSet);
        //verify(viewModel,atLeast(0));
    }

}
