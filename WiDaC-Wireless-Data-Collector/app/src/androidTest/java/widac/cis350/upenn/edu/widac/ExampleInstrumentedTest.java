// Test case
// @author: JPT2, matthewliang, ashutosh56, and anatolian
package widac.cis350.upenn.edu.widac;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
/**
 * Instrumentation test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest
{
    /**
     * Launch app
     * @throws Exception if app can't be launched
     */
    @Test
    public void useAppContext() throws Exception
    {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("widac.cis350.upenn.edu.widac", appContext.getPackageName());
    }
}