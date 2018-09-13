/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

//import global.Word2Vector;
import java.util.ArrayList;

public class Stem {
    public int idx;
    public String text = "";    
    public ArrayList<StemAnalysis> analysis = new ArrayList<StemAnalysis>();    
    public boolean active = false; //used to flag existing words   
    
    public boolean isPOSeq(String pos){
        if (getPOS().equals(pos)) return true;
        return false;
    }
    
    public boolean existsInSentence(Stem[] list){
        for (Stem s : list) {
            if ((this.isPOSeq("S") || this.isPOSeq("V")) && s.isPOSeq("A")) continue;       
            if (hasEqLemmas(s)) {s.active = true; return true;}
        }
        return false;
    }
    
    public boolean existsInSentenceAsSyn(Stem[] list) throws Exception{
        Synonyms syns = new Synonyms();
        //KKB Syns
        Stem[] synsFound = syns.getSyns(this);
        if (synsFound != null) {
        for (Stem s : synsFound) 
            if (s.existsInSentence(list)) {s.active = true; return true;}
        }
        
        //Dictionary        
        for (Stem s : list)
            if (syns.isSynonym(this.text, s.text)) {s.active = true; return true;}
        
        return false;
    }
        
    public boolean hasEqLemmas(Stem s){        
        if (this.text.toLowerCase().equals(s.text.toLowerCase())) return true;
        for (StemAnalysis a1 : analysis)
            for (StemAnalysis a2 : s.analysis)
                if (a1.lex.equals(a2.lex)) 
                    return true;
        return false;
    }
    
    public String getLemma(){
        if (this.analysis.size() > 0) return this.analysis.get(0).lex;
        return this.text.toLowerCase();
    }
    
    public String getPOS(){
        if (this.analysis.size()>0) {
            String[] a = this.analysis.get(0).gr.split(",");
            a = a[0].split("=");
            return a[0];
        }
        return "";
    }
    
    /*
    public boolean existsInSentenceAsW2V(Stem[] list) throws Exception{       
        Word2Vector wv = new Word2Vector();
        if (!text.isEmpty()) 
            for (Stem s : list)
                if (s.text.trim().length() > 1 && wv.isSimilar(s.text, text)) { 
                    //System.out.println("W2V= "+s.text+" "+text);
                    return true;
                } 
        return false;
    }
    */
    
}
