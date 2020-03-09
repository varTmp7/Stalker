package com.vartmp7.stalker;

import android.os.IBinder;
import android.view.WindowManager;

import androidx.test.espresso.Root;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ToastMatcher  extends TypeSafeMatcher<Root> {
    @Override
    protected boolean matchesSafely(Root item) {

        int type = item.getWindowLayoutParams().get().type;

        if (type == WindowManager.LayoutParams.TYPE_TOAST){
            IBinder windowToken = item.getDecorView().getWindowToken();
            IBinder appToken = item.getDecorView().getApplicationWindowToken();
            //means this window isn't contained by any other windows.
            return windowToken == appToken;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("It's a toast!!");
    }
}
