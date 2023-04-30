package edu.uob;

import java.util.ArrayList;

public class Locations {
    public ArrayList<Cluster> clusters = new ArrayList<>();
    public static ArrayList<String> locationsList = new ArrayList<>();

    public Cluster getClusterByName(String name) {
        for (Cluster cluster : clusters) {
            if (cluster.name.equals(name)) {
                return cluster;
            }
        }
        // If no matching cluster found, return null
        return null;
    }
}
