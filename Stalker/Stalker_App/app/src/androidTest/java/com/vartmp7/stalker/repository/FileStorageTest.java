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

import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.gson.Gson;
import com.vartmp7.stalker.TestUtil;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.datamodel.OrganizationResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;



@RunWith(AndroidJUnit4.class)
public class FileStorageTest {
    private static final String TAG ="com.vartmp7.stalker.FileOrganizationsLocalSourceTest" ;
    private FileStorage source;
    private List<Organization> firsts;
    private List<Organization> expected;
    private LifecycleOwner lifecycleOwner;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUpTest(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.lifecycleOwner = TestUtil.mockLifecycleOwner();
        firsts = Arrays.asList(
                new Organization().setId(1).setName("asd").setTracking(false),
                new Organization().setId(2).setName("lol").setTracking(false),
                new Organization().setId(3).setName("lll").setTracking(false)
        );
        try (PrintWriter pw = new PrintWriter(context.openFileOutput("prova.json",Context.MODE_PRIVATE))){
            OrganizationResponse r = new OrganizationResponse().setOrganizations(firsts);
            pw.println(new Gson().toJson(r));
            pw.flush();
            Log.d(TAG,"ok");
        }catch(FileNotFoundException e){
            Log.e(TAG,e.getMessage());
        }
        source = new FileStorage("prova.json",context,new MutableLiveData<>());
        MockitoAnnotations.initMocks(this);
        doNothing().when(observer).onChanged(anyList());
    }

    @Mock
    private Observer<List<Organization>> observer;

    @Test
    public void testGet(){
        doNothing().when(observer).onChanged(anyList());
        source.getLocalOrganizations().observe(lifecycleOwner, observer);
        verify(observer,timeout(1000)).onChanged(firsts);
    }

    @Test
    public void testSave() {
        expected = Arrays.asList(new Organization().setId(12));
        source.saveOrganizations(expected);
        source.getLocalOrganizations().observe(lifecycleOwner, observer);
        verify(observer,timeout(1000)).onChanged(expected);
    }

    @Test
    public void testUpdateOrganization(){
        List<Organization> toUpdate = new ArrayList<>();
        for (Organization org: firsts){
            toUpdate.add(new Organization().setId(org.getId()).setName((org.getName())));
        }
        Organization o = toUpdate.get(0).setName("updated");
        expected = toUpdate;

        source.getLocalOrganizations().observe(lifecycleOwner, observer);
        source.updateOrganization(o);
        verify(observer,timeout(1000)).onChanged(expected);

    }


    @Test
    public void testUpdateOrganizations(){
        List<Organization> toUpdate = new ArrayList<>();
        for (Organization org: firsts){
            toUpdate.add(new Organization().setId(org.getId()).setName((org.getName())));
        }
        toUpdate.get(0).setTrackingActive(true).setName("updated0");
        toUpdate.get(1).setAnonymous(true).setTracking(true).setName("updated1");
//        toUpdate.forEach(o-> Log.d(TAG, "updateOrganizations: "+o.getId()));
        expected = toUpdate;
        source.getLocalOrganizations().observe(lifecycleOwner,observer);
        source.updateOrganizations(toUpdate);
        verify(observer,timeout(1000)).onChanged(expected);
    }

    @Test
    public void testUpdateOrganizationsInfo(){
        List<Organization> updater = new ArrayList<>(Arrays.asList(
                new Organization().setId(firsts.get(0).getId()).setName("updated1"),
                new Organization().setId(firsts.get(1).getId()).setName("updated2").setTracking(true),
                new Organization().setId(36).setName("new org")
        ));
        expected = new ArrayList<>(firsts);
        TestUtil.updateOrganizationsFromOrganizationsLists(expected,updater);
//        expected.forEach(o-> Log.d(TAG, "testUpdateOrganizzazioni: EXPECTED:"+o.getId()+"name "+o.getName()));
        source.updateOrganizationInfo(updater);
        source.getLocalOrganizations().observe(lifecycleOwner,observer);
        verify(observer,timeout(1000)).onChanged(expected);

    }

}