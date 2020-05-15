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



import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.vartmp7.stalker.TestUtil;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.repository.OrganizationsRepository;
import com.vartmp7.stalker.ui.organizations.OrganizationsViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class OrganizationsViewModelTest{
    private static final String TAG="com.vartmp7.stalker.OrganizationsViewModelTest";

    private OrganizationsViewModel viewModel ;
    private OrganizationsRepository orgRepo;
    private LifecycleOwner lifecycleOwner;

    private List<Organization> organizationsToGet;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup(){
        orgRepo = mock(OrganizationsRepository.class);
        viewModel = new OrganizationsViewModel(orgRepo);
        lifecycleOwner = TestUtil.mockLifecycleOwner();

        organizationsToGet = Arrays.asList(
                new Organization().setId(1),
                new Organization().setId(2)
        );

        MutableLiveData<List<Organization>> liveOrganizations = new MutableLiveData<>();
        when(orgRepo.getOrganizations()).then(invoker->{
            liveOrganizations.postValue(organizationsToGet);
            return liveOrganizations;
        });
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private Observer<List<Organization>> observer;

    @Test
    public void testGetOrganizations(){
        viewModel.getOrganizationList().observe(lifecycleOwner,observer);
        doNothing().when(observer).onChanged(anyList());
        verify(observer).onChanged(organizationsToGet);
    }

    @Test
    public void testRefresh(){
        doNothing().when(orgRepo).refreshOrganizations();
        viewModel.refresh();
        verify(orgRepo).refreshOrganizations();
    }

    @Test
    public void testUpdateOrganization(){
        Organization o = new Organization().setId(1);
        doNothing().when(orgRepo).updateOrganization(any(Organization.class));
        viewModel.updateOrganizzazione(o);
        verify(orgRepo).updateOrganization(o);
    }

}
