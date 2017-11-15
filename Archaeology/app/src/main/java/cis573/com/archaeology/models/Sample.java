// Sample
// @author: ashutosh
package cis573.com.archaeology.models;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
public class Sample
{
    private int area_easting;
    private int area_northing;
    private int context_number;
    private int sample_number;
    private String composite_key;
    private String material;
    private double weight;
    private double size;
    private int id;
    /**
     * Default constructor required for database operations
     */
    public Sample()
    {
    }

    /**
     * Constructor
     * @param area_easting - easting
     * @param area_northing - northing
     * @param context_number - context
     * @param sample_number - sample
     * @param material - material artifact
     * @param weight - artifact weight
     * @param size - artifact size
     * @param id - artifact id
     */
    public Sample(int area_easting, int area_northing, int context_number, int sample_number,
                  String material, double weight, double size, int id)
    {
        this.area_easting = area_easting;
        this.area_northing = area_northing;
        this.context_number = context_number;
        this.sample_number = sample_number;
        this.material = material;
        this.weight = weight;
        this.composite_key = getCompositeKey();
        this.id = id;
    }

    /**
     * Get the key
     * @return Returns the key
     */
    public String getCompositeKey()
    {
        return Integer.toString(area_easting) + "-" + Integer.toString(area_northing)
                + "-" + Integer.toString(context_number) + "-" + Integer.toString(sample_number);
    }

    /**
     * Read a JSON
     * @param response - database response
     * @return Returns the sample
     */
    public static Sample parseJSON(String response)
    {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, Sample.class);
    }

    /**
     * Get easting
     * @return Returns easting
     */
    public int getArea_easting()
    {
        return area_easting;
    }

    /**
     * Get northing
     * @return Returns northing
     */
    public int getArea_northing()
    {
        return area_northing;
    }

    /**
     * Get context
     * @return Returns context
     */
    public int getContext_number()
    {
        return  context_number;
    }

    /**
     * Get sample
     * @return Returns sample
     */
    public int getSample_number()
    {
        return sample_number;
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
     * Get size
     * @return Returns size
     */
    public double getSize()
    {
        return size;
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
        return composite_key;
    }
}