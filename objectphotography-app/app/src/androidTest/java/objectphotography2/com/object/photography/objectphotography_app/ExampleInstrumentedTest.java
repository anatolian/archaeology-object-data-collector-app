// Test case
// @author: msenol86, ygowda, and anatolian
package objectphotography2.com.object.photography.objectphotography_app;
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
     * Launch the app
     * @throws Exception if an error occurs
     */
    @Test
    public void useAppContext() throws Exception
    {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("photography.object.com.objectphotography", appContext.getPackageName());
    }
}