/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;

import classes.Chunk;
import classes.GDNode;
import classes.GenericElement;
import classes.RelationObject;
import classes.SWNode;
import classes.Stem;
import classes.Synonyms;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


public class Scripts2 {
 Global g = new Global();
 Function f;
 Synonyms s;
 Gson gson = new Gson();
 Connection db;
 
 public static void main(String args[]) throws Exception{
     Scripts2 s = new Scripts2();     
     //s.AIMLGDInsert();
     s.QADataExtract();     
 }
 
 public Scripts2() throws Exception{
     db = Conn.ConnectMain();
     f = new Function(1);
     s = new Synonyms();
 }
 
 
 //Experiments for AILabsResearcher Center
 List<List<Stem[]>> P = new ArrayList<>();  
 
 void QADataExtract() throws Exception{          
     boolean testMode = false;
     
     String[] testQuestions = new String[]{                                   
        "Кто Председатель совета директоров?"            
     };
               
     
     if (!testMode){      
        //Phrases: objects, syns, SW chunks                       
        for (RelationObject o : f.ROMap.values()) P.add(o.values_syntax);     
        for (Stem[] syns : s.SynsList) {
            List<Stem[]> list = new ArrayList<>();
            for (Stem s : syns) list.add(new Stem[]{s});
            P.add(list);
        }
        addSWPhrases(null);
        g.printToFile(g.Path+"/temp/phrasesQA.txt", gson.toJson(P));         
        //Add General Dialogs Lemmas                      
        for (int idx=0;idx<f.GDList.length;idx++){
           GDNode node = f.GDList[idx];         
           for (Stem[] stems : node.questions_syntax){               
               for (Stem s : stems) block1:{
                  for (int i=0;i<P.size();i++) 
                       for (Stem[] Pstems : P.get(i))
                           if (Pstems.length == 1 && s.hasEqLemmas(Pstems[0])) break block1;
                  List<Stem[]> list = new ArrayList<>();
                  list.add(new Stem[]{s});
                  P.add(list);
               }                
           }
        }     
        g.printToFile(g.Path+"/temp/phrasesJoined.txt", gson.toJson(P));     
        //endOf Add GeneralDialogs Lemmas
     }                
          
     f.loadData();//Load new PQA and P
     
     List<double[]> PIdsList = new ArrayList<>(), TestPIdsList = new ArrayList<>();     
     List<Integer> PAnsIdsList = new ArrayList<>(), TestPAnsIdsList = new ArrayList<>();;
     List<Integer> AnsClasses = new ArrayList<>();
     
     //QA     
     for (int qIdx=0;qIdx<(testMode ? testQuestions.length : f.questions.size());qIdx++){   
         List<String> questions = new ArrayList<String>(), TestQuestions = new ArrayList<String>();
         if (testMode){
            questions.add(testQuestions[qIdx]);
         } else {
            questions.add(f.questions.get(qIdx));            
            //questions.addAll(Arrays.asList(f.tests.get(qIdx)));                              
            TestQuestions.add(f.questions.get(qIdx));                                      
         }
         for (String question : questions){
            if (question == null || question.length() == 0) continue;                        
            System.out.println(question);
            Stem[] stems = f.getStemsClean(f.getStems(question));   
            f.queryStems = stems;
            double[] PIds = f.getQueryInfo().PIdsQA;
            PIdsList.add(PIds.clone());
            PAnsIdsList.add(qIdx);            
            AnsClasses.add(0);
         }
         for (String question : TestQuestions){
            if (question == null || question.length() == 0) continue;                        
            System.out.println("Test - "+question);
            Stem[] stems = f.getStemsClean(f.getStems(question));   
            f.queryStems = stems;
            double[] PIds = f.getQueryInfo().PIdsQA;
            TestPIdsList.add(PIds.clone());
            TestPAnsIdsList.add(qIdx);            
         }
     }      
     if (!testMode){
        g.printToFile(g.Path+"/temp/XTrainQA.txt", gson.toJson(PIdsList));        
        g.printToFile(g.Path+"/temp/YTrainQA.txt", gson.toJson(PAnsIdsList));                    
        g.printToFile(g.Path+"/temp/XTestQA.txt", gson.toJson(TestPIdsList));        
        g.printToFile(g.Path+"/temp/YTestQA.txt", gson.toJson(TestPAnsIdsList));                    
     }
     //endOf QA
               
     //GD          
     if (!testMode){
        for (int idx=0;idx<f.GDList.length;idx++){
          GDNode node = f.GDList[idx];         
          for (Stem[] stems : node.questions_syntax){               
              f.queryStems = stems;
              double[] PIds = f.getQueryInfo().PIds;
              PIdsList.add(PIds.clone());
              PAnsIdsList.add(f.questions.size()+idx);            
              AnsClasses.add(1);
          }
        }             
        g.printToFile(g.Path+"/temp/XTrainJoined.txt", gson.toJson(PIdsList));        
        g.printToFile(g.Path+"/temp/YTrainJoined.txt", gson.toJson(PAnsIdsList));        
        g.printToFile(g.Path+"/temp/YClasses.txt", gson.toJson(AnsClasses));
     } 
     //endOf GD         
     
 }
 
 
 void addSWPhrases(SWNode node){
    if (node == null) {
        for (int idx=0;idx<f.SWTree.length;idx++) addSWPhrases(f.SWTree[idx]);
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
    for (SWNode child : node.children) addSWPhrases(child);              
 }
 
 void GeneralDialogsDataExtract() throws Exception{
     List<int[]> X = new ArrayList<>();     
     List<Integer> Y = new ArrayList<>();     
     List<Stem> WordLemmas = new ArrayList<Stem>();
     for (int idx=0;idx<f.GDList.length;idx++){
         GDNode node = f.GDList[idx];
         
         //Fill WordLemmas and Mark X,Y
         for (Stem[] stems : node.questions_syntax) {
             int[] GDWIds  = new int[500];
             for (Stem s : stems) block1:{
                 for (Stem wl : WordLemmas) if (wl.hasEqLemmas(s)) {
                        GDWIds[WordLemmas.indexOf(wl)] = 1;
                        break block1;
                 }
                 GDWIds[WordLemmas.size()] = 1;
                 WordLemmas.add(s);
                 //System.out.println(gson.toJson(s));
             } 
             X.add(GDWIds);
             Y.add(idx);
         }
         
     }
     
     g.printToFile(g.Path+"/temp/gdin.txt", gson.toJson(X));
     System.out.println(gson.toJson(Y));     
     
    //test
     System.out.println("\nTests\n");
     X.clear();
     Y.clear();     
     String[] questions = new String[]{
        "Привет",
        "Как открыть банковский счет?",        
     };
    for (String question : questions){ 
        int[] GDWIds  = new int[500];
        Stem[] stems = f.getStemsClean(f.getStems(question));
        for (Stem s : stems)
        for (int i=0;i<WordLemmas.size();i++)
            if (WordLemmas.get(i).hasEqLemmas(s)) GDWIds[i] = 1;
        
        X.add(GDWIds);
    }
    System.out.println(gson.toJson(X));
 }
 //endOf Experiments for AILabsResearch Center
 
 


}


