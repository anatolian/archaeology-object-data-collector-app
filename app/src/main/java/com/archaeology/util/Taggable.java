// Taggable object
// @author: msenol
package com.archaeology.util;
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