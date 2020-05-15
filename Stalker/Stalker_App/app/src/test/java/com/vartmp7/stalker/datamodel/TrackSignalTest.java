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

package com.vartmp7.stalker.datamodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TrackSignalTest {

    private  static final long idOrganization=1;
    private  static final long idPlace=0;
    private  static final boolean entered=false;
    private  static final boolean authenticated=false;
    private  static final String username="username";
    private  static final String date_time="2020-03-19";

    private static final TrackSignal trackSignal= new TrackSignal()
            .setIdOrganization(idOrganization)
            .setEntered(entered)
            .setIdPlace(idPlace)
            .setAuthenticated(authenticated)
            .setUsername(username)
            .setDateTime(date_time);

    private TrackSignal signal ;
    private TrackSignal signal2 ;
    @Before
    public void setUP(){
        new TrackSignal(0L);
        signal =new TrackSignal()
                .setIdOrganization(idOrganization)
                .setEntered(entered)
                .setIdPlace(idPlace)
                .setAuthenticated(authenticated)
                .setUsername(username)
                .setDateTime(date_time);
        signal2= new TrackSignal()
                .setIdOrganization(idOrganization)
                .setEntered(entered)
                .setIdPlace(idPlace)
                .setAuthenticated(authenticated)
                .setUsername(username)
                .setDateTime(date_time);
    }

    @Test
    public void testEquals(){
        assertEquals(signal2.hashCode(), trackSignal.hashCode());
        assertEquals(signal2, trackSignal);
        assertEquals(signal2.toString(), trackSignal.toString());
        TrackSignal s = new TrackSignal()
                .setIdOrganization(idOrganization)
                .setEntered(entered)
                .setIdPlace(idPlace)
                .setAuthenticated(authenticated)
                .setUsername(username)
                .setDateTime(date_time)
                .setPassword("");

    }


    @Test
    public void testGetterSetter(){
        assertEquals(signal.getIdOrganization(),signal2.getIdOrganization());
        assertEquals(signal.getIdPlace(),signal2.getIdPlace());
        assertEquals(signal.isAuthenticated(),signal2.isAuthenticated());

    }
}
