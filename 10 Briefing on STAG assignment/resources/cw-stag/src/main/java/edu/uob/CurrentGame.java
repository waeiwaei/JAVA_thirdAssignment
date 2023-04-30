package edu.uob;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

public class CurrentGame {

    public static String process (PlayerGameState player, Layout parseEntitiesList) throws Exception {

        String result;
        //can either be an action command or a built-in command
        if(!player.gamestateplayer.builtInCommand.isEmpty()) {
            //display all the items in the location
            if (player.gamestateplayer.builtInCommand.equalsIgnoreCase("look")) {

                result = lookCommand(player);

                return result;

            } else if(player.gamestateplayer.builtInCommand.equalsIgnoreCase("get")){

                result = getCommand(player);

                return result;

            }else if(player.gamestateplayer.builtInCommand.equalsIgnoreCase("drop")){

                result = dropCommand(player);

                return result;

            }else if(player.gamestateplayer.builtInCommand.equalsIgnoreCase("inventory") || player.gamestateplayer.builtInCommand.equalsIgnoreCase("inv")){

                result = inventoryCommand(player);

                return result;

            }else if(player.gamestateplayer.builtInCommand.equalsIgnoreCase("goto")){

                result = gotoCommand(player, parseEntitiesList);

                return result;
            }
        }




        if(player.gamestateplayer.gameAction.narration != null){
            ///we want to check if the GameActions subject entities exist (either in the location or player inventory)
            int numberSubjectEntities = player.gamestateplayer.gameAction.subjects.size();
            int counterSubjectEnt = 0;

            //checks if the inventory has an subject-entity to be used
            Artefacts subjectEntityInventory;
            for(int i = 0; i < numberSubjectEntities; i++){
                subjectEntityInventory = player.getArtefactInventory(player.gamestateplayer.gameAction.subjects.get(i));
                counterSubjectEnt++;

                if(subjectEntityInventory != null){
                    counterSubjectEnt++;
                }
            }


            //check whether GameAction subject entities exist in clusterLocation
            ArrayList<String> subjectEntities = player.gamestateplayer.gameAction.subjects;
            GameEntity test;
            //I want to check if each element in the subjectEntities exist in the Cluster Location
            //I want to iterate through the subjectEntities element, and check if the string exists in any one of the artefacts, characters, furniture
            for(int i = 0; i < subjectEntities.size(); i++){
                test = player.currentClusterLocation.getMatchingEntity(subjectEntities.get(i));

                if(test != null){
                    counterSubjectEnt++;
                }
            }

            if(counterSubjectEnt == numberSubjectEntities){
                //then we can execute the action
                //we need to remove what has been consumed from
            }
        }
        return "OK";
    }


    public static String lookCommand(PlayerGameState player){
        String value = "You are currently in " + player.currentClusterLocation.description + ". You can see:\n";

        if (!player.currentClusterLocation.artefacts.isEmpty()) {
            //populate artefacts
            String aretfactString = "";
            for (int i = 0; i < player.currentClusterLocation.artefacts.size(); i++) {
                aretfactString += player.currentClusterLocation.artefacts.get(i).getDescription() + "\n";
            }
            value += aretfactString;
        }

        if (!player.currentClusterLocation.furnitures.isEmpty()) {
            //populate furniture
            String furnitureString = "";
            for (int i = 0; i < player.currentClusterLocation.furnitures.size(); i++) {
                furnitureString += player.currentClusterLocation.furnitures.get(i).getDescription() + "\n";
            }
            value += furnitureString;
        }

        if (!player.currentClusterLocation.characters.isEmpty()) {
            //populate character
            String characterString = "";
            for (int i = 0; i < player.currentClusterLocation.characters.size(); i++) {
                characterString += player.currentClusterLocation.characters.get(i).getDescription() + "\n";
            }
            value += characterString;
        }

        if (!player.currentClusterLocation.paths.isEmpty()) {
            //populate character
            String pathString = "You can access from here: \n";
            for (int i = 0; i < player.currentClusterLocation.paths.size(); i++) {
                pathString += player.currentClusterLocation.paths.get(i) + "\n";
            }
            value += pathString;
        }

        return value;
    }

    public static String getCommand(PlayerGameState player) throws Exception {
        String getArtefact = "You picked up a ";

        if(player.currentClusterLocation.artefacts.contains(player.gamestateplayer.artefact.get(0))){

            //we remove the artefact from the current location
            player.currentClusterLocation.artefacts.remove(player.gamestateplayer.artefact.get(0));

            //and we insert it into the inventory
            player.inventory.add(player.gamestateplayer.artefact.get(0));

            getArtefact += player.gamestateplayer.artefact.get(0).getName() + "\n";

            return getArtefact;

        }else{
            throw new Exception("Location does not contain artefact: "+ player.gamestateplayer.artefact.get(0));
        }
    }

    public static String dropCommand(PlayerGameState player) throws Exception {

        if(!player.inventory.isEmpty()) {
            String dropArtefacet = "You dropped ";
            if (player.inventory.contains(player.gamestateplayer.artefact.get(0))) {
                player.inventory.remove(player.gamestateplayer.artefact.get(0));
                player.currentClusterLocation.artefacts.add(player.gamestateplayer.artefact.get(0));

                dropArtefacet += player.gamestateplayer.artefact.get(0).getName() + "\n";

                return dropArtefacet;

            } else {
                throw new Exception("Inventory does not have: " + player.gamestateplayer.artefact.get(0)+ "\n");
            }
        }else{
            throw new Exception("Inventory is empty\n");
        }
    }

    public static String inventoryCommand(PlayerGameState player) throws Exception {
        String inventoryListString = "";

        if(!player.inventory.isEmpty()){
            for(int i = 0; i < player.inventory.size(); i++){
                inventoryListString += player.inventory.get(i).getName() + "\n";
            }
        }else{
            throw new Exception("There is nothing in the inventory\n");
        }

        return inventoryListString;
    }

    public static String gotoCommand(PlayerGameState player, Layout parseEntitiesList) throws Exception {
        boolean clusterIsAvailable = player.currentClusterLocation.paths.contains(player.gamestateplayer.clusterLocation.name);

        if(clusterIsAvailable) {
            //get the cluster that corresponds to the name
            //then assign it to the current cluster
            Cluster nextClusterLocation = parseEntitiesList.locations.getClusterByName(player.gamestateplayer.clusterLocation.name);
            player.currentClusterLocation = nextClusterLocation;

            return lookCommand(player);

        }else{
            throw new Exception("Error cannot move to that location");
        }
    }

}
