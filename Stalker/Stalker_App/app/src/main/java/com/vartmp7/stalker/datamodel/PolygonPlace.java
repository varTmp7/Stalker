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

import androidx.annotation.NonNull;

import com.vartmp7.stalker.datamodel.placecomponent.Coordinate;
import com.vartmp7.stalker.datamodel.placecomponent.Line;
import com.vartmp7.stalker.datamodel.placecomponent.RayCasting;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public class PolygonPlace extends AbstractPlace {
    @Getter
    @Setter
    @Accessors(chain = true)
    private List<Coordinate> coordinates;

    public PolygonPlace() {
        super(0, null);
    }

    PolygonPlace(long id, String name, List<Coordinate> coordinates) {
        super(id, name);
        this.coordinates = coordinates;
    }

    PolygonPlace(long id, String name, long num, List<Coordinate> coordinates) {
        super(id, name, num);
        this.coordinates = coordinates;
    }


    @NonNull
    @Override
    public String toString() {
        return "\nid: " + getId() +
                "\nOrgId: " + getOrgId() +
                "\nNome: " + getName() +
                "\nNum. Max Persone " + getNumMaxPeople() +
                "\nCoordinate: " + getCoordinates().toString();
    }


    @Override
    public Coordinate getCenter() {
        return new Line(coordinates.get(0), coordinates.get(2)).intersezione(new Line(coordinates.get(1), coordinates.get(3)));
    }

    public double distanceTo(Coordinate c) {
        return getCenter().getDistanceTo(c);
    }

    @Override
    public boolean isInside(Coordinate c) {
        return new RayCasting(getCoordinates()).isPointInside(c);
    }

}
