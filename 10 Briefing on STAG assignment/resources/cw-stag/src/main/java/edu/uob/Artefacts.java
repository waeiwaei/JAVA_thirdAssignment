package edu.uob;

import java.util.ArrayList;

public class Artefacts extends GameEntity{

    public static ArrayList<String> listOfAllArtefactsNames = new ArrayList<>();
    public Artefacts(String name, String description) {
        super(name, description);
    }
}
