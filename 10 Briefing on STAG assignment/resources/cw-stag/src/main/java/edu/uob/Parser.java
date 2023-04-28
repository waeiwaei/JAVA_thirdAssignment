package edu.uob;

import com.alexmerz.graphviz.Token;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Parser {

    GameState state;

    public String[] builtInCommands = {"inventory", "get", "drop", "look", "goto"};

    //currently hard-coded, will need to identify how to populate this through reading the XML document
    public String[] actionTriggers = {"cut", "chop", "cutdown"};

    public String[] subjectEntities = {"tree", "axe"};

    public String[] artefactEntities = {"axe", "coin"};

    public String[] furnitureEntities = {"tree"};

    public String[] characterEntities = {"elf"};

    public String[] locations = {"forest", "cellar", "cabin"};

    public String[] consumedEntities = {"tree"};

    public String[] producedEntities = {"log"};

    public Parser(){
        this.state = new GameState();
    }

    GameState parse(Tokenizer token) throws Exception {

        for (int i = 0; i < actionTriggers.length; i++) {
            actionTriggers[i] = actionTriggers[i].toLowerCase();
        }

        for (int i = 0; i < builtInCommands.length; i++) {
            builtInCommands[i] = builtInCommands[i].toLowerCase();
        }

        //create a combined array
        ArrayList<String> combinedArray = new ArrayList<String>();
        for(int i = 0; i < builtInCommands.length; i++){
            combinedArray.add(builtInCommands[i]);
        }

        for(int i = 0; i < artefactEntities.length; i++){
            combinedArray.add(artefactEntities[i]);
        }

        for(int i = 0; i < locations.length; i++){
            combinedArray.add(locations[i]);
        }

        for(int i = 0; i < actionTriggers.length; i++){
            combinedArray.add(actionTriggers[i]);
        }

        for(int i = 0; i < furnitureEntities.length; i++){
            combinedArray.add(furnitureEntities[i]);
        }

        for(int i = 0; i < characterEntities.length; i++){
            combinedArray.add(characterEntities[i]);
        }


        for(int i = 0; i < subjectEntities.length; i++){
            combinedArray.add(subjectEntities[i]);
        }

        ArrayList<String> temporaryArray = new ArrayList<>();
        //we want to filter out decorative words that are not included in the actionCommands and builtCommands list
        //filter out the builtCommands first
        for(int i = 0; i < token.tokens.size() ; i++){
            for(int j = 0; j < combinedArray.size(); j++) {

                if (token.tokens.get(i).equalsIgnoreCase(combinedArray.get(j))) {
                    temporaryArray.add(token.tokens.get(i));
                }
            }
        }

        token.tokens = temporaryArray;

        //Commands from user should only contain
        //Case 1 : builtInCommands (1)
        //Case 2 : builtInCommands + entity (1)
        //Case 3 : actionCommands + entity (1)

        boolean c1 = true;

        //check if it is a builtInCommand
        if(!checkBuiltInCommand(token)){
            c1 = false;
        }

        if(c1 == false && checkActionCommands(token)){
            c1 = true;
        }

        if(c1 == false){
            throw new Exception("Error - Parser");
        }

        return state;
    }

    boolean checkBuiltInCommand(Tokenizer token){
        //Case 1 : builtInCommands (1)
        //Case 2 : builtInCommands + entity (1)

        if(token.tokens.get(0).equalsIgnoreCase("look") || token.tokens.get(0).equalsIgnoreCase("inventory") || token.tokens.get(0).equalsIgnoreCase("inv")){

            if(token.hasMoreTokens()){
                return false;
            }

            return true;

        }else{

            int builtInCommandCounter = 0;
            String BCommand = "";

            for(int i = 0; i < token.tokens.size(); i++){
                for(int j = 0; j < builtInCommands.length; j++) {
                    if (token.tokens.get(i).equalsIgnoreCase(builtInCommands[j])) {
                        builtInCommandCounter++;

                        if(builtInCommandCounter > 1){
                            return false;
                        }

                        BCommand = token.tokens.get(i);
                    }
                }
            }

            //we want to make sure the sequence of tokens is respected
            //goto {location : locations}
            if (BCommand.equalsIgnoreCase("goto")){

                int locationCounter = 0;

                for(int j = 0; j < token.tokens.size(); j++){
                    for (int i = 0; i < locations.length; i++) {
                        if (token.tokens.get(j).equalsIgnoreCase(locations[i])) {
                            locationCounter++;

                            if (locationCounter > 1) {
                                return false;
                            }
                        }
                    }
                }


                if(locationCounter != 1){
                    return false;
                }

                return true;

            //get {entity : entities}
            } else if (BCommand.equalsIgnoreCase("get")){

                int entitiesCounter = 0;

                for(int j = 0; j < token.tokens.size(); j++) {
                    for (int i = 0; i < artefactEntities.length; i++) {
                        if (token.tokens.get(j).equalsIgnoreCase(artefactEntities[i])) {
                            entitiesCounter++;

                            if (entitiesCounter > 1) {
                                return false;
                            }
                        }
                    }
                }

                if(entitiesCounter != 1){
                    return false;
                }

                return true;

            } else if (BCommand.equalsIgnoreCase("drop")) {

                int entitiesCounter = 0;

                for(int j = 0; j < token.tokens.size(); j++) {
                    for (int i = 0; i < artefactEntities.length; i++) {
                        if (token.tokens.get(j).equalsIgnoreCase(artefactEntities[i])) {
                            entitiesCounter++;

                            if (entitiesCounter > 1) {
                                return false;
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

    //cutdown,tree
    //chop,axe,cutdown,tree
    //cutdown,tree,axe
    //tree,cutdown,axe
    //axe,cutdown,tree
    //chop,tree
    //tree,chop,axe
    //chop,axe

    boolean checkActionCommands(Tokenizer token){

        int triggerCounter = 0;
        int entitiesCounter = 0;
        //triggers <keyphrases> + subject <entities>
        for(int i = 0; i < token.tokens.size(); i++){
            //for each token we want to check whether there is atleast one trigger
            for(int j = 0; j < actionTriggers.length; j++) {
                if (token.tokens.get(i).equalsIgnoreCase(actionTriggers[j])) {
                    triggerCounter++;
                }
            }
        }

        for(int i = 0; i < token.tokens.size(); i++){
            for(int j = 0;j < subjectEntities.length; j++){
                if(token.tokens.get(i).equalsIgnoreCase(subjectEntities[j])){
                    entitiesCounter++;
                }
            }
        }

        if(triggerCounter > 0 && entitiesCounter > 0){
            return true;
        }

        return false;
    }

}
