/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml;

import classes.GDNode;
import classes.RelationObject;
import classes.SWNode;
import classes.Stem;
import classes.Synonyms;
import global.Function;
import global.Global;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import global.Conn;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author admin
 */

public class DialogsEngine {
    Function f;
    Synonyms s;
    Global g = new Global();
    Gson gson = new Gson();
    public static boolean isTraining = false;
    
    public static void main(String arsp[]) throws Exception{
        DialogsEngine de = new DialogsEngine();        
        de.trainingStart();//this is all you need to train QA+GD model
    }
    public DialogsEngine() throws Exception{
        f = new Function(1);                
        s = new Synonyms();
    }
        
    
    public void trainingStart() throws Exception{     
        if (isTraining) return;
        try {
        System.out.println("DialogsEngine Training started");
        isTraining = true;                    
        f.loadData(); //renew objects, syns, general dialogs
        List<List<Stem[]>> P = new ArrayList<>();  
        
        //Phrases build: objects, syns, SW chunks                                       
        for (RelationObject o : f.ROMap.values()) P.add(o.values_syntax);     
        /*
        for (Stem[] syns : s.SynsList) {
            List<Stem[]> list = new ArrayList<>();
            for (Stem s : syns) list.add(new Stem[]{s});
            P.add(list);                                    
        } 
        */
        addSWPhrases(null,P);
        //endOf Phrases: objects, syns, SW chunks                       
        
        //Add QA Lemmas
        Connection db = Conn.ConnectMain();
        Statement stmt = db.createStatement();    
        ResultSet rs = stmt.executeQuery("SELECT * FROM QA ORDER BY id");        
        ArrayList<ArrayList<Stem[]>> questions_syntaxes = new ArrayList<>();
        while(rs.next()){
            Type tt = new TypeToken< ArrayList<Stem[]> >(){}.getType();
            ArrayList<Stem[]> tests_syntax = gson.fromJson(rs.getString("questions_syntax"),tt);            
            for (Stem[] stems : tests_syntax){
               for (Stem s : stems) block2:{
                  for (int i=0;i<P.size();i++) 
                       for (Stem[] Pstems : P.get(i))
                           if (Pstems.length == 1 && s.hasEqLemmas(Pstems[0])) break block2;
                  List<Stem[]> list = new ArrayList<>();
                  list.add(new Stem[]{s});
                  P.add(list);
               }                
            }     
            questions_syntaxes.add(tests_syntax);
        }
        rs.close();stmt.close();db.close();
        //endOf Add QA Lemmas
        g.printToFile(g.Path+"/model/phrasesQA.txt", gson.toJson(P)); 
        
        //Add General Dialogs Lemmas                              
        for (int idx=0;idx<f.GDList.length;idx++){
           GDNode node = f.GDList[idx];                    
           for (Stem[] stems : node.questions_syntax){
               for (Stem s : stems) block1:{
                  for (int i=0;i<P.size();i++) 
                       for (Stem[] Pstems : P.get(i))
                           if (Pstems.length == 1 && s.hasEqLemmas(Pstems[0])) break block1;
                  List<Stem[]> list = new ArrayList<>();
                  //System.out.println("GDNodeStem - "+s.text);
                  list.add(new Stem[]{s});
                  P.add(list);
               }
           }
        }
        //endOf Add GeneralDialogs Lemmas
        g.printToFile(g.Path+"/model/phrasesJoined.txt", gson.toJson(P));                        
        
        f.loadData();//Load new PQA and P
     
        
        List<double[]> PIdsList = new ArrayList<>();
        List<Integer> PAnsIdsList = new ArrayList<>();
        List<double[]> GDPIdsList = new ArrayList<>();
        List<Integer> GDPAnsIdsList = new ArrayList<>();        
        List<Integer> AnsClasses = new ArrayList<>();
        
        //QA     
        for (int qIdx = 0; qIdx < questions_syntaxes.size();qIdx++){
            System.out.println("DE Training QA idx - "+qIdx);            
            List<Stem[]> tests_syntax = questions_syntaxes.get(qIdx);
            //take only half for training set. Use all for production version
            if (g.run_mode == 1) tests_syntax = tests_syntax.subList(0, tests_syntax.size()/2 + (tests_syntax.size()%2));            
            for (Stem[] stems : tests_syntax){
                f.queryStems = stems;
                double[] PIds = f.getQueryInfo().PIdsQA;
                PIdsList.add(PIds.clone());
                PAnsIdsList.add(qIdx);            
                AnsClasses.add(0);              
            }            
        }           
        g.printToFile(g.Path+"/model/XTrainQA.txt", gson.toJson(PIdsList));        
        g.printToFile(g.Path+"/model/YTrainQA.txt", gson.toJson(PAnsIdsList));                                                
        //endOf QA

        //GD                       
        for (int idx=0;idx<f.GDList.length;idx++){
          GDNode node = f.GDList[idx];               
          //take only half for training set. Use all for production version
          if (g.run_mode == 1) node.questions_syntax = (node.questions_syntax.subList(0, node.questions_syntax.size()/2 + (node.questions_syntax.size()%2)));
          for (Stem[] stems : node.questions_syntax){
              f.queryStems = stems;
              double[] PIds = f.getQueryInfo().PIds;              
              GDPIdsList.add(PIds.clone());
              GDPAnsIdsList.add(idx);            
              PIdsList.add(PIds.clone());
              AnsClasses.add(1);
          }
        }
        //endOf GD    
        g.printToFile(g.Path+"/model/XTrainGD.txt", gson.toJson(GDPIdsList));                
        g.printToFile(g.Path+"/model/YTrainGD.txt", gson.toJson(GDPAnsIdsList));
        g.printToFile(g.Path+"/model/XTrainSeparator.txt", gson.toJson(PIdsList));                
        g.printToFile(g.Path+"/model/YTrainSeparator.txt", gson.toJson(AnsClasses));
        
        f.cl.train(); //Train ML model
        f.cl.load(); //reload the ML model
        
        f.reini();//finally renew everything
        
        System.out.println("DialogsEngine Training finished");
        } catch(Exception e){
            System.out.println("ERR->GialogsEngine->Training - " + gson.toJson(e.getStackTrace()));
            e.printStackTrace();
        }
        isTraining = false;        
    }
    
    
 void addSWPhrases(SWNode node, List<List<Stem[]>> P){
    if (node == null) {
        for (int idx=0;idx<f.SWTree.length;idx++) addSWPhrases(f.SWTree[idx], P);
    } else if (node.type == 1) block2:{
    for (int i=0;i<P.size();i++) 
        for (Stem[] Pstems : P.get(i)){                
            boolean found1 = true, found2 = true;
            for (Stem s : Pstems) if (!s.existsInSentence(node.val_syntax)) found1 = false;
            for (Stem s : node.val_syntax) if (!s.existsInSentence(Pstems)) found2 = false;                                                
            if (found1 && found2) {                    
                System.out.println(i+" old- "+gson.toJson(P.get(i))+"\n"+gson.toJson(node.val)+"\n");
                break block2;
            }
        }
        List<Stem[]> list = new ArrayList<>(1);
        list.add(node.val_syntax);                
        P.add(list);
        System.out.println(P.size()+" new- "+gson.toJson(node.val_syntax));        
    }   
    
    if (node !=null)
    for (SWNode child : node.children) addSWPhrases(child, P);              
 }
 
}
