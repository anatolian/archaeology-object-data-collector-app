// Sample
// @author: ashutosh
package cis573.com.archaeology.models;
public class Sample
{
    private int areaEasting;
    private int areaNorthing;
    private int contextNumber;
    private int sampleNumber;
    private String compositeKey;
    private String material;
    private double weight;
    private int id;
    /**
     * Default constructor required for database operations
     */
    @SuppressWarnings("unused")
    public Sample()
    {
    }

    /**
     * Constructor
     * @param areaEasting - easting
     * @param areaNorthing - northing
     * @param contextNumber - context
     * @param sampleNumber - sample
     * @param material - material artifact
     * @param weight - artifact weight
     * @param size - artifact size
     * @param id - artifact id
     */
    @SuppressWarnings("unused")
    public Sample(int areaEasting, int areaNorthing, int contextNumber, int sampleNumber,
                  String material, double weight, double size, int id)
    {
        this.areaEasting = areaEasting;
        this.areaNorthing = areaNorthing;
        this.contextNumber = contextNumber;
        this.sampleNumber = sampleNumber;
        this.material = material;
        this.weight = weight;
        this.compositeKey = getCompositeKey();
        this.id = id;
    }

    /**
     * Get the key
     * @return Returns the key
     */
    public String getCompositeKey()
    {
        return Integer.toString(areaEasting) + "-" + Integer.toString(areaNorthing)
                + "-" + Integer.toString(contextNumber) + "-" + Integer.toString(sampleNumber);
    }

    /**
     * Get material
     * @return Returns material
     */
    public String getMaterial()
    {
        return material;
    }

    /**
     * Get weight
     * @return Returns weight
     */
    public double getWeight()
    {
        return weight;
    }

    /**
     * Get id
     * @return Returns id
     */
    public int getId() { return id; };

    /**
     * Express as a string
     * @return Returns string representation
     */
    @Override
    public String toString()
    {
        return compositeKey;
    }
}