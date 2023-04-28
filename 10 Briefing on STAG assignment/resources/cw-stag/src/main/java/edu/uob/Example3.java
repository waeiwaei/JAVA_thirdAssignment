package edu.uob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Id;
import com.alexmerz.graphviz.objects.Node;

public class Example3 {

    //attributes to store the GameState from the XML (actions) and the DOT (entities) file
    //.DOT file (entities)
    Layout layout = new Layout();

    public static ArrayList<Hashtable<String,String>> attr = new ArrayList<>();

    public static void main(String[] args) {

        ArrayList ents= new Example3().getEntities();
        for(int i=0; i<ents.size(); i++) {
            System.out.println(ents.get(i));
            System.out.println("Description " + attr.get(i).values());
        }
    }

    ArrayList<String> getEntities(){
        Parser p;
        FileReader in = null;
        File f = new File("/home/waei/Java/JAVA2022-main/Weekly Workbooks/10 Briefing on STAG assignment/resources/cw-stag/config/basic-entities.dot");
        p = new Parser();
        try {
            in = new FileReader(f);
            p.parse(in);

        } catch (FileNotFoundException e) {

        } catch (ParseException e2) {
        }

        ArrayList<Graph> gl = p.getGraphs();
        System.out.println("graph formed");


        ArrayList<Node> snl = gl.get(0).getNodes(false);
        ArrayList<String> ents= new ArrayList<String>();
        for(int i=0; i<snl.size(); i++) {
            System.out.println(snl.get(i));
            if(!snl.get(i).isSubgraph()) {
                ents.add(snl.get(i).getId().getId());
                Hashtable<String, String> temp = new Hashtable<>();

                temp = snl.get(i).getAttributes();
                attr.add(temp);
            }
        }
        return ents;
    }
}



