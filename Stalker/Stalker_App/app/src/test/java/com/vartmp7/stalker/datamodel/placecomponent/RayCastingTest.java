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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class RayCastingTest {
    private RayCasting raycasting;
    private boolean isInside;
    private Coordinate coordinate;
    public RayCastingTest(RayCasting raycasting, Coordinate coordinate,boolean isInside) {
       this.raycasting=raycasting;
       this.isInside=isInside;
       this.coordinate = coordinate;
    }
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Coordinate> coordinate = Arrays.asList(
                new Coordinate(0,0),
                new Coordinate(1,1),
                new Coordinate(0,2),
                new Coordinate(-1,1)
        );
        //RayCasting raycasting = new RayCasting(coordinate,new Coordinata(0,1));
        return Arrays.asList(new Object[][]{
                {new RayCasting(coordinate),new Coordinate(0,1),true},
                {new RayCasting(coordinate),new Coordinate(0.01d,2),false}
        });
    }

    @Test
    public void test(){
        assertEquals(raycasting.isPointInside(coordinate),isInside);
    }

    @Test
    public void testIsInsideWhenItShould(){
        List<Coordinate> coordinate = Arrays.asList(
                new Coordinate(0,0),
                new Coordinate(1,1),
                new Coordinate(0,2),
                new Coordinate(-1,1)
        );
        RayCasting raycasting = new RayCasting(coordinate);
        assertTrue(raycasting.isPointInside(new Coordinate(0,1)));
    }




}
