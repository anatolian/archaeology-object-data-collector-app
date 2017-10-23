package widac.cis350.upenn.edu.widac.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ashutosh on 2/23/17.
 */

public class Sample {

    int area_easting;
    int area_northing;
    int context_number;
    int sample_number;
    String composite_key;
    String material;
    double weight;
    double size;
    int id;

    public Sample() {
        // Default constructor required for database operations
    }

    public Sample(int area_easting, int area_northing, int context_number, int sample_number,
                  String material, double weight, double size, int id) {
        this.area_easting = area_easting;
        this.area_northing = area_northing;
        this.context_number = context_number;
        this.sample_number = sample_number;
        this.material = material;
        this.weight = weight;
        this.composite_key = getCompositeKey();
        this.id = id;
    }

    public String getCompositeKey() {
        return Integer.toString(area_easting) + "-" + Integer.toString(area_northing)
                + "-" + Integer.toString(context_number) + "-" + Integer.toString(sample_number);
    }

    public static Sample parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        Sample sample = gson.fromJson(response, Sample.class);
        return sample;
    }

    public int getArea_easting() { return area_easting; };
    public int getArea_northing() { return area_northing; };
    public int getContext_number() {return  context_number; };
    public int getSample_number() { return sample_number; };

    public String getMaterial() { return material; };
    public double getWeight() { return weight; };

    public double getSize() {
        return size;
    }

    public int getId() { return id; };

    @Override
    public String toString() {
        return composite_key;
    }
}