package widac.cis350.upenn.edu.widac.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ashutosh on 4/27/17.
 */

public class Samples {

    List<Sample> samples;

    // public constructor is necessary for collections
    public Samples() {
        samples = new ArrayList<Sample>();
    }

    public static Samples parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        Samples samples = gson.fromJson(response, Samples.class);
        return samples;
    }

    public List<String> getCompositeKeys() {
        List<String> allContextNumbers = new ArrayList<>();
        for (Sample sample: samples) {
            allContextNumbers.add(sample.getCompositeKey());
        }
        return allContextNumbers;
    }

    @Override
    public String toString() {
        String samplesString = "";
        for (Sample sample : samples) {
            samplesString += sample;
        }
        return samplesString;
    }
}
