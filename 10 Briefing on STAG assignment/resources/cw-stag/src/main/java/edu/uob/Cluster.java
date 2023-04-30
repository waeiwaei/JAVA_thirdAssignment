package edu.uob;

import java.util.ArrayList;

public class Cluster {

    public String name;
    public ArrayList<Artefacts> artefacts = new ArrayList<>();
    public ArrayList<Furniture> furnitures = new ArrayList<>();
    public ArrayList<Characters> characters = new ArrayList<>();
    public ArrayList<GameEntity> fullListEntitiesInCluster = new ArrayList<>();
    public String description;
    public ArrayList<String> paths = new ArrayList<>();

    public GameEntity getMatchingEntity(String name) {
        // Check for matching Artefacts
        for (Artefacts artefact : artefacts) {
            if (artefact.getName().equals(name)) {
                return artefact;
            }
        }

        // Check for matching Furniture
        for (Furniture furniture : furnitures) {
            if (furniture.getName().equals(name)) {
                return furniture;
            }
        }

        // Check for matching Characters
        for (Characters character : characters) {
            if (character.getName().equals(name)) {
                return character;
            }
        }

        // No match found, return null
        return null;
    }

}


//    public Artefacts getArtefact(String entity) {
//        for (Artefacts artefact : artefacts) {
//            if (artefact.getName().equals(entity)) {
//                return artefact;
//            }
//        }
//        return null;
//    }
//
//    public Furniture getFurniture(String entity) {
//        for (Furniture furn : furnitures) {
//            if (furn.getName().equals(entity)) {
//                return furn;
//            }
//        }
//        return null;
//    }
//
//
//    public Characters getCharacter(String entity) {
//        for (Characters character : characters) {
//            if (character.getName().equals(entity)) {
//                return character;
//            }
//        }
//        return null;
//    }
//