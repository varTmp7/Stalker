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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class LuogoPoligonoTest {

    private static final Double DELTA = 0.000011d;
    private PolygonPlace luogo;
    private Coordinate coordinate;
    private boolean inside;
    private double distance;

    public LuogoPoligonoTest(final PolygonPlace l, final Coordinate c, boolean inside, double distance) {
        this.luogo = l;
        this.coordinate = c;
        this.inside = inside;
        this.distance = distance;

    }



    @Test
    public void testDistanceTo(){
        System.out.println(luogo.getCenter()+"calculated"+ luogo.distanceTo(coordinate)+" expected"+distance+ " actual"+luogo.getCenter().getDistanceTo(coordinate));
        assertEquals(distance,luogo.distanceTo(coordinate),DELTA);
    }


    @Test
    public void testProva(){
        assertEquals(luogo.isInside(coordinate),inside);
    }
    @Test
    public void testConstructor4params(){
        PolygonPlace l = new PolygonPlace(luogo.getId(),luogo.getName(),luogo.getNumMaxPeople(),luogo.getCoordinates());

        assertEquals(l.getId(),luogo.getId() );
        assertEquals(l.getName(), luogo.getName());
        assertEquals(l.getCoordinates(), luogo.getCoordinates());
        assertEquals(l.getNumMaxPeople(), luogo.getNumMaxPeople());
    }
    @Test
    public void testConstructor3params(){
        PolygonPlace l = new PolygonPlace(luogo.getId(),luogo.getName(),  luogo.getCoordinates());

        assertEquals(l.getId(),luogo.getId() );
        assertEquals(l.getName(), luogo.getName());
        assertEquals(l.getCoordinates(), luogo.getCoordinates());
    }

    @Test
    public void testToString(){
        assert luogo.toString()!=null;
    }




    @Parameterized.Parameters
    public static Collection luoghi() {
        ArrayList<Coordinate> torreArchimede = new ArrayList<>();
        torreArchimede.add(new Coordinate(45.411555, 11.887476));
        torreArchimede.add(new Coordinate(45.411442, 11.887942));
        torreArchimede.add(new Coordinate(45.411108, 11.887787));
        torreArchimede.add(new Coordinate(45.411222, 11.887319));
        PolygonPlace t = new PolygonPlace();
        t.setId(1).setName("org").setNumMaxPeople(10);
        t.setCoordinates(torreArchimede);


        ArrayList<Coordinate> inail = new ArrayList<>();
        inail.add(new Coordinate(45.411660, 11.887027));
        inail.add(new Coordinate(45.411846, 11.887572));
        inail.add(new Coordinate(45.411730, 11.887650));
        inail.add(new Coordinate(45.411544, 11.887106));
        PolygonPlace i = new PolygonPlace();
        i.setId(1).setName("org").setNumMaxPeople(10);
        i.setCoordinates(inail);


        ArrayList<Coordinate> dsea = new ArrayList<>();
        dsea.add(new Coordinate(45.411660, 11.887957));
        dsea.add(new Coordinate(45.411702, 11.888113));
        dsea.add(new Coordinate(45.411341, 11.888381));
        dsea.add(new Coordinate(45.411284, 11.888224));
        PolygonPlace d= new PolygonPlace();
        d.setId(1).setName("org").setNumMaxPeople(10);
        d.setCoordinates(dsea);

        return Arrays.asList(new Object[][]{
                {t, new Coordinate( 45.411332,11.887631), true,0.02066094133453709},
                {i, new Coordinate(45.411695, 11.887339),true,0.011153516755715097},
                {i, new Coordinate(45.911695, 11.887339),false,55659.73783104104},
                {d, new Coordinate(45.411502, 11.888165), true,0.11660063237332076}
        });

    }



}
