/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;

import classes.Candidate;
import classes.DNode;
import classes.GDNode;
import classes.QRFamily;
import classes.QRNode;
import classes.SWNode;
import classes.RelationObject;
import classes.Stem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Scripts {
    Connection db;
    Global g = new Global();
    Gson gson = new Gson();    
    Function f;    
    Mind io;
    
    public static void main(String args[]) throws Exception {       
       Scripts s = new Scripts();
       //s.objectsAndSynsConvert();              
       //s.temp();
       s.SWConvert();
       //s.generalDialogsStemmatize();
       //s.generalDialogsConvert();       
       //s.generalDialogsTest();
       //s.quickRepliesConvert();       
       //s.test();
       //s.testQA(2);           
       //s.newQATest();
       //s.outputDB();                                   
       //s.insertInGD();       
       //s.getQASyntax();
       //s.QAStemmatize();
    }
        
    
    public Scripts() throws Exception {
        db = global.Conn.ConnectMain();
        f = new Function(1);
        io = new Mind();
        io.setFBSender("1154746731270978");
    }
    
    public void temp() throws Exception {
        //Runtime.getRuntime().exec("cd /Users/admin/Desktop/python/ailabs/aicc/");
        Process p = Runtime.getRuntime().exec("/Library/Frameworks/Python.framework/Versions/2.7/Resources/Python.app/Contents/MacOS/Python /Users/admin/Desktop/python/ailabs/aicc/main.py 2 [[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]]");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream())); 
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        // read the output from the command
        
        String s, ans = "";
        while ((s = stdInput.readLine()) != null) {ans+=s;}
        
        System.out.println("Result = "+ans);
        
        // read any errors from the attempted command
        //System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            //System.out.println(s);
        }
        
        stdInput.close();
        stdError.close();        
    }
    
    public void getQASyntax() throws Exception {
        Connection db = Conn.ConnectMain();
        PreparedStatement pstmt = db.prepareStatement("UPDATE QA SET tests = ?, questions_syntax = ? WHERE id = ?");        
        Statement stmt = db.createStatement();  
        ResultSet rs = stmt.executeQuery("SELECT * FROM QA ORDER BY id");        
        while(rs.next()){                
            ArrayList<String> tests = new ArrayList<String>();
            ArrayList<Stem[]> tests_syntax = new ArrayList<Stem[]>();            
            int id = rs.getInt("id");
            System.out.println(id);
            String question = rs.getString("question");
            tests_syntax.add(f.getStemsClean(f.getStems(question)));
            String testsSt = rs.getString("tests")+"\n"+rs.getString("tests2");
            String[] arr = testsSt.split("\n");                        
            for (String st : arr){
                st = st.trim();
                if (st.length() > 0 && !st.equals("null")) {
                    tests.add(st);                    
                    tests_syntax.add(f.getStemsClean(f.getStems(st)));
                }
            }            
            pstmt.setString(1, gson.toJson(tests));
            pstmt.setString(2, gson.toJson(tests_syntax));
            pstmt.setInt(3,id);
            pstmt.execute();            
        }            
    }

    public void QAStemmatize() throws Exception {
        Connection db = Conn.ConnectMain();
        PreparedStatement pstmt = db.prepareStatement("UPDATE QA SET questions_syntax = ? WHERE id = ?");  
        Statement stmt = db.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM QA ORDER BY id");        
        while(rs.next()){
            String[] tests = gson.fromJson(rs.getString("tests"), String[].class);
            ArrayList<Stem[]> tests_syntax = new ArrayList<Stem[]>();
            int id = rs.getInt("id");
            System.out.println(id);
            
            String question = rs.getString("question");            
            tests_syntax.add(f.getStemsClean(f.getStems(question)));
                                    
            for (String st : tests){
                st = st.trim();
                if (st.length() > 0 && !st.equals("null")) {                    
                    tests_syntax.add(f.getStemsClean(f.getStems(st)));
                }
            }            
            
            pstmt.setString(1, gson.toJson(tests_syntax));
            pstmt.setInt(2,id);
            pstmt.execute();            
        }            
    }

    
    
    public void test() throws Exception {
        String[] tests = new String[]{                                   
            "start","начать", 
            "оставить отзыв о боте", 
            "перейти на оператора",
            "кто ты", "как дела",
            "сколько литров в молоке"
        };
        for (String st :tests)
            System.out.println(gson.toJson(io.f.getClosestQA(st, null)));
    }   
    

    void testQA(int version) throws Exception {                      
        Connection db = Conn.ConnectMain();
        Statement stmt = db.createStatement();    
        ResultSet rs = stmt.executeQuery("SELECT * FROM QA ORDER BY id");        
        int i = 0;
        while(rs.next()){
            int id = f.qids.get(i);
            String correct_q = f.questions.get(i);            
            
            Type tt = new TypeToken<List<Stem[]>>(){}.getType();
            ArrayList<Stem[]> questions_syntax = gson.fromJson(rs.getString("questions_syntax"), tt);
            for (Stem[] stems : questions_syntax) {
                String question = ""; for (Stem stem : stems) question+=stem.text+" ";
                DNode res = (version==1 ? f.getClosestQAV1(id+"."+". "+question) : f.getClosestQA(id+"."+question, stems));
                //System.out.println("c = "+correct_q+", p = "+res.answer);                
                System.out.println(res.id == id ? "Correct" : "INCORRECT");
                System.out.println("////// \n");                
            }
            i++;
        }                
    }
    
    void generalDialogsTest() throws Exception {
        for (GDNode node : f.GDList) 
        for (int i=0;i<node.questions.size();i++){            
            String correct_q = node.questions.get(i);
                if (!correct_q.trim().isEmpty()){
                    DNode res = f.getClosestQA(correct_q, null);
                    if (res!=null) {
                        System.out.print(node.answer+" p = "+res.answer);                        
                        System.out.println(res.answer.equals(node.answer) ? "CORRECT" : "INCORRECT");                        
                    }
                    System.out.println("////// \n");
                }                                
        }
    }    
        
                                 
    void SWConvert() throws Exception{
       Scanner in = new Scanner(new FileReader(g.Path+"/data/semanticweb.txt"));       
       int lastTabs = 0;
       ArrayList<SWNode> SWTree = new ArrayList<SWNode>();
       SWNode t = new SWNode();       
       while(in.hasNextLine()){
           String st = in.nextLine();
           System.out.println(st);
           if (st.trim().isEmpty() || st.startsWith("*")) continue;
           SWNode node = new SWNode();
           //Create Node
           int Tabs = 0;for (int i=0;i<st.length();i++) if (st.charAt(i) == '	') Tabs++; else break;
           String[] a = st.split(" = ");
           node.val = a[0].trim();                      
           node.val_syntax = gson.fromJson(f.getSyntax(node.val), Stem[].class);                                       
           node.val_syntax = f.getStemsClean(node.val_syntax);
           if (node.val.startsWith("_")) node.type = 2;
           if (a.length > 1) node.ansId = a[1].trim();               
           //endOf Create Node
           //add Node
           if (Tabs > lastTabs) {
               node.parent = t;
               t.children.add(node);               
           } else {
              while(lastTabs > Tabs) {lastTabs--; t = t.parent;}
              if (t.parent == null) SWTree.add(node); else {node.parent = t.parent; t.parent.children.add(node);}
           }
           t = node;
           //endOf addNode           
           lastTabs = Tabs;
       }       
       in.close();       
       g.printToFile(g.Path+"/data/semanticweb_data.txt",gson.toJson(SWTree));
    }
    
    void objectsAndSynsConvert() throws Exception{               
        //Relation Objects
        HashMap<String, RelationObject> map = new HashMap<String, RelationObject>();
        BufferedReader in = new BufferedReader(new FileReader(g.Path+"data/objects.txt"));        
        while(in.ready()){
            String st = in.readLine().trim();
            if (st.isEmpty()) continue;
            String[] a = st.split("=");
            if (a.length == 0) continue;
            RelationObject ro = new RelationObject();           
            String name = a[0].trim(), values =  a[1].trim();                        
            String[] d = values.split(",");
            int n = d.length;
            ro.values = new String[n];
            ro.values_syntax = new ArrayList<Stem[]>();
            for (int i=0;i<n;i++){
                String val = d[i].trim();
                ro.values[i] = val;
                ro.values_syntax.add(f.getStemsClean(gson.fromJson(f.getSyntax(val), Stem[].class)));
            }            
            map.put(name,ro);
        }        
        in.close();        
        g.printToFile(g.Path+"/data/objects_data.txt",gson.toJson(map));
        
        //Synonyms
        ArrayList<Stem[]> syns = new ArrayList<Stem[]>();
        String[] array = g.getFileContentAsArray(g.Path+"/data/synonyms.txt");        
        for (String line : array){
            String[] a = line.split(",");
            Stem[] stems = new Stem[a.length];
            for (int i=0;i<a.length;i++){
                Stem[] s = gson.fromJson(f.getSyntax(a[i].trim()),Stem[].class);                 
                stems[i] = s[0];
            }
            syns.add(stems);
        }
        g.printToFile(g.Path+"/data/synonyms_data.txt",gson.toJson(syns));          
    }
    
    void quickRepliesConvert() throws Exception{        
        BufferedReader in = new BufferedReader(new FileReader(g.Path+"/data/quickreply.txt"));
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<QRFamily> fams = new ArrayList<QRFamily>();
        while (in.ready()){
            String st = in.readLine().trim();
            if (st.isEmpty()){
                if (list.size() < 2) continue;
                QRFamily fam = new QRFamily();
                for (int i=0;i<list.size();i++){
                    String[] a = list.get(i).split(" = ");
                    System.out.println(list.get(i));
                    if (i == 0) {fam.title = a[0];fam.ansId = a[1];} 
                    else {
                        if (a.length == 3 && a[2].equals("parent")){
                            fam.parentTitle = a[0];
                            fam.parentId = a[1];
                        } else {
                            fam.children.add(new QRNode(a[0],a[1],a[1].startsWith("_") ? 2 : 1));
                        }
                    }
                }
                list.clear();
                fams.add(fam);
            } else list.add(st);
        }
        in.close();        
        g.printToFile(g.Path+"/data/quickreply_data.txt",gson.toJson(fams));        
    }
    
    void generalDialogsConvert() throws Exception {
        Statement stmt = db.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM GeneralDialogs ORDER BY id");
        ArrayList<GDNode> arr = new ArrayList<GDNode>();                
        while(rs.next()){
            GDNode node = new GDNode();
            String[] a = rs.getString("questions").split(",");
            for (String st : a) if (st!=null && st.trim().length() > 0) {
                st = st.trim();
                System.out.println(st);
                node.questions.add(st);                
                //Stem[] s = gson.fromJson(f.getSyntax(st),Stem[].class);                                                 
                //node.questions_syntax.add(s);
            }
            Type tt = new TypeToken<ArrayList<Stem[]>>(){}.getType();
            node.questions_syntax = gson.fromJson(rs.getString("questions_syntax"), tt);
            node.answer = rs.getString("answer");
            arr.add(node);
        }
        g.printToFile(g.Path+"/data/general_dialog_data.txt",gson.toJson(arr));
    }
    
    void generalDialogsStemmatize() throws Exception {
        PreparedStatement pstmt = db.prepareStatement("UPDATE GeneralDialogs SET questions_syntax = ? WHERE id = ?");
        Statement stmt = db.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM GeneralDialogs WHERE questions_syntax IS NULL");
        ArrayList<GDNode> arr = new ArrayList<GDNode>();                
        while(rs.next()){
            GDNode node = new GDNode();
            String[] a = rs.getString("questions").split(",");
            for (String st : a) if (st!=null && st.trim().length() > 0) {            
                st = st.trim();
                System.out.println(st);
                Stem[] s = gson.fromJson(f.getSyntax(st),Stem[].class);                 
                node.questions_syntax.add(f.getStemsClean(s));                
            }
            pstmt.setString(1, gson.toJson(node.questions_syntax));
            pstmt.setInt(2, rs.getInt("id"));            
            pstmt.execute();
        }
    }    
        
       
    void newQATest() throws Exception {
        String a[] = g.getFileContentAsArray(g.Path+"/data/newinqa.txt");
        for (String question : a){
            System.out.println(question + "\n" + gson.toJson(f.getClosestQA(question, null)) + "\n");
        }
    }
    
    void bankRelatedWordsConvert() throws Exception{
        String[] words = g.getFileContentAsArray(g.Path+"/dictionary/bank_words_v1.txt");
        StringBuffer sb = new StringBuffer(); 
        HashSet<String> set = new HashSet<String>();        
        int k = 0; 
        for (String w : words) {
            sb.append(w+",");
            if (++k % 100 == 0){
                System.out.println("Stemming "+words.length+", k= "+k);
                Stem[] stems = f.getStemsClean(f.getStems(sb.toString()));        
                for (Stem s : stems) set.add(s.getLemma());
                sb = new StringBuffer();
            }
        }
        
        
        g.printToFile(g.Path+"/data/bankrelatedwords_data.txt",gson.toJson(set));
    }
        
    void ini() throws Exception{
        f.initialized = false;
        f.questions.clear();f.answers.clear();f.answer_types.clear();
        io.initialized = false;  
        io.ini();
    }
}
