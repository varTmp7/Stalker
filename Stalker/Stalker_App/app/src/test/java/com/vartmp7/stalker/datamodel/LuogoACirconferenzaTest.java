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


import com.vartmp7.stalker.datamodel.placecomponent.Coordinate;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class LuogoACirconferenzaTest {

    private static final Coordinate CENTRO = new Coordinate(0, 0);
    private static final double METERS = 111320D;
    private CircumferencePlace circonferenza;
    private Coordinate punto, centro;
    private boolean isInside;
    private Double raggio;


    public LuogoACirconferenzaTest(CircumferencePlace circonferenza, Coordinate centro, Coordinate punto, Double raggio, boolean isInside) {
        this.punto = punto;
        this.centro = centro;
        this.circonferenza = circonferenza;
        this.isInside = isInside;
        this.raggio = raggio;
    }

    @Test
    public void testIsInside() {
        assertEquals(circonferenza.isInside(punto), isInside);
    }

    @Test
    public void testEqualsHashCode() {
        CircumferencePlace c1 = new CircumferencePlace(1, "Cerchio");
        c1.setRaggio(raggio).setCenter(circonferenza.getCenter());

        assertEquals(c1, circonferenza);
        assertEquals(c1.hashCode(), circonferenza.hashCode());
    }
    @Test
    public void testGetCenter(){
        assertEquals(circonferenza.distanceTo(centro),0.0);
    }
    @Test
    public void testGetters() {
        assertEquals(circonferenza.getCenter(), CENTRO);
        assertEquals(circonferenza.getRaggio(), raggio);
    }


    @NotNull
    @Parameterized.Parameters
    public static Collection parametro() {
        CircumferencePlace c1 = new CircumferencePlace(1, "Cerchio");
        c1.setCenter(new Coordinate(0, 0)).setRaggio(METERS);

        Coordinate coordinate = new Coordinate(0, 0);
        CircumferencePlace c2 = new CircumferencePlace(1, "Cerchio", coordinate, METERS);


        coordinate = new Coordinate(0, 0);
        CircumferencePlace c3 = new CircumferencePlace(1, "Cerchio", coordinate, METERS, 10);

        CircumferencePlace c4 = new CircumferencePlace(1, "Cerchio");
        c4.setCenter(new Coordinate(0, 0)).setRaggio(METERS);
        CircumferencePlace c5 = new CircumferencePlace(1, "Cerchio");
        c5.setCenter(new Coordinate(0, 0)).setRaggio(METERS);

        return Arrays.asList(new Object[][]{
                {c1, CENTRO, new Coordinate(0.5, 0.5), METERS, true},
                {c2, CENTRO, new Coordinate(2, 2), METERS, false},
                {c3, CENTRO, new Coordinate(0, 1), METERS, true},
                {c4, CENTRO, new Coordinate(0.9, 0), METERS, true},
                {c5, CENTRO, new Coordinate(0.71, 0.71), METERS, false},
                {c5, CENTRO, new Coordinate(0.70, 0.70), METERS, true}
//                {, new Coordinata(45.411502, 11.888165), true}
        });
    }
}
