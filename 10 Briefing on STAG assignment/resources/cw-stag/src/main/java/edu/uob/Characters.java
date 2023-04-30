package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class Characters extends GameEntity{
    public static ArrayList<String> listOfAllCharacterNames = new ArrayList<>();
    public Characters(String name, String description) {
        super(name, description);
    }
}
