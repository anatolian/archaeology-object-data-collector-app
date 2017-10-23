// Saved image processing
// @author: msenol
package objectphotography2.com.object.photography.objectphotography_app;
import android.net.Uri;
abstract public class AfterImageSavedMethodWrapper
{
    /**
     * Process saved image
     * @param thumbnailImageUri - image location
     */
    abstract public void doStuffWithSavedImage(Uri thumbnailImageUri);
}