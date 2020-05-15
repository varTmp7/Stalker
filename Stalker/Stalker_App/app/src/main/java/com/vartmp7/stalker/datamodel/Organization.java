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

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public class Organization implements Serializable {
    public static final String BOTH= "both";
    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private";

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    @Accessors(chain = true)
    private String address;
    @Getter
    @Setter
    @Accessors(chain = true)
    private String city;
    @Getter
    @Setter
    @Accessors(chain = true)
    private String email;
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
    private String nation;
    @Getter
    @Setter
    @Accessors(chain = true)
    @SerializedName(value = "phone_number")
    private String phoneNumber;
    @Getter
    @Setter
    @Accessors(chain = true)
    @SerializedName(value = "postal_code")
    private String postalCode;
    @Getter
    @Setter
    @Accessors(chain = true)
    private String region;
    @Getter
    @Setter
    @Accessors(chain = true)
    private String type="public";
    @Getter
    @Setter
    @Accessors(chain = true)
    @SerializedName(value = "ldap_common_name")
    private String ldapCommonName;
    @Getter
    @Setter
    @Accessors(chain = true)
    @SerializedName(value = "ldap_domain_component")
    private String ldapDomainComponent;
    @Getter
    @Setter
    @Accessors(chain = true)
    @SerializedName(value = "ldap_port")
    private int ldapPort=389;
    @Getter
    @Setter
    @Accessors(chain = true)
    @SerializedName(value = "ldap_url")
    private String ldapUrl;
    @Getter
    @Setter
    @Accessors(chain = true)
    @SerializedName(value = "image_url")
    private String imageUrl="https://res.cloudinary.com/dyz86jubl/image/upload/v1587155106/iu_uvy5az.jpg";
    @Getter
    @Accessors(chain = true)
    private List<PolygonPlace> places = new ArrayList<>();

    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean isFavorite = false;
    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean isTracking = false;
    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean isLogged = false;
    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean isAnonymous = false;
    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean isTrackingActive = false;
    @Getter
    @Setter
    @Accessors(chain = true)
    private String personalCn;
    @Getter
    @Setter
    @Accessors(chain = true)
    private String ldapPassword;

    @NotNull
    @Override
    public String toString() {
        return "Organization{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", email='" + email + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", nation='" + nation + '\'' +
                ", phone_number='" + phoneNumber + '\'' +
                ", postal_code='" + postalCode + '\'' +
                ", region='" + region + '\'' +
                ", type='" + type + '\'' +
                ", ldap_common_name='" + ldapCommonName + '\'' +
                ", ldap_domain_component='" + ldapDomainComponent + '\'' +
                ", ldap_port=" + ldapPort +
                ", ldap_url='" + ldapUrl + '\'' +
                ", image_url='" + imageUrl + '\'' +
                ", places=" + places +
                ", isFavorite=" + isFavorite +
                ", isTracking=" + isTracking +
                ", isLogged=" + isLogged +
                ", isAnonymous=" + isAnonymous +
                ", isTrackingActive=" + isTrackingActive +
                ", personalCn='" + personalCn + '\'' +
                ", ldapPassword='" + ldapPassword + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization)) return false;
        Organization that = (Organization) o;
        return getId() == that.getId();
    }

    public Organization(@NotNull Organization org) {
        this.address=org.address;
        this.city=org.city;
        this.email=org.email;
        this.id=org.id;
        this.name=org.name;
        this.nation=org.nation;
        this.phoneNumber=org.phoneNumber;
        this.postalCode=org.postalCode;
        this.region=org.region;
        this.type=org.type;
        this.ldapCommonName=org.ldapCommonName;
        this.ldapDomainComponent=org.ldapDomainComponent;
        this.ldapPort=org.ldapPort;
        this.ldapUrl=org.ldapUrl;
        this.imageUrl=org.imageUrl;
        this.places=org.places;
        this.isFavorite=org.isFavorite;
        this.isTracking=org.isTracking;
        this.isLogged=org.isLogged;
        this.isAnonymous=org.isAnonymous;
        this.isTrackingActive=org.isTrackingActive;
        this.personalCn=org.personalCn;
        this.ldapPassword=org.ldapPassword;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Organization setPlaces(List<PolygonPlace> places){
        this.places = places;
        return this;
    }

    public Organization() {
    }


    public String getPlacesInfo() {
        StringBuilder builder = new StringBuilder();

        getPlaces().forEach(polygonPlace -> builder.append("Nome Luogo: ")
                .append(polygonPlace.getName())
                .append(", Num. persone massimo: ")
                .append(polygonPlace.getNumMaxPeople()).append("\n"));
        return builder.toString();
    }

}
