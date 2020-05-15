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

import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.datamodel.OrganizationResponse;
import com.vartmp7.stalker.datamodel.PlaceResponse;
import com.vartmp7.stalker.datamodel.TrackHistory;
import com.vartmp7.stalker.datamodel.TrackRequest;
import com.vartmp7.stalker.datamodel.TrackSignal;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RestApiService {
    @Headers("Organization-Token: vartmp7")
    @GET("organizations")
    Call<OrganizationResponse> organizations();

    @Headers("Organization-Token: vartmp7")
    @GET("organizations/{organization_id}")
    Call<Organization> organization(@Path("organization_id") long id);

    @Headers("Organization-Token: vartmp7")
    @GET("organizations/{organization_id}/places")
    Call<PlaceResponse> organizationsPlaces(@Path("organization_id") long id);

    @Headers("Organization-Token: vartmp7")
    @GET("organizations/{organization_id}/places/{place_id}")
    Call<PlaceResponse> organizationsPlaceWithID(@Path("organization_id") long id, @Path("place_id") long placeId);

    @Headers("Organization-Token: vartmp7")
    @POST("organizations/{organization_id}/places/{place_id}/tracks")
    Call<Void> tracking(@Path("organization_id") long orgId, @Path("place_id") long placeId, @Body TrackSignal signal);

    @Headers("Organization-Token: vartmp7")
    @POST("organizations/{id_orgs}/tracks")
    Call<TrackHistory> getTracks(@Path("id_orgs") long id, @Body TrackRequest signal);
}
