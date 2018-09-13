/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import global.Global;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;

/**
 *
 * @author admin
 */
public class Synonyms {
    public static HashMap<String, Integer> SynsMap = new HashMap<String, Integer>();    
    public static Stem[][] SynsList;        
    public static HashMap<String, Stack> SynsDictMap = new HashMap<String, Stack>();
    public static String[][] SynsDictList;
    static boolean initialized = false;
    Gson gson = new Gson();
    Global g = new Global();    
    
    public static void main(String args[]) throws Exception{
        Synonyms s = new Synonyms();
        System.out.println(s.isSynonym("тип","вид"));
    }
    
    public Synonyms() throws Exception{
        if (!initialized) ini();
    }

    void ini() throws Exception{
        System.out.println("Syns ini Started");
        initialized = true;
        SynsList = gson.fromJson(g.getFileContent(g.Path+"/data/synonyms_data.txt"), Stem[][].class);        
        for (int i=0;i<SynsList.length;i++)
            for (Stem s : SynsList[i]){
                SynsMap.put(s.text.toLowerCase(),i);
                for (StemAnalysis sa : s.analysis) SynsMap.put(sa.lex,i);                
        }    
        
        //Synonyms Dictionary                
        /*
        String[] arr = g.getFileContentAsArray(g.Path+"/dictionary/syns_sw_v2.txt");
        SynsDictList = new String[arr.length][];
        for (int i=0;i<arr.length;i++){            
            SynsDictList[i] = arr[i].split(",");                   
            for (String si : SynsDictList[i]){
                si = si.trim();                
                Stack<Integer> d;
                if (SynsDictMap.containsKey(si)) d = SynsDictMap.get(si); else {
                    d = new Stack<Integer>();                    
                    SynsDictMap.put(si,d);                    
                }
                d.add(i);
            }                                     
        }               
        */
        //endOf Synonyms Dictionary                                                       
        
        /*
        //g.printToFile(g.Path+"/dictionary/synonyms_structured.txt", gson.toJson(SynsDictMap)); 
        String st = gson.toJson(SynsDict);
        Type tt = new TypeToken<HashMap<String, TreeSet<String>>>(){}.getType();
        SynsDict = gson.fromJson(st, tt);        
        */
        
        System.out.println("Syns ini Finished");
    }
    
    Stem[] getSyns(Stem s){        
        for (StemAnalysis sa : s.analysis)
        if (SynsMap.containsKey(sa.lex)){
            int idx = SynsMap.get(sa.lex);
            return SynsList[idx];
        }                   
        if (SynsMap.containsKey(s.text.toLowerCase())){
            int idx = SynsMap.get(s.text.toLowerCase());
            return SynsList[idx];
        }
        return null;
    }
    
    boolean isSynonym(String st1, String st2){
        st1 = st1.toLowerCase();st2 = st2.toLowerCase();
        Stack<Integer> a = SynsDictMap.get(st1);                
        if (a != null)
        for (Integer idx : a){
            for (String st : SynsDictList[idx]){                
                if (st.trim().equals(st2)) {System.out.println(st1+" S=S "+st2); return true;}
            }
        }
        return false;
    }

}
