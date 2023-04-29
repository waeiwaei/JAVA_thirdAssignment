package edu.uob;

import java.util.ArrayList;

public class GameAction {

    public ArrayList<String> subjects = new ArrayList<>();
    public ArrayList<String> consumed = new ArrayList<>();
    public ArrayList<String> produced = new ArrayList<>();
    String narration;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GameAction)) {
            return false;
        }

        GameAction other = (GameAction) o;
        return this.subjects.equals(other.subjects) &&
                this.consumed.equals(other.consumed) &&
                this.produced.equals(other.produced);
    }


}
