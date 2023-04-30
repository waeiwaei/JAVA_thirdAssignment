package edu.uob;

import java.util.ArrayList;

public class GameState {

    //GameAction to be performed if the instruction is provided
    GameAction gameAction;

    //Artefact to be manipulated with
    ArrayList<Artefacts> artefact = new ArrayList<>();

    //locations to travel to (goto)
    //the state of that particular location we want to go to - artefacts, furniture, etc...
    Cluster clusterLocation = new Cluster();

    //Built-in command (look, inventory, inv, get, drop, goto)
    String builtInCommand;


}
