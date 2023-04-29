package edu.uob;

import java.util.ArrayList;

public class ProcessUserCommand {

    static Cluster currentClusterLocation;
    static ArrayList<Artefacts> inventory;
    GameState gameState;

    public ProcessUserCommand (Layout Entities, GameState state){
        this.currentClusterLocation = Entities.locations.clusters.get(0);
        this.inventory = new ArrayList<>();
        this.gameState = state;
    }

    public String process (){

        //display all the items in the location
        if(gameState.builtInCommand.equalsIgnoreCase("look")){
            String value = "You are currently in " + currentClusterLocation.description + ". You can see:\n";

            if(!currentClusterLocation.artefacts.isEmpty()) {
                //populate artefacts
                String aretfactString = "";
                for (int i = 0; i < currentClusterLocation.artefacts.size(); i++) {
                    aretfactString += currentClusterLocation.artefacts.get(i).getDescription() + "\n";
                }
                value += aretfactString;
            }

            if(!currentClusterLocation.furnitures.isEmpty()) {
                //populate furniture
                String furnitureString = "";
                for (int i = 0; i < currentClusterLocation.furnitures.size(); i++) {
                    furnitureString += currentClusterLocation.furnitures.get(i).getDescription() + "\n";
                }
                value += furnitureString;
            }

            if(!currentClusterLocation.characters.isEmpty()) {
                //populate character
                String characterString = "";
                for (int i = 0; i < currentClusterLocation.characters.size(); i++) {
                    characterString += currentClusterLocation.characters.get(i).getDescription() + "\n";
                }
                value += characterString;
            }

            if(!currentClusterLocation.paths.isEmpty()) {
                String pathString = "You can access from here:\n";
                for(int i = 0; i < currentClusterLocation.paths.size(); i++){
                    pathString += currentClusterLocation.paths.get(i) + "\n";
                }
                value += pathString;
            }

            return value;
        }

        return "OK";
    }

}
