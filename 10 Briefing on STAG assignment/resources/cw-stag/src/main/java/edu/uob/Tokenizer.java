package edu.uob;

import java.util.ArrayList;

public class Tokenizer {

    //Commands from user should only contain
    //builtInCommands (1)
    //builtInCommands + entity (1)
    //actionCommands + entity (1)

    //if it doesnt make sense - perform nothing or send a message back to user

    public ArrayList<String> tokens = new ArrayList<String>();
    public int currentTokenIndex = 0;

    public Tokenizer(String command) throws Exception {
        tokenize(command);
    }

    //string is to separate tokens
    private void tokenize(String command) throws Exception {

        //convert the commands to lowercase
        command = command.toLowerCase();

        String[] specialChar = {",", ";", "'", ")", "(", ".", "+", " "};

        // lookahead matches a position in the input string that is followed by one of the special characters, string array or a whitespace character
        // lookbehind matches a position in the input string that is preceded by one of the special characters, string array or a whitespace character.
        String regex = "(?=[" + String.join("", specialChar) + "\\s])|(?<=[" + String.join("", specialChar) + "\\s])";

        String[] tokensArray = command.split(regex);

        for(int i = 0; i < tokensArray.length; i++){
            tokens.add(tokensArray[i]);
        }

    }

    public String nextToken(){
        currentTokenIndex = currentTokenIndex + 1;
        return tokens.get(currentTokenIndex);
    }

    public void setCurrentTokenIndex(int index){
        currentTokenIndex = index;
    }

    public String currentToken(){
        return tokens.get(currentTokenIndex);
    }

    public boolean hasMoreTokens(){
        return currentTokenIndex<tokens.size()-1;
    }
}
