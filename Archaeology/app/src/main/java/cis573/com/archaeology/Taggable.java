// Taggable object
// @author: msenol
package cis573.com.archaeology;
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