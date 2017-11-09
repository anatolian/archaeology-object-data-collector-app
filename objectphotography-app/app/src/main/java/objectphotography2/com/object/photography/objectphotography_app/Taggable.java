// Taggable object
// @author: msenol
package objectphotography2.com.object.photography.objectphotography_app;
import android.graphics.Canvas;
public interface Taggable
{
    /**
     * Add tag
     * @param inputCanvas - image canvas
     * @param tagCharacter - image tag
     */
    void tagImage(Canvas inputCanvas, String tagCharacter);
}