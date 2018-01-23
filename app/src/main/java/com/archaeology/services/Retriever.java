// Interface for implementations which retrieve and parse databases
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.archaeology.services;
import java.io.InputStream;
public interface Retriever
{
    /**
     * Returns a local copy of the db as an InputStream
     * @param location - db location
     * @return Returns a file reader
     */
    InputStream retrieve(String location);

    /**
     * Searches for an item from retrieved db
     * @param item - key to search
     * @return Returns the results
     */
    String[] search(String item);
}