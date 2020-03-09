package com.vartmp7.stalker.GsonBeans;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RettaTest {

    private static final Double DELTA = 0.00001d;
    private Retta r;

    private Retta rettaProva;
    private Coordinata coordinataRes;

    public RettaTest(final Retta r, final Coordinata c) {
        this.rettaProva = r;
        this.coordinataRes = c;
    }

    @Before
    public void initialize() {
        r = new Retta(1, 0);
    }


    @Test
    public void testRettaOrizzontale() {
        r = new Retta(0, 0);

        assertEquals(r.calcoloLatitude(0), 0, DELTA);
    }

    @Test
    public void testRetta() {
        r = new Retta(3, 1);

        assertEquals(r.calcoloLatitude(1), 4, DELTA);
    }


    @Test
    public void testIntersezione() {
        r = new Retta(3, 1);
        Retta r1 = new Retta(-1, 2);
        Coordinata c = r.intersezione(r1);
//        assertEquals(0.25,c.getLatitude(),DELTA);
//        assertEquals(1.75,c.getLongitude(),DELTA);

        assertEquals(c, new Coordinata(1.75f, 0.25f));
    }

    @Test
    public void testIntersezioneRettaNulla() {
        r = new Retta(3, 1);
        Retta r2 = new Retta(0, 1);
        Coordinata c = r.intersezione(r2);
//        assertEquals(c, new Coordinata(0,1));
    }

    @Test
    public void testIntersezioneRetteNulla() {
        r = new Retta(1, 0);
        Retta r2 = new Retta(0, 1);
        Coordinata c = r.intersezione(r2);
        assertEquals(c, new Coordinata(1, 1));
    }

    @Test
    public void testRettaPerDueCoordinate() {
        Coordinata c1 = new Coordinata(1, 1);
        Coordinata c2 = new Coordinata(2, 2);

        r = new Retta(c1, c2);

        assertEquals(new Retta(1f, 0f), r);
    }

    // serie di test

    @Parameterized.Parameters
    public static Collection rette() {
        return Arrays.asList(new Object[][]{
                {new Retta(1, 0), new Coordinata(1, 1)},
                {new Retta(2, 1), new Coordinata(15, 7)}
                // todo aggiungere altre rette e punti da calcolare
        });
    }

    @Test
    public void testConSetRette() {
        System.out.println("retta: " + rettaProva);
        assertEquals(rettaProva.calcoloLatitude(coordinataRes.getLongitude()), coordinataRes.getLatitude(), DELTA);
    }


    @Test
    public void coordinateTorre() {

        Retta r1 = new Retta(new Coordinata(45.411555, 11.887476), new Coordinata(45.411108, 11.887787));
        Retta r2 = new Retta(new Coordinata(45.411442, 11.887942), new Coordinata(45.411222, 11.887319));
        Coordinata intersezione = r1.intersezione(r2);
        Coordinata c = new Coordinata(45.41133218, 11.88763102); // coordinata di intersezione
        // calcolata a mano
        assertEquals(c.getLatitude(), intersezione.getLatitude(), DELTA);
        assertEquals(c.getLongitude(), intersezione.getLongitude(), DELTA);

    }


}
