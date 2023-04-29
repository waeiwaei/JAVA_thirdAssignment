package edu.uob;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class ParseXMLFile {

    XMLEntities parseXML = new XMLEntities();

    public XMLEntities getActions (File file) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();
//        System.out.println("Root Node : " + doc.getFirstChild().getNodeName());
        NodeList nodeList = doc.getElementsByTagName("action");

        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            Node node = nodeList.item(itr);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;



                //extracts the action trigger keyphrases
                Element triggers = (Element) eElement.getElementsByTagName("triggers").item(0);
                NodeList keyphraseNodes = triggers.getElementsByTagName("keyphrase");
                ArrayList<String> keyphraseList = new ArrayList<>();

                for(int i = 0; i < keyphraseNodes.getLength(); i++){
                    Element keyphraseElement = (Element) keyphraseNodes.item(i);
                    String keyphrase = keyphraseElement.getTextContent();
                    keyphraseList.add(keyphrase);

                    parseXML.actionTriggersList.add(keyphrase);
                }



                //for each trigger keyphrase and create a new GameAction object
                for(int j = 0; j < keyphraseList.size(); j++){
                    String trigger = keyphraseList.get(j);

                    GameAction gameAct = new GameAction();

                    //extracts the subject entities
                    Element subjects = (Element) eElement.getElementsByTagName("subjects").item(0);
                    NodeList subjectEntitiesNodes = subjects.getElementsByTagName("entity");
                    ArrayList<String> subjectEntitiesList = new ArrayList<>();

                    for(int i = 0; i < subjectEntitiesNodes.getLength(); i++){
                        Element subjectEntitiesElement = (Element) subjectEntitiesNodes.item(i);
                        String entities = subjectEntitiesElement.getTextContent();
                        subjectEntitiesList.add(entities);

                        parseXML.subjectEntitiesList.add(entities);
                    }

                    gameAct.subjects = subjectEntitiesList;


                    //extracts the consumed entities
                    Element consumed = (Element) eElement.getElementsByTagName("consumed").item(0);
                    NodeList consumedEntitiesNodes = consumed.getElementsByTagName("entity");
                    ArrayList<String> consumedEntitiesList = new ArrayList<>();

                    for(int i = 0; i < consumedEntitiesNodes.getLength(); i++){
                        Element entitiesElement = (Element) consumedEntitiesNodes.item(i);
                        String entities = entitiesElement.getTextContent();
                        consumedEntitiesList.add(entities);

                        parseXML.consumedEntitiesList.add(entities);
                    }

                    gameAct.consumed = consumedEntitiesList;


                    //extracts the produced entities
                    Element produced = (Element) eElement.getElementsByTagName("produced").item(0);
                    NodeList producedEntitiesNodes = produced.getElementsByTagName("entity");
                    ArrayList<String> producedEntitiesList = new ArrayList<>();

                    for(int i = 0; i < producedEntitiesNodes.getLength(); i++){
                        Element entitiesElement = (Element) producedEntitiesNodes.item(i);
                        String entities = entitiesElement.getTextContent();
                        producedEntitiesList.add(entities);

                        parseXML.producedEntitiesList.add(entities);
                    }

                    gameAct.produced = producedEntitiesList;



                    //get the narration
                    gameAct.narration = eElement.getElementsByTagName("narration").item(0).getTextContent();

                    HashSet<GameAction> set = new HashSet<>();
                    set.add(gameAct);

                    parseXML.actions.put(trigger, set);
                }
            }
        }

        //remove duplicates from list of subject, produced, consumed
        // Create a new ArrayList from the HashSet to preserve order
        HashSet<String> temp = new HashSet<>(parseXML.subjectEntitiesList);
        parseXML.subjectEntitiesList = new ArrayList<>(temp);

        temp = new HashSet<>(parseXML.producedEntitiesList);
        parseXML.producedEntitiesList = new ArrayList<>(temp);

        temp = new HashSet<>(parseXML.consumedEntitiesList);
        parseXML.consumedEntitiesList = new ArrayList<>(temp);

        return parseXML;
    }
}
