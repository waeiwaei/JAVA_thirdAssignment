package edu.uob;

import java.util.ArrayList;

public class Tokenizer {

    public String[] builtInCommands = {"inventory", "get", "drop", "look"};

    //currently hard-coded, will need to identify how to populate this through reading the XML document
    public String[] actionCommands = {"cut", "chop", "cutdown", "tree", "axe"};
    public ArrayList<String> tokens = new ArrayList<String>();

    public Tokenizer(String command){
        tokenize(command);
    }

    //string is to separate tokens
    private void tokenize(String command){

        //convert the commands to lowercase
        command = command.toLowerCase();

        for (int i = 0; i < actionCommands.length; i++) {
            actionCommands[i] = actionCommands[i].toLowerCase();
        }

        for (int i = 0; i < builtInCommands.length; i++) {
            builtInCommands[i] = builtInCommands[i].toLowerCase();
        }


        String[] specialChar = {",", ";", "'", ")", "(", ".", "+", " "};

        // lookahead matches a position in the input string that is followed by one of the special characters, string array or a whitespace character
        // lookbehind matches a position in the input string that is preceded by one of the special characters, string array or a whitespace character.
        String regex = "(?=[" + String.join("", specialChar) + "\\s])|(?<=[" + String.join("", specialChar) + "\\s])";

        String[] tokensArray = command.split(regex);

        //create a combined array
        ArrayList<String> combinedArray = new ArrayList<String>();
        for(int i = 0; i < builtInCommands.length; i++){
            combinedArray.add(builtInCommands[i]);
        }
        for(int i = 0; i < actionCommands.length; i++){
            combinedArray.add(actionCommands[i]);
        }


        //we want to filter out decorative words that are not included in the actionCommands and builtCommands list
        //filter out the builtCommands first
        for(int i = 0; i < tokensArray.length ; i++){
            for(int j = 0; j < combinedArray.size(); j++) {

                if (tokensArray[i].equalsIgnoreCase(combinedArray.get(j))) {
                    tokens.add(tokensArray[i]);
                }
            }
        }

        //we also want to ensure that no double commands are passed (e.g. "Please show me the Inventory and cut down the tree")
        boolean containsBuiltInCommand = false;
        boolean containsActionCommand = false;

        for (String builtCmd : builtInCommands) {
            if (tokens.contains(builtCmd)) {
                containsBuiltInCommand = true;
                break;
            }
        }

        for (String actCmd : actionCommands) {
            if (tokens.contains(actCmd)) {
                containsActionCommand = true;
                break;
            }
        }

        if (containsBuiltInCommand && containsActionCommand) {
            System.out.println("command contains two instructions - not allowed");
            //throw new exception("Not allowed to have 2 instructions")
        }

        System.out.println(tokens);

    }

}
