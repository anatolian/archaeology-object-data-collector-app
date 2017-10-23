// Interface for implementations which retrieve and parse databases
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import java.io.InputStream;
public interface Retreiver
{
    //Returns a local copy of the db as an InputStream
    InputStream retrieve(String location);

    //Saves a local copy of the db to local storage
    void download(String url);

    //Searches for an item from retrieved db
    String[] search(String item);
}
