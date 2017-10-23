// Placeholder entry
// @author: J. Patrick Taggart
package widac.cis350.upenn.edu.widac;
public class DummyEntry
{
    String ID;
    // Could define an enum for types
    String type;
    String locationData;
    // assume in grams
    double weight;
    double size;
    /**
     * Constructor
     * @param ID - placeholder id
     * @param type - placeholder item type
     * @param locationData - placeholder location data
     * @param weight - placeholder weight
     * @param size - placeholder size
     */
    DummyEntry(String ID, String type, String locationData, double weight, double size)
    {
        this.ID = ID;
        this.type = type;
        this.locationData = locationData;
        this.weight = weight;
        this.size = size;
    }
}