// Syncable object
// @author: msenol
package cis573.com.archaeology;
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