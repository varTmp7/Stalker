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

package com.vartmp7.stalker.component;

import com.google.android.gms.location.LocationRequest;

class TrackRequestCreator {
    private StalkerStepCounter stepCounter;


    private static final double MAXIMUM_DISTANCE = 10_000d;
    private static final long MAX_DISTANCE_AWAIT_TIME=10 *60*1000L; // dieci minuti


    private static final double INTERMEDIATE_DISTANCE = 5_000d;
    private static final long INTERMEDIATE_DISTANCE_AWAIT_TIME=5 *60*1000L; // cinque minuti


    private static final double ALMOST_MOST_PRECISE_DISTANCE = 1_000d;
    private static final long ALMOST_MOST_DISTANCE_AWAIT_TIME=3*60*1000L; // tre minuti


    private static final double MOST_PRECISE_DISTANCE = 100d;
    private static final long MOST_PRECISE_DISTANCE_AWAIT_TIME=60*1000L; // un minuto

    private static final int STEPS = 50;


    TrackRequestCreator(StalkerStepCounter stepCounter) {
        this.stepCounter = stepCounter;
    }

    /**
     * costruisce un LocationRequest in base alla distanza da una coordinata
     * @param distance distanza in double
     * @return restituisce un LocationRequest con determinate caratteristiche in base alla distanza. 
     */
    LocationRequest getNewRequest(double distance) {
        LocationRequest request = new LocationRequest();

        if (distance >= MAXIMUM_DISTANCE) {
            request.setSmallestDisplacement((float) MAXIMUM_DISTANCE)
                    .setInterval(MAX_DISTANCE_AWAIT_TIME)
                    .setPriority(LocationRequest.PRIORITY_NO_POWER)
            .setMaxWaitTime(10);
        } else if (distance >= INTERMEDIATE_DISTANCE) {
            request.setSmallestDisplacement((float) INTERMEDIATE_DISTANCE)
                    .setInterval(INTERMEDIATE_DISTANCE_AWAIT_TIME)
                    .setPriority(LocationRequest.PRIORITY_LOW_POWER);
        } else
            if (distance >= ALMOST_MOST_PRECISE_DISTANCE) {
            request.setSmallestDisplacement((float) ALMOST_MOST_PRECISE_DISTANCE)
                    .setInterval(ALMOST_MOST_DISTANCE_AWAIT_TIME)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        } else if(distance >= MOST_PRECISE_DISTANCE || stepCounter.getSteps() > STEPS)
        {
            stepCounter.resetSteps();
            request.setSmallestDisplacement((float) MOST_PRECISE_DISTANCE)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(MOST_PRECISE_DISTANCE_AWAIT_TIME);
        }
        return request;
    }

    LocationRequest getMostPrecise() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setMaxWaitTime(1000);
        mLocationRequest.setInterval(1_000);
        mLocationRequest.setSmallestDisplacement(2);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
}
