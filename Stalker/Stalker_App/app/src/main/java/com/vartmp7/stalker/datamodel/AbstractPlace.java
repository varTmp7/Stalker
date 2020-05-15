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

import com.google.gson.annotations.SerializedName;
import com.vartmp7.stalker.datamodel.placecomponent.Coordinate;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public abstract class AbstractPlace {
    @Getter
    @Setter
    @Accessors(chain = true)
    private long id;
    @Getter
    @Setter
    @Accessors(chain = true)
    private String name;
    @Getter
    @Setter
    @Accessors(chain = true)
    @SerializedName(value = "num_max_people")
    private long numMaxPeople;
    @Getter
    @Setter
    @Accessors(chain = true)
    private long orgId;

    AbstractPlace(long id, String name, long num) {
        this(id, name);
        this.numMaxPeople = num;
    }

    AbstractPlace(long id, String name) {
        this.id = id;
        this.name = name;
    }
    abstract Coordinate getCenter();
    abstract  double distanceTo(Coordinate c);
    abstract  boolean isInside(Coordinate c);
}
