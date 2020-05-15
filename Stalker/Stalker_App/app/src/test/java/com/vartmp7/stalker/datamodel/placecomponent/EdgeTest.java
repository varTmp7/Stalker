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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class EdgeTest {

    private static final double DELTA=0d;
    private Edge edge;
    private double x1,y1,x2,y2;

    public EdgeTest(Edge edge, double x1, double y1, double x2, double y2) {
        this.edge = edge;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Test
    public void testLinesIntersectWhenItShouldNot(){
        Edge l=new Edge(new Coordinate(0,0),new Coordinate(1,1));
        Coordinate p = new Coordinate(3,3);
        assertFalse(l.linesIntersect(p));
    }

    @Test
    public void testLinesIntersectWhenPointIsOnEdge(){
        Edge l2=new Edge(new Coordinate(0,0),new Coordinate(1,1));
        Coordinate p2 = new Coordinate(0.5d,0.5d);
        assertTrue(l2.linesIntersect(p2));
    }


    @Test
    public void testLinesIntersectWhenItShould(){
        Edge l=new Edge(new Coordinate(0,0),new Coordinate(1,2));
        Coordinate p = new Coordinate(0,1);
        assertTrue(l.linesIntersect(p));
    }



    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        Edge l1 = new Edge(new Coordinate(0,0),new Coordinate(1,1));
        return Arrays.asList(new Object[][]{
                {l1,0,0,1,1}
        });
    }

    @Test
    public void testToStringHashCode(){
        Edge l = new Edge(new Coordinate(x1,y1),new Coordinate(x2,y2));

        assertEquals(edge,l);
        assertEquals(edge.hashCode(),l.hashCode());
    }

    @Test
    public void testGetters(){
        Edge l = new Edge(new Coordinate(x1,y1),new Coordinate(x2,y2));
        l.setStartX(x1);
        l.setStartY(y1);
        l.setEndX(x2);
        l.setEndY(y2);
        assertEquals(edge.getStartX(),l.getStartX(),DELTA);
        assertEquals(edge.getStartY(),l.getStartY(),DELTA);
        assertEquals(edge.getEndX(),l.getEndX(),DELTA);
        assertEquals(edge.getEndY(),l.getEndY(),DELTA);
    }
}
