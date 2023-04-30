package edu.uob;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Parser {

    GameState state;

    public XMLEntities parseActions;
    public Layout parseEntities;
    public ArrayList<String> builtInCommands = new ArrayList<String>(Arrays.asList("inventory", "get", "drop", "look", "goto", "inv"));


    //coming from parseXMLFile
    public ArrayList<String> actionTriggers;
    public ArrayList<String> subjectEntities;
    public ArrayList<String> consumedEntities;
    public ArrayList<String> producedEntities;



    //coming from parseDotFile
    public ArrayList<String> artefactEntities;
    public ArrayList<String> furnitureEntities;
    public ArrayList<String> characterEntities;
    public ArrayList<String> locations;


    public ArrayList<String> combinedList;


    public Parser(XMLEntities parseActions, Layout parseEntities){

        this.state = new GameState();

        //parseDotFile
        this.artefactEntities = Artefacts.listOfAllArtefactsNames;
        this.furnitureEntities = Furniture.listOfAllFurnitureNames;
        this.characterEntities = Characters.listOfAllCharacterNames;
        this.locations = Locations.locationsList;


        //parseXMLFile
        this.actionTriggers = parseActions.actionTriggersList;
        this.subjectEntities = parseActions.subjectEntitiesList;
        this.consumedEntities = parseActions.consumedEntitiesList;
        this.producedEntities = parseActions.producedEntitiesList;


        this.combinedList = new ArrayList<>();
        combinedList.addAll(builtInCommands);
        combinedList.addAll(actionTriggers);
        combinedList.addAll(subjectEntities);
        combinedList.addAll(consumedEntities);
        combinedList.addAll(producedEntities);
        combinedList.addAll(artefactEntities);
        combinedList.addAll(furnitureEntities);
        combinedList.addAll(characterEntities);
        combinedList.addAll(locations);

        HashSet<String> uniqueSet = new HashSet<String>(combinedList);
        combinedList.clear();
        combinedList.addAll(uniqueSet);


        //convert to lower case
        this.artefactEntities.replaceAll(String::toLowerCase);
        this.furnitureEntities.replaceAll(String::toLowerCase);
        this.characterEntities.replaceAll(String::toLowerCase);
        this.locations.replaceAll(String::toLowerCase);
        this.actionTriggers.replaceAll(String::toLowerCase);
        this.subjectEntities.replaceAll(String::toLowerCase);
        this.consumedEntities.replaceAll(String::toLowerCase);
        this.producedEntities.replaceAll(String::toLowerCase);

        this.combinedList.replaceAll(String::toLowerCase);
        this.parseActions = parseActions;
        this.parseEntities = parseEntities;
    }

    GameState parse(Tokenizer token, PlayerGameState currentPlayerState) throws Exception {


        //filter out tokens for "Decorative" words
        ArrayList<String> temporaryArray = new ArrayList<>();

        for(int i = 0; i < token.tokens.size() ; i++){
            for(int j = 0; j < combinedList.size(); j++) {

                if (token.tokens.get(i).equalsIgnoreCase(combinedList.get(j))) {
                    temporaryArray.add(token.tokens.get(i));
                }
            }
        }

        token.tokens = temporaryArray;
        token.tokens.replaceAll(String::toLowerCase);









        boolean builtInCmdInsruction = true;
        boolean actionCmdInstruction = true;

        //check if it is a builtInCommand
        if(!checkBuiltInCommand(token, currentPlayerState)){
            builtInCmdInsruction = false;
        }

        if(!builtInCmdInsruction && !checkActionCommands(token)){
            actionCmdInstruction = false;
        }

        if(!actionCmdInstruction && !builtInCmdInsruction){
            throw new Exception("Not a valid command\n");
        }

        return state;
    }






    boolean checkBuiltInCommand(Tokenizer token, PlayerGameState currentPlayerState){
        //Case 1 :
        //Built-in Commands : Look, inventory, inv
        // should not have any other tokens after it


        //Case 2 :
        //Built-in Commands : goto, get, drop
        //must follow the sequence of "built-in" + entity (any type of entity)
        // e.g.
        // goto cellar (if the entity is not a location - flag out to user during gameplay : all location will have valid paths, if player inputs path that does not exist issue warning)
        // get tree  (if entity is not an artefact - flag out to user during gameplay : entities which are both artefact and consumed entities)
        // drop key  (valid)

        //invalid built-in commands
        //e.g.
        //Composite commands
        //goto cellar and get key (built-in + built-in)
        //goto cellar and cut tree (built-in + action trigger)

        //Wrong ordering
        //key drop
        //forest goto

        //Built-in command only, without entity
        //get
        //drop

        if(token.tokens.get(0).equalsIgnoreCase("look") || token.tokens.get(0).equalsIgnoreCase("inventory") || token.tokens.get(0).equalsIgnoreCase("inv")){

            //assigning the value of the BuiltIn Command
            state.builtInCommand = token.tokens.get(0);

            if(token.hasMoreTokens()){
                return false;
            }

            return true;

        }else{

            /*CHECKING FOR THE CASE OF COMPOSITE - extracts the builtInCommand if any*/
            int builtInCommandCounter = 0;
            String BCommand = "";

            //check if it is a composite built-in command (built-in command + built-in command)
            for(int i = 0; i < token.tokens.size(); i++){
                for(int j = 0; j < builtInCommands.size(); j++) {
                    if (token.tokens.get(i).equalsIgnoreCase(builtInCommands.get(j))) {
                        builtInCommandCounter++;

                        //if there is more than one built-in command, then it is considered to be a composite command which is not allowed
                        if(builtInCommandCounter > 1){
                            return false;
                        }

                        BCommand = token.tokens.get(i);
                    }
                }
            }

            int actionTriggerCounter = 0;
            //check if it is a composite built-in command + action trigger
            for(int i = 0; i < token.tokens.size(); i++){
                for(int j = 0; j < actionTriggers.size(); j++){
                    if(token.tokens.get(i).equalsIgnoreCase(actionTriggers.get(j))){
                        actionTriggerCounter++;
                    }
                }
            }

            //checking if the token has both the action triggers and built-in commands, which is not allowed
            if(builtInCommandCounter > 1 && actionTriggerCounter > 1){
                return false;
            }

            state.builtInCommand = BCommand;
            /**********************************************************************************************/


            //Check if sequence is respected
            //goto location
            if (BCommand.equalsIgnoreCase("goto")){

                //gets the index of the command - must check if the location comes after
                int indexOfCmd = token.tokens.indexOf(BCommand);

                if(indexOfCmd + 1 == token.tokens.size()){
                    return false;
                }

                int locationCounter = 0;

                for(int j = 0; j < token.tokens.size(); j++){
                    for (int i = 0; i < locations.size(); i++) {
                        if (token.tokens.get(j).equalsIgnoreCase(locations.get(i))) {
                            locationCounter++;

                            if (locationCounter > 1) {
                                return false;
                            }

                            //find cluster location
                            for(int q = 0; q < parseEntities.locations.clusters.size(); q++){
                                if(parseEntities.locations.clusters.get(q).name.equalsIgnoreCase(token.tokens.get(j))){
                                       state.clusterLocation = parseEntities.locations.clusters.get(q);
                                }
                            }
                        }
                    }
                }

                //if none of the following tokens match with any location in locations list, return false
                if(locationCounter != 1){
                    return false;
                }else{
                    return true;
                }


            //get {artefact-entity : artefact-entities}
            //also checks if the there is atleast one artefact entity to get and not more than one
            } else if (BCommand.equalsIgnoreCase("get")){

                //gets the index of the command - must check if the entity comes after
                int indexOfCmd = token.tokens.indexOf(BCommand);

                if(indexOfCmd + 1 == token.tokens.size()){
                    return false;
                }

                int artefactEntitiesCounter = 0;

                for(int j = 0; j < token.tokens.size(); j++) {
                    for (int i = 0; i < artefactEntities.size(); i++) {
                        if (token.tokens.get(j).equalsIgnoreCase(artefactEntities.get(i))) {
                            artefactEntitiesCounter++;

                            if (artefactEntitiesCounter > 1) {
                                return false;
                            }

                            //we want to populate the matching artefacts to get
                            for(int q = 0; q < parseEntities.locations.clusters.size(); q++){
                                for(int u = 0; u < parseEntities.locations.clusters.get(q).artefacts.size(); u++){

                                    if(parseEntities.locations.clusters.get(q).artefacts.get(u).getName().equalsIgnoreCase(token.tokens.get(j))){
                                        state.artefact.add(parseEntities.locations.clusters.get(q).artefacts.get(u));
                                    }

                                }
                            }
                        }
                    }
                }

                if(artefactEntitiesCounter != 1){
                    return false;
                }

                return true;

                //get {artefact-entity : artefact-entities}
                //also checks if the there is atleast one artefact entity to drop and not more than one
            } else if (BCommand.equalsIgnoreCase("drop")) {

                //we need to count the entities, there should only be one
                //any more than one entity that needs to drop will cause an error
                int entitiesCounter = 0;

                for(int j = 0; j < token.tokens.size(); j++) {
                    for (int i = 0; i < artefactEntities.size(); i++) {
                        if (token.tokens.get(j).equalsIgnoreCase(artefactEntities.get(i))) {
                            entitiesCounter++;

                            if (entitiesCounter > 1) {
                                return false;
                            }

                            //we want to populate the matching artefacts to drop
                            for(int q = 0; q < currentPlayerState.inventory.size(); q++){
                                if(currentPlayerState.inventory.get(q).getName().equalsIgnoreCase(token.tokens.get(j))){
                                    state.artefact.add(currentPlayerState.inventory.get(q));
                                }
                            }
                        }
                    }
                }

                if(entitiesCounter != 1){
                    return false;
                }

                return true;
            }
        }
        return false;
    }

    //atleast one trigger and one subject-entity
    //cutdown,tree
    //chop,axe,cutdown,tree
    //cutdown,tree,axe
    //tree,cutdown,axe
    //axe,cutdown,tree
    //chop,tree
    //tree,chop,axe
    //chop,axe

    //Valid
    //chop with axe to cutdown tree
    //in this example, both chop and cut down are trigger phrases that would match the same action.
    //need to pick out the trigger words, and see if they are to the same game action

    boolean checkActionCommands(Tokenizer token){

//        int numberOfActionTriggers = 0;
        ArrayList<String> actionTriggersIdentified = new ArrayList<>();
        for(int i = 0; i < token.tokens.size(); i++){
            for(int j = 0; j < actionTriggers.size(); j++){
                if(token.tokens.get(i).equalsIgnoreCase(actionTriggers.get(j))){
//                    numberOfActionTriggers++;
                    actionTriggersIdentified.add(token.tokens.get(i));
                }
            }
        }

        //[chop , tree]
        if(actionTriggersIdentified.size() == 1){

            //we need to check if there is atleast one subject entity exists
            ArrayList<String> numberOfSubjectEntities = new ArrayList<>();
            for(int i = 0; i < token.tokens.size(); i++){
                for(int j = 0; j < subjectEntities.size();j++) {
                    if (token.tokens.get(i).equalsIgnoreCase(subjectEntities.get(j))) {
                        numberOfSubjectEntities.add(token.tokens.get(i));
                    }
                }
            }

            //if the command does not contain any subjectEntities, we return false
            if(numberOfSubjectEntities.size() != 1){
                return false;
            }

            //set gameState for the Action Command
            HashSet<GameAction> listGameActions = parseActions.actions.get(actionTriggersIdentified.get(0));

            for(GameAction gameAction1 : listGameActions){
                if(gameAction1.subjects.contains(numberOfSubjectEntities.get(0))){
                    state.gameAction = gameAction1;
                }
            }

            return true;
        // [chop,axe,cutdown,tree]

        }else if (actionTriggersIdentified.size() == 2){

            //both unqiue action keyphrase triggers - "cutdown" and "chop"
            //but both triggers contains the same GameAction within their list - so this is still a valid action
            //if they did not have the same GameAction, this would be considered as a double command which is not allowed





            //chop with axe to cutdown tree




            //if the command contains more than one action trigger, need to identify if the triggers used contain the same GameAction
            //chop
            HashSet<GameAction> firstActionTrigger = parseActions.actions.get(actionTriggersIdentified.get(0));
            //cutdown
            HashSet<GameAction> secondActionTrigger = parseActions.actions.get(actionTriggersIdentified.get(1));


            boolean hasMatchingGameAction = false;
            for (GameAction gameActionFirst : firstActionTrigger) {
                for(GameAction gameActionSecond : secondActionTrigger) {
                    if (gameActionFirst.equals(gameActionSecond)) {
                        hasMatchingGameAction = true;
                        state.gameAction = gameActionFirst;
                        break;
                    }
                }
            }

            if (hasMatchingGameAction) {
                System.out.println("The two sets contain at least one matching GameAction.");
                return true;
            } else {
                System.out.println("The two sets do not contain any matching GameAction.");
                return false;
            }

        }

        return false;
    }

}
