package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class XMLEntities {

    public HashMap<String, HashSet<GameAction>> actions = new HashMap<String, HashSet<GameAction>>();
    public ArrayList<String> subjectEntitiesList = new ArrayList<>();
    public ArrayList<String> consumedEntitiesList = new ArrayList<>();
    public ArrayList<String> producedEntitiesList = new ArrayList<>();
    public ArrayList<String> actionTriggersList = new ArrayList<>();

}
