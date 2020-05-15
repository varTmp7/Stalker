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


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

@RunWith(Parameterized.class)
public class LineTest {

    private static final Double DELTA = 0.00001d;
    private Line r;
    private Line rettaProva;
    private Coordinate coordinateRes;

    public LineTest(final Line r, final Coordinate c) {
        this.rettaProva = r;
        this.coordinateRes = c;
    }




    @Before
    public void initialize() {
        r = new Line(1, 0);
    }


    @Test
    public void testLineOrizzontale() {
        r = new Line(0, 0);
        assertEquals(r.calcoloLatitude(0), 0, DELTA);
    }

    @Test
    public void testLine() {
        r = new Line(3, 1);
        assertEquals(r.calcoloLatitude(1), 4, DELTA);
    }


    @Test
    public void testIntersezione() {
        r = new Line(3, 1);
        Line r1 = new Line(-1, 2);
        Coordinate c = r.intersezione(r1);
//        assertEquals(0.25,c.getLatitude(),DELTA);
//        assertEquals(1.75,c.getLongitude(),DELTA);

        assertEquals(c, new Coordinate(1.75f, 0.25f));
    }

    @Test
    public void testIntersezioneLineNulla() {
        r = new Line(3.0, 1.0);
        Line r2 = new Line(0, 1);
//        assertEquals(c, new Coordinata(0,1));
        assertEquals(r.getM(),3d);
        assertEquals(r.getQ(),1d);
    }

    @Test
    public void testIntersezioneRetteNulla() {
        r = new Line(1, 0);
        Line r2 = new Line(0, 1);
        Coordinate c = r.intersezione(r2);
        assertEquals(c, new Coordinate(1, 1));
    }

    // serie di test

    @Parameterized.Parameters
    public static Collection rette() {
        return Arrays.asList(new Object[][]{
                {new Line(1, 0), new Coordinate(1, 1)},
                {new Line(2, 1), new Coordinate(15, 7)}
        });
    }

    @Test
    public void testConSetRette() {
        System.out.println("retta: " + rettaProva);
        assertEquals(rettaProva.calcoloLatitude(coordinateRes.getLongitude()), coordinateRes.getLatitude(), DELTA);
    }
    @Test
    public void testEquals(){
        Line l1 = new Line(new Coordinate(1,1), new Coordinate(2,2));
        Line l2 = new Line(new Coordinate(1,1), new Coordinate(2,2));
        assertEquals(l1,l1);
        assertEquals(l1.hashCode(),l2.hashCode());
        assertEquals(l1,l2);
        l2 = new Line(new Coordinate(1,1), new Coordinate(2,3));
        assertFalse(l1.equals(l2));

    }


    @Test
    public void coordinateTorre() {

        Line r1 = new Line(new Coordinate(45.411555, 11.887476), new Coordinate(45.411108, 11.887787));
        Line r2 = new Line(new Coordinate(45.411442, 11.887942), new Coordinate(45.411222, 11.887319));
        Coordinate intersezione = r1.intersezione(r2);
        Coordinate c = new Coordinate(45.41133218, 11.88763102); // coordinata di intersezione
        // calcolata a mano
        assertEquals(c.getLatitude(), intersezione.getLatitude(), DELTA);
        assertEquals(c.getLongitude(), intersezione.getLongitude(), DELTA);

    }


}