// Samples
// @author: ashutosh
package cis573.com.archaeology.models;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
public class Samples
{
    private List<Sample> samples;
    /**
     * public constructor is necessary for collections
     */
    public Samples()
    {
        samples = new ArrayList<>();
    }

    /**
     * Read JSON response
     * @param response - database response
     * @return Returns samples
     */
    public static Samples parseJSON(String response)
    {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, Samples.class);
    }

    /**
     * Get sample keys
     * @return Returns sample keys
     */
    public List<String> getCompositeKeys()
    {
        List<String> allContextNumbers = new ArrayList<>();
        for (Sample sample: samples)
        {
            allContextNumbers.add(sample.getCompositeKey());
        }
        return allContextNumbers;
    }

    /**
     * Represent as a string
     * @return Returns string representation
     */
    @Override
    public String toString()
    {
        StringBuilder samplesString = new StringBuilder("");
        for (Sample sample: samples)
        {
            samplesString.append(sample);
        }
        return samplesString.toString();
    }
}