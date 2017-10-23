// Splash Activity
// @author: JPT2, matthewliang, ashutosh56, and anatolian
package widac.cis350.upenn.edu.widac;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
public class SplashActivity extends AppCompatActivity
{
    /**
     * Launch activity
     * @param savedInstanceState - app state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}