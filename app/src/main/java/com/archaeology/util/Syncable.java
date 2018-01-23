// Syncable object
// @author: msenol
package com.archaeology.util;
public interface Syncable
{
    /**
     * Set status
     * @param syncStatus - status of sync
     */
    void setSyncStatus(String syncStatus);

    /**
     * Get status
     * @return Returns sync status
     */
    String getSyncStatus();
}