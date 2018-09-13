/*
    new push 1.0
 */
package global;

/**
 *
 * @author admin
 */
import graphs.DGraphs;
import graphs.DGraphNode;
import classes.DNode;
import classes.Location;
import classes.QRFamily;
import classes.QRNode;
import classes.User;
import cm.API;
import java.util.HashMap;
import java.util.Stack;
import com.google.gson.Gson;
import graphs.DGraphRespOption;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;



public class Mind {        
    Gson gson = new Gson();
    Global g = new Global();    
    DB dbQ = new DB();
    public Track track = new Track();    
    Function f = new Function(1);       
    DGraphs dG = new DGraphs();
    Translater translater  = new Translater();
    API api = new API();    
    
    static HashMap<Integer,Stack> Dialogs = new HashMap<Integer,Stack>();    
    static HashMap<Integer,String> UserLanguage = new HashMap<Integer,String>();    
    public static QRFamily[] QRs;        
    public static boolean initialized = false;    
    public int user_id = 0;
   
    public static void main(String args[]) throws Exception{
        Mind io = new Mind();
        
        io.setUser(1);
        
    }
      
    public Mind() throws Exception{
      ini();  
    }
        
    public void ini() throws Exception{
        if (!initialized){
            initialized = true;            
            QRs = gson.fromJson(g.getFileContent(g.Path+"/data/quickreply_data.txt"), QRFamily[].class);            
        }
    }

    public void finish(){
        dbQ.finish();
        track.finish();
    }   
    
    public void setUser(int uid){
        user_id = uid;
    }
    
    public void setWPSender(String wp_id) throws Exception {
        if (wp_id!=null) setUser(dbQ.getUserIdByWPId(wp_id));        
    }
    
    public void setFBSender(String sender_id) throws Exception {
        if (sender_id!=null) setUser(dbQ.getUserIdByFBId(sender_id));        
    }
    
    public void setTelegramSender(String chatId, String userId, String userName, String firstName, String lastName) throws Exception{
        if (chatId!=null) {
            setUser(dbQ.getUserIdByTelegramId(chatId,userId,userName,firstName,lastName));        
        }
    }
    
    public void setSkypeSender(String skypeId, String name) throws Exception{
        if (skypeId!=null) {
            setUser(dbQ.getUserIdBySkypeId(skypeId,name));        
        }
    }      
    
    public boolean isMovedToOperator(int uid){
        try{
        User u = dbQ.getUser(uid);         
        if (g.getDateDiff(g.getCurrDate(), u.lastMovedToOperator) < 0.5 * 60 * 60) return true; 
        } catch(Exception e){e.printStackTrace();}
        return false;
    }    
    
    public void historyAdd(DNode res) throws Exception{
        if (user_id == 0) return;
        Stack<DNode> list;
        if (Dialogs.containsKey(user_id)) list = Dialogs.get(user_id); else list = new Stack<DNode>();
        list.add(res);        
        Dialogs.put(user_id,list);              
    }
    
    DNode getLastDialog(){
        if (!Dialogs.containsKey(user_id)) return null;
        return (DNode)Dialogs.get(user_id).lastElement();
    }
    
    public DNode textResponsed(String text) throws Exception {                                
        
        //isMovedToOperator
        if (isMovedToOperator(user_id)){
            DNode o = new DNode();o.r = 10;return o;
        }         
                
               
        DNode last = getLastDialog(); 
        if (last != null && last.r == 8){                
            last.lastMessage = text;                
            DGraphNode lgn = last.getLastGraph();                                        
            GraphNodeIterate(last,lgn);                       
            return last;
        }       
        
        
        DNode res;
        if (g.QAML_version == 9){ //if DialogFlow
            res = f.getClosestQADF(text); //DF version
        } else if (g.QAML_version == 2){ //new
            res = f.getClosestQA(text, null);
            if (res.r == 1 && res.prob < 0.01) {dbQ.addIntentRecommendation(text.trim(),res.prob);} //add to recommendations                
        } else { //old version                
            res = f.getClosestQAV1(text); 
        }                                                               
        if (res.r == 8) {GraphNodeIterate(res, null);}
        else if (res.r==81) {}
        else if (res.r == 9) {
            dbQ.OperatorMovedSet(user_id);
            //OperatorsNotify(9,user_id);
            res.answer = g.LangDefault == 2 ? "You are now connected with an operator. Please wait" : "Вы переключены на оператора. Пожалуйста ожидайте";                        
            track.MovedToOperator(user_id);           
        }
        
        if (res.qrFamily != null) res.quickReply = rQRFam(res.qrFamily, res.qid);
        else if (res.quickReply == null && res.qid != null) res.quickReply = rParentQRFam(res.qid);
        
        res.lastMessage = text;
        historyAdd(res);       
        System.out.println("textResponsed= " + gson.toJson(res));
        return res;
    }
    
    public DNode locationResponsed(double lat, double lng) throws Exception {
        DNode last = getLastDialog(); 
        if (last!=null && last.r == 8) {
           DGraphNode lgn = last.getLastGraph();
           lgn.loc = new Location();
           lgn.loc.latitude = lat;
           lgn.loc.longitude = lng;
           GraphNodeIterate(last,lgn);
        }
        return last;
    }
    
    public DNode quickRepled(String type, String reply, String sid) throws Exception{        
        DNode last = getLastDialog();        
        DNode res = new DNode();        
        if (type.equals("reply")){
            if (last != null && last.r == 8){                
                last.lastMessage = reply;                
                DGraphNode lgn = last.getLastGraph();                
                //in case of previous buttons click when text/location etc.. is required
                if (lgn.required!=3 && !reply.startsWith("command_")) return null;                 
                GraphNodeIterate(last,lgn);                       
                return last;
            }            
            if (reply.startsWith("ds_")){//dispatch ds_id_idx
                String[] a = reply.split("_");
                int dispatch_id = Integer.parseInt(a[1]);   
                int response_idx = Integer.parseInt(a[2]);                
                track.dispatchResponsed(user_id, dispatch_id, response_idx);
                res.r = 8;
                res.GraphNodeId = "ds_"+dispatch_id;                                
                GraphNodeIterate(res,null);
                res.lastMessage = reply;
                GraphNodeIterate(res,res.getLastGraph());
            } else res.r = 6;
            //res.quickReply = rQRFam(reply, sid);
        } else if (type.equals("qid")){
            int index = f.qidx.get(g.toInt(sid));            
            res.r = f.answer_types.get(index);
            res.question = f.questions.get(index);
            res.answer = f.answers.get(index);          
            res.quickReply = rParentQRFam(sid);
        } 
        historyAdd(res);          
        return res;
    }
    
    //QuickReply
    public QRFamily rQRFam(String ansId, String sid) throws Exception{
        for (QRFamily node : QRs){
            if (node.ansId.equals(ansId)) 
                return node.normalized(sid);
        }
        return null;
    }
    
    public QRFamily rParentQRFam(String qid) throws Exception{        
        for (QRFamily fam : QRs)
            for (QRNode node : fam.children)
                if (node.ansId.equals(qid)) return fam.normalized(qid);
        return null;
    }
    //endOf QuickReply              

    //translate
    public void translate(DNode node){//to Kazakh and Russian
        try {
            String lang = node.getDGNodeResponse("_LANG-Select");
            if (lang == null && UserLanguage.containsKey(user_id)) lang = UserLanguage.get(user_id);
            

            if (lang!=null){
                String id = (node.getLastGraph()!=null ? node.getLastGraph().id : null);
                
                if (node.answer!=null) node.answer = translater.getTranslation(id, node.answer, lang);
                
                if (node.quickReply!=null &&  node.quickReply.title!=null)  
                    node.quickReply.title = translater.getTranslation(id, node.quickReply.title, lang);

                if (node.quickReply!=null && node.quickReply.children!=null)
                    for (QRNode q : node.quickReply.children){
                        q.title = translater.getTranslation(null, q.title, lang);
                    }

                UserLanguage.put(user_id, lang);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    //endOf translate
    
    //Graphs
    void GraphNodeIterate(DNode dn, DGraphNode dgn) throws Exception{           
        if (dgn == null) dgn = dG.getNodeById(dn.GraphNodeId);        
        //go back or finish
        
        if (dn.lastMessage!=null && dn.lastMessage.equals("command_back")){
            try {
                dn.graphs.remove(dgn);                                    
                DGraphNode prev = dn.getLastGraph(); 
                if (prev!=null){                
                    String prevId = prev.id;
                    dn.graphs.remove(prev);
                    prev = dG.getNodeById(prevId);
                    //System.out.println("lastGraph on back - "+gson.toJson(prev));            
                    dn.lastMessage = null;
                    GraphNodeIterate(dn, prev);
                }
                return;
            } catch(Exception e){e.printStackTrace();}
        } else if (dn.lastMessage!=null && dn.lastMessage.equals("command_finish")){
            dn.lastMessage = null;
            GraphNodeIterate(dn, dG.getNodeById("command_finish"));
            return;
        }
        //endOf go back or finish
        
        //move to operator
        if (dgn.id.equals("_FOR")){ //move to operator            
            dbQ.OperatorMovedSet(user_id);            
            dn.r = 9;
            dn.quickReply = null;dn.list = null;
            dn.answer = "Вы переключены на оператора. Пожалуйста ожидайте";            
            track.MovedToOperator(user_id);  
            return;
        }
        //endOf move to operator
        
        System.out.println("AtGNode - "+dgn.id+" dgn-"+gson.toJson(dgn)+"\n");
        
        //skip
        /*
        if (){
            System.out.println("skipping to " + dgn.next);
            GraphNodeIterate(dn, dG.getNodeById(dgn.next)); 
            return;
        }
        */
        //endOf skip
        
        if (dgn.waiting){
            dgn.response = dn.lastMessage;
            if (dgn.required == 1) dgn.waiting = false; //text           
            else if (dgn.required == 0) dgn.waiting = false;
            else if (dgn.required == 2) { //location
                if (dgn.loc!=null) dgn.waiting = false;
            } else if (dgn.required == 3){ //list value                  
                for (DGraphRespOption o : dgn.respOptions)                    
                    for (String value : o.values) 
                        if (g.removeEmojis(dgn.response).trim().equals(g.removeEmojis(value).trim())){
                        //if (dgn.response.equals(value)){
                            dgn.waiting = false;
                            if (o.action==1) GraphNodeIterate(dn, dG.getNodeById(o.dgnId));
                            return;
                        }                
                //dgn.onIncorrect();//not found                
            }
            
            if (!dgn.waiting && dgn.next != null) GraphNodeIterate(dn, dG.getNodeById(dgn.next));
        } 
        
        else         
            
        {
            GraphNodePrepareValues(dn,dgn);
            dn.answer = dgn.message;dn.fb_answer = dgn.fb_answer;dn.tg_answer = dgn.tg_answer;
            dn.quickReply = dgn.quickReply;
            if (dgn.event != null) {
                if (!GraphNodeEvent(dn,dgn)) return;
                dgn.waiting  = true;
                dn.GraphNodeAdd(dgn);
                GraphNodeIterate(dn,dgn);                
            } else if (dgn.required > 0) {
                dgn.waiting = true;
                dn.GraphNodeAdd(dgn);
            } else {
                dn.GraphNodeAdd(dgn);
                dn.r = 0;            
            }
        }
    }
    
    //CUSTOMIZATION PART    
    boolean GraphNodeEvent(DNode dn, DGraphNode dgn) throws Exception{ //true means continue iterating after Event is executed, false means not to continue
       switch(dgn.event){
            case "balance_check":{
                String inn = dn.getDGNodeResponse("dt_balance_reinter")!=null ? dn.getDGNodeResponse("dt_balance_reinter") : dn.getDGNodeResponse("dt_balance");
                System.out.println("INN - " + inn);
                break;
            }
            case "RateBot.Rate":{
                 int rate = g.toInt(dn.getDGNodeResponse("_RATE-Bot"));
                 String feedback = dn.getDGNodeResponse("rbot_text");                 
                 User u = dbQ.getUser(user_id);                
                 dbQ.addUserRate(u, rate, feedback);
                 track.UserRate(user_id, rate, feedback);
                 System.out.println("Bot rate - "+rate+", "+feedback);
                 break;
             }
       }
       return true;
    }
    
    void GraphNodePrepareValues(DNode dn, DGraphNode dgn) throws Exception{  
        // check api token        
        switch(dgn.id) { 
            
            case "_RATE-Bot" : {
                String next = "rbot_rate";
                User u = dbQ.getUser(user_id);                                    
                if (u.rates!=null && (u.rates.size() == 1 || u.rates.size() % 4 == 0)) next = "rbot_text";
                dgn.respOptions = new DGraphRespOption[5];
                dgn.respOptions[0] = new DGraphRespOption(next,new String[]{"1","_1","one"});        
                dgn.respOptions[1] = new DGraphRespOption(next,new String[]{"2","_2","two"});        
                dgn.respOptions[2] = new DGraphRespOption(next,new String[]{"3","_3","three"});        
                dgn.respOptions[3] = new DGraphRespOption(next,new String[]{"4","_4","four"});        
                dgn.respOptions[4] = new DGraphRespOption(next,new String[]{"5","_5","five"});                
                break;
            }                                    
        }
    }
    //endOf CUSTOMIZATION PART    
    //endOf Graphs   
}