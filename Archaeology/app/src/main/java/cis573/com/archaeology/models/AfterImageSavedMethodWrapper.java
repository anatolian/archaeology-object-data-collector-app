// Saved image processing
// @author: msenol
package cis573.com.archaeology.models;
import android.net.Uri;
abstract public class AfterImageSavedMethodWrapper
{
    /**
     * Process saved image
     * @param thumbnailImageURI - image location
     */
    abstract public void doStuffWithSavedImage(Uri thumbnailImageURI);
}