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

package com.vartmp7.stalker.ui.organizations;


import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.vartmp7.stalker.R;
import com.vartmp7.stalker.ui.SingleFragmentActivity;
import com.vartmp7.stalker.datamodel.Organization;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class OrganizationsFragmentTest{
    private OrganizationsFragment fragment;
    private OrganizationsViewModel viewModel;

    private List<Organization> organizations;

    @Rule
    public ActivityTestRule<SingleFragmentActivity> activityRule = new ActivityTestRule<>(SingleFragmentActivity.class, true, true);

    @Before
    public void setup(){
        viewModel = mock(OrganizationsViewModel.class);
        when(viewModel.getOrganizationList()).then(invoker->{
            MutableLiveData<List<Organization>> liveOrganizations = new MutableLiveData<>();
            liveOrganizations.setValue(organizations);
            return liveOrganizations;
        });

        //OrganizationsFragment fragment = OrganizationsFragment.newInstance()
//        try {
//
//            Field viewModelField = OrganizationsFragment.class.getField("organizzazioneViewModel");
//            viewModelField.setAccessible(true);
//            viewModelField.set(viewModelField.get(fragment),viewModel);
//
//
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }


      //  activityRule.getActivity().setFragment();

    }

    @Test
    public void testSwipeDown(){
        //assertTrue(false);

        onView(withId(R.id.srfl)).perform(swipeDown());
    }



}
