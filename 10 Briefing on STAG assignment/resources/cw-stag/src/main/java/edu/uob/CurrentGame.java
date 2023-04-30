package edu.uob;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
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




        if(player.gamestateplayer.gameAction != null){
            ///we want to check if the GameActions subject entities exist (either in the location or player inventory)
            int numberSubjectEntities = player.gamestateplayer.gameAction.subjects.size();
            int counterSubjectEnt = 0;

            //I should keep track of what items are found in the Inventory, and in the location
            ArrayList<HashMap<String,GameEntity>> fullList = new ArrayList<>();

            //checks if the inventory has an subject-entity to be used
            Artefacts subjectEntityInventory;
            for(int i = 0; i < numberSubjectEntities; i++){
                subjectEntityInventory = player.getArtefactInventory(player.gamestateplayer.gameAction.subjects.get(i));

                if(subjectEntityInventory != null){
                    counterSubjectEnt++;
                    HashMap<String, GameEntity> fromInventory = new HashMap<>();
                    fromInventory.put("Inventory", subjectEntityInventory);
                    fullList.add(fromInventory);
                }
            }


            //check whether GameAction subject entities exist in clusterLocation
            ArrayList<String> subjectEntities = player.gamestateplayer.gameAction.subjects;
            GameEntity entityExistInLocation;
            //I want to check if each element in the subjectEntities exist in the Cluster Location
            //I want to iterate through the subjectEntities element, and check if the string exists in any one of the artefacts, characters, furniture
            for(int i = 0; i < subjectEntities.size(); i++){
                entityExistInLocation = player.currentClusterLocation.getMatchingEntity(subjectEntities.get(i));
                if(entityExistInLocation != null){
                    counterSubjectEnt++;
                    HashMap<String, GameEntity> fromCurrentLocation = new HashMap<>();
                    fromCurrentLocation.put("Location", entityExistInLocation);
                    fullList.add(fromCurrentLocation);
                }
            }

            if(counterSubjectEnt == numberSubjectEntities){

                if(player.gamestateplayer.gameAction.consumed.size() != 0) {

                    boolean consumedEntityHealth = false;
                    //check if the GameAction.consumed - health we will reduce
                    for(int i = 0; i < player.gamestateplayer.gameAction.consumed.size(); i++){
                        if(player.gamestateplayer.gameAction.consumed.get(i).equalsIgnoreCase("health")){
                            consumedEntityHealth = true;
                        }
                    }

                    //then we can execute the action
                    //we need to remove entity (1) has been consumed from either the inventory or location and add it to the store room
                    String entityConsumed = player.gamestateplayer.gameAction.consumed.get(0);

                    //check if the entityConsumed is a location/cluster
                    //if it is we want to remove the path to it
                    Boolean checkConsumedLocation = parseEntitiesList.locations.getLocationNames().contains(entityConsumed);
                    if (checkConsumedLocation) {
                        //we want to remove the paths from this current cluster to that location
                        boolean currentLocationPathToConsumed = player.currentClusterLocation.paths.contains(entityConsumed);
                        if (currentLocationPathToConsumed) {
                            player.currentClusterLocation.paths.remove(entityConsumed);
                        }
                    }

                    //check if it exist in location
                    boolean consumedInLocation = false;
                    GameEntity entityInLocation;

                    entityInLocation = player.currentClusterLocation.getMatchingEntity(entityConsumed);
                    if (entityInLocation != null) {
                        consumedInLocation = true;
                    }

                    //we want to remove that consumed-entity that is in the location
                    if (consumedInLocation == true && consumedEntityHealth == false) {
                        //we want to remove that entity and place it in store room (furniture/artefact) from the location
                        Object consumedEntityClass = entityInLocation.getClass();

                        if (entityInLocation instanceof Artefacts) {
                            //remove from currentlocation artefacts - add to store room
                            player.currentClusterLocation.artefacts.remove((Artefacts) entityInLocation);
                            player.storeRoom.artefacts.add((Artefacts) entityInLocation);


                        } else if (entityInLocation instanceof Furniture) {
                            //remove from currentlocation furniture - add to store room
                            player.currentClusterLocation.furnitures.remove((Furniture) entityInLocation);
                            player.storeRoom.furnitures.add((Furniture) entityInLocation);

                        } else if (entityInLocation instanceof Characters) {
                            //remove from currentlocation characters - add to store room
                            player.currentClusterLocation.characters.remove((Characters) entityInLocation);
                            player.storeRoom.characters.add((Characters) entityInLocation);

                        } else {
                            throw new Exception("Error in line 135\n");
                        }

                    } else if(consumedInLocation != true && consumedEntityHealth == false){
                        //check if the consumed entity is in the inventory (can only hold artefacts) - remove it and add to store room
                        Artefacts consumedEntityInventory = player.getArtefactInventory(entityConsumed);

                        if (consumedEntityInventory != null) {
                            //remove from inventory & add to store room
                            player.inventory.remove(consumedEntityInventory);
                            player.storeRoom.artefacts.add(consumedEntityInventory);
                        }

                    }else if(consumedEntityHealth){

                        player.health--;

                        if(player.health == 0){
                            //drop all inventory items of the player in the current location
                            for(int i = 0; i < player.inventory.size(); i++){
                                player.inventory.remove(player.inventory.get(i));
                                player.currentClusterLocation.artefacts.add(player.inventory.get(i));
                            }

                            return "you died and lost all of your items, you must return to the start of the game\n\n";
                        }

                    }
                }

                //now we want to add the produced entities to the current game location
                ArrayList<String> producedEntities = player.gamestateplayer.gameAction.produced;
                //we want to see if any of the produced entitites are a location, if it is. then we want to create a path from the current location to that other location
                for(int i = 0; i < producedEntities.size(); i++){
                    Boolean checkProducedLocation = parseEntitiesList.locations.getLocationNames().contains(producedEntities.get(i));
                    if(checkProducedLocation){
                        //we want to remove the paths from this current cluster to that location
                        boolean currentLocationPathtoProduced = player.currentClusterLocation.paths.contains(producedEntities.get(i));
                        if(currentLocationPathtoProduced == false){
                            player.currentClusterLocation.paths.add(producedEntities.get(i));
                        }
                    }
                }

                ArrayList<String> allArtefactNames = parseEntitiesList.locations.getAllArtefactNames();
                ArrayList<String> allCharacterNames = parseEntitiesList.locations.getAllCharacterNames();
                ArrayList<String> allFurnitureNames = parseEntitiesList.locations.getAllFurnitureNames();

                //need to identify if the produced entities is an artefact, furniture or characters
                for(int i = 0; i < producedEntities.size(); i++){
                    if(allArtefactNames.contains(producedEntities.get(i))){

                        //we then want to get this artefact from the storeroom cluster - remove it from the store room and add it to the current location
                        Artefacts fromStoreRoom = (Artefacts) player.storeRoom.getMatchingEntity(producedEntities.get(i));

                        player.currentClusterLocation.artefacts.add(fromStoreRoom);
                        player.storeRoom.artefacts.remove(fromStoreRoom);

                    }else if(allFurnitureNames.contains(producedEntities.get(i))){

                        //we then want to get this artefact from the storeroom cluster - remove it from the store room and add it to the current location
                        Furniture fromStoreRoom = (Furniture) player.storeRoom.getMatchingEntity(producedEntities.get(i));

                        player.currentClusterLocation.furnitures.add(fromStoreRoom);
                        player.storeRoom.furnitures.remove(fromStoreRoom);

                    }else if (allCharacterNames.contains(producedEntities.get(i))){

                        //try getting it from the store room first, if it is not there, then we will go through each location to and remove the lumberjack to follow the players currentLocation
                        GameEntity object = player.storeRoom.getMatchingEntity(producedEntities.get(i));

                        //means it is not in the store room
                        if(object == null) {
                            for (int j = 0; j < parseEntitiesList.locations.clusters.size(); j++) {
                                //so we want to go to each location and check their characters if they have a matching
                                Cluster cluster = parseEntitiesList.locations.clusters.get(j);

                                //returns that character in that cluster if it exists
                                GameEntity value = cluster.getMatchingEntity(producedEntities.get(i));

                                //if it exists - we want to remove that character from that cluster and move it to the  current cluster,
                                if (value != null) {
                                    cluster.characters.remove((Characters) value);
                                    player.currentClusterLocation.characters.add((Characters) value);
                                }
                            }

                        }else {

                            //we then want to get this artefact from the storeroom cluster - remove it from the store room and add it to the current location
                            Characters fromStoreRoom = (Characters) player.storeRoom.getMatchingEntity(producedEntities.get(i));

                            player.currentClusterLocation.characters.add(fromStoreRoom);
                            player.storeRoom.characters.remove(fromStoreRoom);
                        }
                    }else if(producedEntities.get(i).equalsIgnoreCase("health")){

                        if(player.health < 3) {
                            player.health++;
                        }
                    }

                }

                return player.gamestateplayer.gameAction.narration + "\n";

            }else{
                throw new Exception("Unable to execute this action \n");
            }
        }
        return "Error";
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
            throw new Exception("Cannot move to that location\n");
        }
    }

}
