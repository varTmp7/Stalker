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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class OrganizationTest {
    private static final String address = "Via trieste";
    private static final String city = "Padova";
    private static final String email = "info@stalker.com";
    private static final long id = 1;
    private static final String name = "UNIPD";
    private static final String nation = "IT";
    private static final String phone_number = "0049xxxxxxx";
    private static final String postal_code = "35021";
    private static final String region = "Veneto";
    private static final String type = "private";
    private static final String ldap_common_name = "com.unipd";
    private static final String ldap_domain_component = "cn=wen,cn=xiaowei";
    private static final int ldap_port = 250;
    private static final String ldap_url = "ulr";
    private static final String image_url = "ulr";
    private static final String personalCN = "ulr";
    private static final String ldapPassword = "ulr";
    private static final List<PolygonPlace> luoghi = new ArrayList<>();
    private static final Organization org = new Organization()
            .setAddress(address)
            .setCity(city)
            .setEmail(email)
            .setId(id)
            .setName(name)
            .setLdapPassword(ldapPassword)
            .setPersonalCn(personalCN)
            .setNation(nation)
            .setPhoneNumber(phone_number)
            .setPostalCode(postal_code)
            .setRegion(region)
            .setType(type)
            .setLdapCommonName(ldap_common_name)
            .setLdapDomainComponent(ldap_domain_component)
            .setLdapPort(ldap_port)
            .setLdapUrl(ldap_url)
            .setImageUrl(image_url)
            .setPlaces(luoghi)
            .setFavorite(false)
            .setTracking(false)
            .setTrackingActive(false)
            .setLogged(false)
            .setAnonymous(false)
            .setPersonalCn("cn")
            .setLdapPassword("www");


    private Organization organization;

    @Before
    public void setUp() throws Exception {
        organization = new Organization()
                .setAddress(address)
                .setCity(city)
                .setEmail(email)
                .setId(id)
                .setName(name)
                .setNation(nation)
                .setPhoneNumber(phone_number)
                .setPostalCode(postal_code)
                .setRegion(region)
                .setType(type)
                .setLdapCommonName(ldap_common_name)
                .setLdapDomainComponent(ldap_domain_component)
                .setLdapPort(ldap_port)
                .setLdapUrl(ldap_url)
                .setLdapPassword(ldapPassword)
                .setPersonalCn(personalCN)
                .setImageUrl(image_url)
                .setPlaces(luoghi)
                .setFavorite(false)
                .setTracking(false)
                .setTrackingActive(false)
                .setLogged(false)
                .setAnonymous(false)
                .setPersonalCn("cn")
                .setLdapPassword("www");
    }


    @Test
    public void testEqualsHashCode() {
        assertEquals(org, organization);
        assertEquals(organization.toString(), organization.toString());
        assertEquals(organization.hashCode(), organization.hashCode());
        Organization newOrg = new Organization(organization);
        assertEquals(newOrg.toString(), organization.toString());

    }



    @Test
    public void testGetterSetter() {

        assertEquals(org.getAddress(), organization.getAddress());
        assertEquals(org.getCity(), organization.getCity());
        assertEquals(org.getEmail(), organization.getEmail());
        assertEquals(org.getId(), organization.getId());
        assertEquals(org.getName(), organization.getName());
        assertEquals(org.getNation(), organization.getNation());
        assertEquals(org.getPhoneNumber(), organization.getPhoneNumber());
        assertEquals(org.getPostalCode(), organization.getPostalCode());
        assertEquals(org.getRegion(), organization.getRegion());
        assertEquals(org.getType(), organization.getType());
        assertEquals(org.getLdapCommonName(), organization.getLdapCommonName());
        assertEquals(org.getLdapDomainComponent(), organization.getLdapDomainComponent());
        assertEquals(org.getLdapPort(), organization.getLdapPort());
        assertEquals(org.getLdapUrl(), organization.getLdapUrl());
        assertEquals(org.getLdapPassword(), organization.getLdapPassword());
        assertEquals(org.getPersonalCn(), organization.getPersonalCn());
        assertEquals(org.isFavorite(), organization.isFavorite());
        assertEquals(org.hashCode(), org.hashCode());

        assertEquals(org, org);
//        assertEquals(org.getPlacesInfo(), "");
        List<PolygonPlace> p = new ArrayList<>();
        p.add((PolygonPlace) new PolygonPlace().setName("Unipd").setNumMaxPeople(100));
        org.setPlaces(p);
        assertEquals(org.getPlacesInfo(), "Nome Luogo: Unipd, Num. persone massimo: 100\n");


    }


}
