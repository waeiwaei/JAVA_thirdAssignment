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

    public ArrayList<String> getAllArtefactNames() {
        ArrayList<String> artefactNames = new ArrayList<>();
        for (Cluster cluster : clusters) {
            for (Artefacts artefact : cluster.artefacts) {
                artefactNames.add(artefact.getName());
            }
        }
        return artefactNames;
    }

    public ArrayList<String> getAllFurnitureNames() {
        ArrayList<String> furnitureNames = new ArrayList<>();
        for (Cluster cluster : clusters) {
            for (Furniture furn : cluster.furnitures) {
                furnitureNames.add(furn.getName());
            }
        }
        return furnitureNames;
    }

    public ArrayList<String> getAllCharacterNames() {
        ArrayList<String> characterNames = new ArrayList<>();
        for (Cluster cluster : clusters) {
            for (Characters character : cluster.characters) {
                characterNames.add(character.getName());
            }
        }
        return characterNames;
    }

    public ArrayList<String> getLocationNames() {
        ArrayList<String> locationNames = new ArrayList<>();
        for (Cluster cluster : clusters) {
            locationNames.add(cluster.name);
        }
        return locationNames;
    }

}
