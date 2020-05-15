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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 * @version 1.0
 * <p>
 * Usato per rappresentare dei luoghi con una forma di circonferenza.
 */
public class CircumferencePlace extends AbstractPlace {

    @Setter
    @Accessors(chain = true)
    private Coordinate center;
    @Getter
    @Setter
    @Accessors(chain = true)
    private Double raggio;

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CircumferencePlace)) return false;
        CircumferencePlace that = (CircumferencePlace) o;
        return getCenter().equals(that.getCenter()) &&
                getRaggio().equals(that.getRaggio());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCenter(), getRaggio());
    }

    public CircumferencePlace(long id, String name) {
        super(id, name);
    }

    @Override
    public Coordinate getCenter() {
        return center;
    }

    @Override
    public double distanceTo(@NotNull Coordinate c) {
        return c.getDistanceTo(center);
    }

    CircumferencePlace(long id, String name, Coordinate center, double raggio) {
        super(id, name);
        this.center = center;
        this.raggio = raggio;
    }

    CircumferencePlace(long id, String name, Coordinate center, double raggio, long num) {
        super(id, name, num);
        this.center = center;
        this.raggio = raggio;
    }

    @Override
    public boolean isInside(Coordinate c) {
        double distanza = center.getDistanceTo(c);
        return distanza <= raggio;
    }

}
