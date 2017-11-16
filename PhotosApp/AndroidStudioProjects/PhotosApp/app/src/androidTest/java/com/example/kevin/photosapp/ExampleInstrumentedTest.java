// Instrumentation test, which will execute on an Android device.
// @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// @author: Kevin Trinh
package com.example.kevin.photosapp;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest
{
    /**
     * Build app
     * @throws Exception if the app doesn't build.
     */
    @Test
    public void useAppContext() throws Exception
    {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.example.kevin.photosapp", appContext.getPackageName());
    }
}
