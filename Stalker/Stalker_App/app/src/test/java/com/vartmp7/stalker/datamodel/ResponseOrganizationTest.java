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

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class ResponseOrganizationTest {

    private OrganizationResponse response;
    ArrayList<Organization> l;

    @Before
    public void setUp(){
        response = new OrganizationResponse();
         l = new ArrayList<>();
        l.add(new Organization().setName("UNIPD"));

        response.setOrganizations(l);
    }

    @Test
    public void testResponse(){
        assertEquals(l,l);
        assertEquals(l.hashCode(),l.hashCode());
        assertEquals(l, response.getOrganizations());

        OrganizationResponse re = new OrganizationResponse();
        re.setOrganizations(l);

        assertEquals(response, re);


        assertEquals(response.hashCode(), response.hashCode());


        assertEquals(response.getOrganizzationsLength(),1);
//        l.add(new Organizzazione().setName("boh"));

        assertEquals(l, response.getOrganizations());

        ArrayList<String> data = new ArrayList<>();
        data.add("Scegli un'organizzazione");
        data.add("UNIPD");

    }

}
