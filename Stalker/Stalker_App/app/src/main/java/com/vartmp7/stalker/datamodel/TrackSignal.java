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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public class TrackSignal {
    // Annotazione expose usato indicare quale campo devo venir serializzato
    @Setter
    @Getter
    @Accessors(chain = true)
    private long idOrganization;
    @Setter
    @Getter
    @Accessors(chain = true)
    private long idPlace=0;

    public static final String LDAP_V3="ldapv3";
    public static final String GOOGLE="GOOGLE";
    public static final String FACEBOOK="FACEBOOK";

    @Setter
    @Accessors(chain = true)
    @Expose
    @SerializedName(value = "auth_type")
    private String authType=LDAP_V3;

    @Accessors(chain = true)
    @Expose @Setter
    private boolean entered=false;

    @Setter
    @Accessors(chain = true)
    @Getter
    @Expose
    private boolean authenticated=false;

    @Setter
    @Accessors(chain = true)
    @Expose
    @SerializedName(value = "date_time")
    private String dateTime;

    @Setter
    @Accessors(chain = true)
    @Expose
    private String username;

    @Setter
    @Accessors(chain = true)
    @Expose
    private String password;

    public TrackSignal(long idOrganization) {
        this.idOrganization = idOrganization;
    }

    public TrackSignal() {

    }

    @Override
    public String toString() {
        return "TrackSignal{" +
                "idOrganization=" + idOrganization +
                ", idPlace=" + idPlace +
                ", auth_type='" + authType + '\'' +
                ", entered=" + entered +
                ", authenticated=" + authenticated +
                ", date_time='" + dateTime + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackSignal)) return false;
        TrackSignal that = (TrackSignal) o;
        return idOrganization == that.idOrganization &&
                idPlace == that.idPlace &&
                entered == that.entered &&
                authenticated == that.authenticated &&
                Objects.equals(authType, that.authType) &&
                Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrganization, idPlace, authType, entered, authenticated, dateTime, username, password);
    }

}