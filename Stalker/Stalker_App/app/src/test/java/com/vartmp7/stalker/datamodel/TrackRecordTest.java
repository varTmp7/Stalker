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

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class TrackRecordTest {
    private String orgName;
    private long placeId;
    private PolygonPlace place;
    private String dateTime;
    private boolean entered;
    private TrackRecord record;
    private String placeName ;

    @Before
    public void setUp() throws Exception {
        orgName ="Math UNIPD";
        placeId=1;
        place = new PolygonPlace();
        dateTime="Oggi";
        entered=false;
        placeName="Torre";
        place.setName(placeName);

        record = new TrackRecord()
                .setDateTime(dateTime)
                .setEntered(entered)
                .setOrgName(orgName)
                .setPlace(place)
                .setPlaceId(placeId)
        .setPlaceName(placeName);
    }

    @Test
    public void testToString() {
        assertEquals(record.toString(),"TrackRecord{orgName='Math UNIPD', placeName='Torre', placeId=1, dateTime='Oggi', entered=false}");
    }

    @Test
    public void getPlaceName() {
        assertEquals(record.getOrgName(),orgName);
        assertEquals(record.getDateTime(),dateTime);
        assertEquals(record.isEntered(),entered);
        assertEquals(record.getPlaceId(),placeId);
        assertEquals(record.getPlace(),place);
        assertEquals(record.getPlaceName(),placeName);
    }

}