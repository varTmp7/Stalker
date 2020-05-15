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

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.datamodel.OrganizationResponse;
import com.vartmp7.stalker.datamodel.PolygonPlace;
import com.vartmp7.stalker.datamodel.TrackHistory;
import com.vartmp7.stalker.datamodel.TrackRecord;
import com.vartmp7.stalker.datamodel.TrackRequest;
import com.vartmp7.stalker.datamodel.placecomponent.Coordinate;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RESTObtainer implements Obtainer {


    private static final String TAG = "com.vartmp7.stalker.repository.RESTObtainer";
    private RestApiService service;

    private MutableLiveData<List<Organization>> mutableLiveDataOrganizzazioni;
    private MutableLiveData<List<TrackRecord>> trackRecords;


    @Inject
    public RESTObtainer(RestApiService service) {
        this.service = service;
        mutableLiveDataOrganizzazioni = new MutableLiveData<>();
        trackRecords = new MutableLiveData<>(new ArrayList<>());
    }


    @Override
    public LiveData<List<Organization>> getOrganizations() {

        Call<OrganizationResponse> organizations = service.organizations();
        organizations.enqueue(new Callback<OrganizationResponse>() {
            @SneakyThrows
            @Override
            public void onResponse(@NotNull Call<OrganizationResponse> call, @NotNull Response<OrganizationResponse> response) {
                List<Organization> organizations1= new ArrayList<>();
                if (response.body()!=null){
                    organizations1 = response.body().getOrganizations();
                }
                mutableLiveDataOrganizzazioni.postValue(organizations1);
            }

            @Override
            public void onFailure(@NotNull Call<OrganizationResponse> call, @NotNull Throwable throwable) {
                Log.d(TAG, "onFailure: " + throwable.getMessage());
                ArrayList<Coordinate> torreArchimede = new ArrayList<>();
                torreArchimede.add(new Coordinate(45.411555, 11.887476));
                torreArchimede.add(new Coordinate(45.411442, 11.887942));
                torreArchimede.add(new Coordinate(45.411108, 11.887787));
                torreArchimede.add(new Coordinate(45.411222, 11.887319));
                PolygonPlace t = new PolygonPlace();
                t.setId(1).setName("Torre Archimede").setNumMaxPeople(10);
                t.setCoordinates(torreArchimede).setOrgId(1);

                ArrayList<Coordinate> dsea = new ArrayList<>();
                dsea.add(new Coordinate(45.411660, 11.887957));
                dsea.add(new Coordinate(45.411702, 11.888113));
                dsea.add(new Coordinate(45.411341, 11.888381));
                dsea.add(new Coordinate(45.411284, 11.888224));
                PolygonPlace d = new PolygonPlace();
                d.setId(2).setName("TORRE 3C").setNumMaxPeople(10);
                d.setCoordinates(dsea).setOrgId(2);


                List<Organization> orgs = Arrays.asList(
                        new Organization().setId(1)
                                .setName("UniPD Dipartimento di Matematica")
                                .setTracking(true)
                                .setAddress("Trieste n. 8")
                                .setCity("Padova")
                                .setRegion("Padova")
                                .setNation("IT")
                                .setPostalCode("35921")
                                .setPhoneNumber("049********")
                                .setLdapDomainComponent("dc=daf,dc=test,dc=it")
                                .setLdapCommonName("cn=")
                                .setLdapPort(389)
                                .setLdapUrl("iuasdoas")
                                .setType("both")
                                .setEmail("info@math.unipd.it")
//                                .setImage_url("https://upload.wikimedia.org/wikipedia/it/thumb/5/53/Logo_Universit%C3%A0_Padova.svg/1200px-Logo_Universit%C3%A0_Padova.svg.png")
                                .setImageUrl("https://pbs.twimg.com/profile_images/1173976802416582657/LCZXVSqH_400x400.jpg")
                                .setPlaces(Collections.singletonList(t)),
                        new Organization()
                                .setId(2)
                                .setName("UNIPD dSeA")
                                .setType(Organization.PRIVATE)
                                .setTracking(false)
//                                .setImageUrl("https://www.economia.unipd.it/sites/economia.unipd.it/files/img-logo-trentennale-dsea-big.png")
                                .setPlaces(Collections.singletonList(d)),
                        new Organization()
                                .setId(3)
                                .setName("UNIPD DSFARM")
                                .setType(Organization.PUBLIC)
                                .setTracking(true)
//                                .setImageUrl("https://www.dsfarm.unipd.it/sites/dsfarm.unipd.it/files/sublogo_9.png")

                );
                mutableLiveDataOrganizzazioni.postValue(orgs);
            }


        });
        return mutableLiveDataOrganizzazioni;
    }

    @Override
    public LiveData<List<TrackRecord>> getTrackRecords() {
        return trackRecords;

    }

    @Override
    public void updateTrackRecords(List<Organization> orgs) {
        ArrayList<TrackRecord> mockedTrackRecords = new ArrayList<>();
        if (orgs.size() == 0 || orgs.stream().noneMatch(Organization::isLogged)) {
            trackRecords.postValue(mockedTrackRecords);
            return;
        }
        orgs.forEach(org -> {
            TrackRequest trackRequest = new TrackRequest();
            trackRequest.setUserName(org.getPersonalCn());
            trackRequest.setPassword(org.getLdapPassword());
            Call<TrackHistory> tracks = service.getTracks(org.getId(), trackRequest);
            tracks.enqueue(new Callback<TrackHistory>() {
                @Override
                public void onResponse(@NotNull Call<TrackHistory> call, @NotNull Response<TrackHistory> response) {
                    if (response.body() != null) {
                        mockedTrackRecords.addAll(response.body().getTracks());
                    }
                    trackRecords.postValue(mockedTrackRecords);
                }

                @Override
                public void onFailure(@NotNull Call<TrackHistory> call, @NotNull Throwable t) {
                    trackRecords.postValue(mockedTrackRecords);
                }
            });
        });


    }


}
