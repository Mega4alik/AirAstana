/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import graphs.DGraphNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */

public class DNode {
    public int r = 0; //1-Q&A, 2-generalDialog, 3-Map, 4 - fb-generic answer, 5 - currency, 6 - QuickReply menu, 7-SearchAPI, 8-DGraph, 81-dispatch, 9-ForwardToConsultant, 10-returnNothing, 11-fb-button template, 12-tg-inline-keyboard, 13-tg-custom, 14-image
    public String question="", answer = "", fb_answer, tg_answer, lastMessage, qid, qrFamily;           
    public double prob = 1.0; //probability of answer
    public int id = 0; //id from QAdb or GD
    public ArrayList<Object> list = new ArrayList<Object>();
    public QRFamily quickReply;
    public String GraphNodeId;    
    public List<DGraphNode> graphs;
        
    
    public void GraphNodeAdd(DGraphNode dgn){
     if (graphs == null) graphs = new ArrayList<DGraphNode>();
     graphs.add(dgn);
    }
    
    public String getDGNodeResponse(String dgId){
        if (graphs!=null)
            for (int i=graphs.size()-1;i>=0;i--){
                DGraphNode dgn = graphs.get(i);
                if (dgn.id.equals(dgId))
                    return dgn.response;
            }
        return null;
    }
    
    public DGraphNode getDGNode(String dgId){
        if (graphs!=null)
            for (int i=graphs.size()-1;i>=0;i--){
                DGraphNode dgn = graphs.get(i);
                if (dgn.id.equals(dgId))
                    return dgn;
            }
        return null;
    }
    
    public DGraphNode getLastGraph(){
        if (graphs!=null) return graphs.get(graphs.size()-1);
        return null;        
    }

}

