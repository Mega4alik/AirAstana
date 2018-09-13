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
public class SWNode{
    
    public int type=1;
        
    public transient SWNode parent = null;
    
    
    public String ansId = null, val="";
    
    
    public Stem[] val_syntax = null;
    
    
    public ArrayList<SWNode> children = new ArrayList<SWNode>();    
}
