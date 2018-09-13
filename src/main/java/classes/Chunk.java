/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.ArrayList;


/**
 *
 * @author admin
 */
public class Chunk {
    public String phrase;
    public ArrayList<Stem> stems = new ArrayList<Stem>();           
    public Chunk(String phrase){
        this.phrase = phrase;
    }
    
    public boolean isRule(int k, String pos){
        if (k == 1){
            String prevPos = stems.get(stems.size()-1).getPOS();
            if ((pos.equals("APRO") || pos.equals("PR")) 
                    && !(prevPos.equals("APRO") || prevPos.equals("PR")) ) 
                        return false;            
        }
        return true;
    }
    
    public Stem[] stemsAsArray(){
        return stems.toArray(new Stem[stems.size()]);
    }
}
