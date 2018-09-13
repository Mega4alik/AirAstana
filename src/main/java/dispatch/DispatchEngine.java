/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dispatch;

import classes.DNode;
import classes.User;
import com.google.gson.Gson;
import global.DB;
import global.Global;
import graphs.DGraphNode;
import graphs.DGraphs;
import java.util.List;

/**
 *
 * @author admin
 */
public class DispatchEngine {
    Global g = new Global();
    Gson gson = new Gson();
    
    public static void main(String argsp[]) throws Exception{
        DispatchEngine ds = new DispatchEngine();
        ds.runDispatch(3);
    }
    
    public void runDispatch(int dispatch_id) throws Exception{
        DB dbQ = new DB();
        DGraphs dG = new DGraphs();
        List<User> users = dbQ.getAllUsers();
        
        DGraphNode dgNode = dG.getNodeById("ds_"+dispatch_id);        
        DNode node = new DNode();        
        node.quickReply = dgNode.quickReply;
        node.answer = dgNode.message;        
        for (User u : users){             
            System.out.println("dispatchSending->"+u.id);
            
            g.getURLContent(g.nodeJSURL+"/func?action=sendMessageToUser&user_id="+u.id+"&dnode="+g.encodeURL(gson.toJson(node)));    
        }
                
    }
}
