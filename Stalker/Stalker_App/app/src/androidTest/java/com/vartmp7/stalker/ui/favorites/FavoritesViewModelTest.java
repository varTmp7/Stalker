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

package com.vartmp7.stalker.ui.favorites;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.vartmp7.stalker.TestUtil;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.repository.OrganizationsRepository;
import com.vartmp7.stalker.ui.favorite.FavoritesViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class FavoritesViewModelTest {
    private FavoritesViewModel viewModel;
    private OrganizationsRepository orgRepo;
    private List<Organization> allOrganizations;
    private LifecycleOwner lifecycleOwner;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();


    @Before
    public void setup(){
        orgRepo = mock(OrganizationsRepository.class);
        viewModel=new FavoritesViewModel(orgRepo);
        allOrganizations = Arrays.asList(
                new Organization().setId(1).setFavorite(false),
                new Organization().setId(2).setFavorite(true)
        );
        when(orgRepo.getOrganizations()).then(invocation->{
            MutableLiveData<List<Organization>> allLiveOrgs = new MutableLiveData<>();
             allLiveOrgs.postValue(allOrganizations);
             return allLiveOrgs;
        });
        lifecycleOwner = TestUtil.mockLifecycleOwner();
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private Observer<List<Organization>> observer;

    @Test
    public void testGetOrganizations(){
        List<Organization> expected = allOrganizations.stream().filter(Organization::isFavorite).collect(Collectors.toList());
        doNothing().when(observer).onChanged(anyList());
        viewModel.getFavoriteOrganizations().observe(lifecycleOwner,observer);
        verify(observer).onChanged(expected);
    }
}
