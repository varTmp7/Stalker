package com.vartmp7.stalker.GsonBeans;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Retta {

    private double m;
    private double q;

    public Retta(Coordinata c1, Coordinata c2) {
        this.m = (c2.getLatitude() - c1.getLatitude()) / (c2.getLongitude() - c1.getLongitude());
        this.q = (((c2.getLatitude() - c1.getLatitude())*(-1)*c1.getLongitude()) / (c2.getLongitude() - c1.getLongitude())) + c1.getLatitude();

    }

    public Retta(float coeffangolare, float q) {
        this.m = coeffangolare;
        this.q = q;
    }

    public double calcoloLatitude(double longitude) {
        return this.m * longitude + this.q;
    }

    Coordinata intersezione(Retta r) {
        //System.out.println("x/:"+(-this.q + r.q));
        //System.out.println("/x:"+(this.m - r.m));
        double x = (-this.q + r.q) / (this.m - r.m);

        return new Coordinata(calcoloLatitude(x),x);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Retta) {
            Retta c = (Retta) obj;
            return c.m == this.m && c.q == this.q;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        if (q<=0){
            return  "y="+m+"x"+q;
        }
        return "y="+m+"x+"+q;
    }
}