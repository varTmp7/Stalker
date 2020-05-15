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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */

public class Coordinate {

    @Getter
    @Setter
    @Accessors(chain = true)
    private double latitude = 0;//y
    @Getter
    @Setter
    @Accessors(chain = true)
    private double longitude = 0; //x

    public Coordinate() {
    }

    public Coordinate(@NotNull Coordinate c) {
        latitude = c.getLatitude();
        longitude = c.getLongitude();
    }

    public Coordinate(double latitudine, double longitude) {
        this.latitude = latitudine;
        this.longitude = longitude;
    }



    @NonNull
    @Override
    public String toString() {
        return "\nLongitude(x): " + getLongitude() +
                "\nLatitude(y): " + getLatitude();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.getLatitude(), getLatitude()) == 0 &&
                Double.compare(that.getLongitude(), getLongitude()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLatitude(), getLongitude());
    }

    @Contract(pure = true)
    private double rad(double x) {
        return x * Math.PI / 180;
    }

    /**
     * restituisce la distanza in metri della coordinata corrente alla coordinata C
     * @param anotherCoordinate la seconda coordinata
     * @return la distanza in metri dalla coordinata corrente alla seconda coordinata
     */
    public double getDistanceTo(@NotNull final Coordinate anotherCoordinate){

        long R = 6378137; // Earth’s mean radius in meter
        double dLat = rad(anotherCoordinate.getLatitude() - getLatitude());
        double dLong = rad(anotherCoordinate.getLongitude() - getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(getLatitude())) * Math.cos(rad(anotherCoordinate.getLatitude())) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c1 = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c1; // returns the distance in meter
    }

}
