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

package com.vartmp7.stalker;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.vartmp7.stalker.datamodel.Organization;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtil {
    private static final String TAG="com.vartmp7.stalker.TestUtil";

    public static LifecycleOwner mockLifecycleOwner() {
        LifecycleOwner owner = mock(LifecycleOwner.class);
        LifecycleRegistry lifecycle = new LifecycleRegistry(owner);
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        when(owner.getLifecycle()).thenReturn(lifecycle);
        return owner;
    }
    /**/
    public static void updateOrganizationsFromOrganizationsLists(List<Organization> toUpdate, List<Organization> updater){
        List<Organization> toUpdateBackup = new ArrayList<>(toUpdate);
        toUpdate.clear();
        Log.d(TAG, "updateOrganizationsFromOrganizationsLists: "+toUpdateBackup.size());
        for(int i=0;i<updater.size();i++){
            Organization orgUpdated = updater.get(i);
            boolean contained=false;
            for (int j=0;j<toUpdateBackup.size() &&!contained;j++){
                Organization currentOrg = toUpdateBackup.get(j);
                if(orgUpdated.getId()==currentOrg.getId()){
                    Log.d(TAG, "preso "+orgUpdated.getId()+"name"+orgUpdated.getName());
                    contained=true;
                    orgUpdated.setTrackingActive(currentOrg.isTrackingActive());
                    orgUpdated.setTracking(currentOrg.isTracking());
                    orgUpdated.setFavorite(currentOrg.isFavorite());
                    toUpdate.add(orgUpdated);
                }else{
                    Log.d(TAG, "escluso "+orgUpdated.getId());
                }
            }
            if(!contained) toUpdate.add(orgUpdated);
        }

        toUpdate.forEach(o-> Log.d(TAG, "updateOrganizationsFromOrganizationsLists: "+o.getId()+" "+o.getName()));
    }

}
