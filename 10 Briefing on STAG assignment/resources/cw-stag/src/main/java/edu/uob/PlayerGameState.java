package edu.uob;

import java.util.ArrayList;

public class PlayerGameState {

    GameState gamestateplayer;
    Cluster currentClusterLocation;
    Cluster storeRoom;
    ArrayList<Artefacts> inventory;
    int health;

    public PlayerGameState (Layout Entities){
        this.currentClusterLocation = Entities.locations.clusters.get(0);

        //is there a better way to get the storeroom cluster?
        this.storeRoom = Entities.locations.clusters.get(Entities.locations.clusters.size() - 1);
        this.inventory = new ArrayList<>();
        this.health = 3;
    }

    public PlayerGameState(){
    }

    public Artefacts getArtefactInventory (String entity) {
        for (Artefacts artefact : inventory) {
            if (artefact.getName().equals(entity)) {
                return artefact;
            }
        }
        return null;
    }

}
