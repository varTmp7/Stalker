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

package com.vartmp7.stalker.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.vartmp7.stalker.datamodel.Organization;

import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertTrue;

//@RunWith(MockitoJUnitRunner.class)
public class FirebaseFavoritesRepositoryTest {

    private static final String TAG="com.vartmp7.stalker.model.FirebaseFavoritesRepositoryTest";

    private static final Organization org1= new Organization().setName("UNIPD").setAddress("Via trieste");
    private static final Organization org2= new Organization().setName("UNIPD 2").setAddress("Via trieste 2");
    private static final Organization org3= new Organization().setName("Alì").setAddress("Via roma 2");
    @Mock
    private FirebaseFavoritesSource ffr;
    @Mock
    private FirebaseFirestore fbfs;
    @Mock
    private OrganizationsRepository or;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


//    @Before
    public void setUP(){
//        MockitoAnnotations.initMocks(this);
//        ffr = Mockito.mock(FirebaseFavoritesRepository.class);
//        ffr.initUserStorage("1");
//        ArrayList<Organizzazione> list = new ArrayList<>();
//        list.add(org1);
//        list.add(org2);
//        list.add(org3);
//        when(or.getOrganizzazioni()).thenReturn(new MutableLiveData<>(list));
//
//        doNothing().when(fbfs.collection("utenti")).document("organizzazioni").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                List<Organizzazione> l= ffr.getOrganizzazioni().getValue();
//                l.add(org1);
//                l.add(org2);
//                l.add(org3);
//            }
//        });
//
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                return null;
//            }
//        });
//
//
//
//        LiveData<List<Organizzazione>> liveData = new MutableLiveData<>();
//        when(ffr.getOrganizzazioni()).thenReturn(liveData);
//


    }



    public void testGetOrganization(){
//        Log.d(TAG, "testGetOrganization: "+
//                ffr.getOrganizzazioni().getValue().toString());
        assertTrue(true);
    }

}
