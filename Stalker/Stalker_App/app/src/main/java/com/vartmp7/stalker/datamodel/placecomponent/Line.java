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

import androidx.annotation.NonNull;

import java.util.Objects;


public class Line {

    private double m;


    public double getM() {
        return m;
    }

    public double getQ() {
        return q;
    }

    private double q;
    private Coordinate a;
    private Coordinate b;

    public Line(Coordinate c1, Coordinate c2) {
        this.m = (c2.getLatitude() - c1.getLatitude()) / (c2.getLongitude() - c1.getLongitude());
        this.q = (((c2.getLatitude() - c1.getLatitude())*(-1)*c1.getLongitude()) / (c2.getLongitude() - c1.getLongitude())) + c1.getLatitude();
        a= c1;
        b = c2;
    }

    public Line(double coeffangolare, double q) {
        this.m = coeffangolare;
        this.q = q;
    }

    public double calcoloLatitude(double longitude) {
        return this.m * longitude + this.q;
    }

    public Coordinate intersezione(Line r) {
        double x = (-this.q + r.q) / (this.m - r.m);

        return new Coordinate(calcoloLatitude(x),x);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Double.compare(line.getM(), getM()) == 0 &&
                Double.compare(line.getQ(), getQ()) == 0 &&
                Objects.equals(a, line.a) &&
                Objects.equals(b, line.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getM(), getQ(), a, b);
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