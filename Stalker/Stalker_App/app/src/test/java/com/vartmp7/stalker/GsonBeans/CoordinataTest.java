package com.vartmp7.stalker.GsonBeans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class CoordinataTest {

    private final static double DELTA_METRI=0.1d;
//    @Parameterized.Parameter
//    public static Collection dati(){
//        return new Colle
//    }

    @Test
    public void distanzaTraCoordinate(){
        Coordinata c1 = new Coordinata(45.411220, 11.887316);
        Coordinata c2 = new Coordinata(45.411110, 11.887791);

        assertEquals(c1.getDistanceTo(c2),39.04d,DELTA_METRI);
    }

}
