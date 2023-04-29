package edu.uob;

import java.util.ArrayList;
import java.util.Hashtable;

public class Furniture extends GameEntity {
    public static ArrayList<String> listOfAllFurnitureNames = new ArrayList<>();

    public Furniture(String name, String description) {
        super(name, description);
    }
}
