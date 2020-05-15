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

package com.vartmp7.stalker.datamodel.placecomponent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(Parameterized.class)
public class CoordinateTest {

    private final static double DELTA_METRI=0.1d;

    private Coordinate c1, c2;
    private double latitudine, longitudine;

    public CoordinateTest(Coordinate c1, Coordinate c2, double latitudine, double longitudine) {
        this.c1 = c1;
        this.c2 = c2;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    @Test
    public void testEquals(){
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Coordinate c = new Coordinate(1,1);
        return Arrays.asList(new Object[][]{
                {new Coordinate(0,1), new Coordinate(0,1),0,1,},
                {new Coordinate(),new Coordinate(),0,0},
                {c, new Coordinate(c),1,1},
        });
    }

    @Test
    public void testToString(){
        String s= "\nLongitude(x): "+longitudine+"\nLatitude(y): "+latitudine;

        assertEquals(c1.toString(), s);
    }


    @Test
    public void testGetters(){
        assertEquals(c1.getLatitude(),latitudine);
        assertEquals(c1.getLongitude(),longitudine);
    }
    @Test
    public void testSetters(){
        Coordinate c =new Coordinate();
        c1.setLatitude(latitudine);
        c1.setLongitude(longitudine);
        assertEquals(c1.getLongitude(), longitudine);
        assertEquals(c1.getLatitude(), latitudine);
    }

    @Test
    public void distanzaTraCoordinate(){
        Coordinate c1 = new Coordinate(45.411220, 11.887316);
        Coordinate c2 = new Coordinate(45.411110, 11.887791);

        assertEquals(c1.getDistanceTo(c2),39.04d,DELTA_METRI);
    }

}
