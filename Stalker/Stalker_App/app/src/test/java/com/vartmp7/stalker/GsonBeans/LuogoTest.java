package com.vartmp7.stalker.GsonBeans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class LuogoTest {


    public static final Double DELTA = 0.000011d;
    private Luogo luogo;
    private Coordinata coordinata;

    public LuogoTest(final Luogo l, final Coordinata c) {
        this.luogo = l;
        this.coordinata = c;
    }


    @Test
    public void provaGetCentroLuogo() {
        assertEquals(coordinata.getLongitude(), luogo.getCentro().getLongitude(), DELTA);
        assertEquals(coordinata.getLatitude(), luogo.getCentro().getLatitude(), DELTA);
    }


    @Parameterized.Parameters
    public static Collection luoghi() {
        ArrayList<Coordinata> torreArchimede = new ArrayList<>();
        torreArchimede.add(new Coordinata(45.411555, 11.887476));
        torreArchimede.add(new Coordinata(45.411442, 11.887942));
        torreArchimede.add(new Coordinata(45.411108, 11.887787));
        torreArchimede.add(new Coordinata(45.411222, 11.887319));
        Luogo t = new Luogo();
        t.setCoordinate(torreArchimede);


        ArrayList<Coordinata> inail = new ArrayList<>();
        inail.add(new Coordinata(45.411660, 11.887027));
        inail.add(new Coordinata(45.411846, 11.887572));
        inail.add(new Coordinata(45.411730, 11.887650));
        inail.add(new Coordinata(45.411544, 11.887106));
        Luogo i = new Luogo();
        i.setCoordinate(inail);
        
        
        ArrayList<Coordinata> dsea = new ArrayList<>();
        dsea.add(new Coordinata(45.411660, 11.887957));
        dsea.add(new Coordinata(45.411702, 11.888113));
        dsea.add(new Coordinata(45.411341, 11.888381));
        dsea.add(new Coordinata(45.411284, 11.888224));
        Luogo d= new Luogo();
        d.setCoordinate(dsea);

        return Arrays.asList(new Object[][]{
                {t, new Coordinata( 45.411332,11.887631)},
                {i, new Coordinata(45.411695, 11.887339)},
                {d, new Coordinata(45.411502, 11.888165)}
        });

    }



}
