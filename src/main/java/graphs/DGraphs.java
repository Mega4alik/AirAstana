/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphs;

/**
 *
 * @author admin
 */

import classes.DNode;
import classes.QRFamily;
import classes.QRNode;
import com.google.gson.Gson;
import dispatch.DispatchInfo;
import global.DB;
import global.Global;
import graphs.DGraphRespOption;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.json.JSONObject;


public class DGraphs {
    Global g = new Global();
    Gson gson = new Gson();    
    public static DGraphNode[] graphs;    
    static boolean initialized = false;             
    
    public static void main(String args[]) throws Exception{
        DGraphs dg = new DGraphs();                
    }
    
    
    
    public DGraphs() throws Exception{
     if (!initialized || initialized) {         
         initialized = true;
         ini();
     }
    }    
    
    void ini() throws Exception{
        List<DGraphNode> a = new ArrayList<DGraphNode>();                
        DGraphNode q;       
        
        //importDispatches(a);//Dispatches import, not closing connections                                       
                
        q = new DGraphNode();
        q.id = "dt_menu";
        q.required = 0;//3
        q.respOptions = new DGraphRespOption[5];
        q.respOptions[0] = new DGraphRespOption("dt_apartment", new String[]{"1", "Купить недвижимость"});
        q.respOptions[1] = new DGraphRespOption("dt_for_owners", new String[]{"2", "Владельцам"});
        q.respOptions[2] = new DGraphRespOption("dt_contacts", new String[]{"3", "Контакты"});
        q.respOptions[3] = new DGraphRespOption("_FOR", new String[]{"4", "Перейти на оператора"});
        q.respOptions[4] = new DGraphRespOption("dt_call", new String[]{"5", "Заказать звонок"});        
        q.quickReply = new QRFamily();
        q.quickReply.title = "Вас приветствует BI-GROUP Bot, чем я могу помочь?";
        q.quickReply.children.add(new QRNode("Купить недвижимость","1",2));
        q.quickReply.children.add(new QRNode("Владельцам","2",2));  
        q.quickReply.children.add(new QRNode("Контакты","3",2));                                        
        q.quickReply.children.add(new QRNode("Перейти на оператора","4",2));                                      
        q.quickReply.children.add(new QRNode("Заказать звонок","5",2));                                              
        q.tg_answer = "{\"answer_type\":13, \"value\":{\"text\":\"Вас приветствует BI-GROUP Bot, чем я могу помочь? \",\"addon\": {\"reply_markup\": {\"keyboard\": [[\"🏡 Купить недвижимость\",\"💼 Владельцам\"],[\"ℹ️ Контакты\"],[\"👩 Перейти на оператора\"], [\"📞 Заказать звонок\"]] }}}}";
        a.add(q);
        
        
        q = new DGraphNode();
        q.id = "dt_apartment";
        q.required = 3;
        q.respOptions = new DGraphRespOption[4];        
        q.respOptions[0] = new DGraphRespOption("dt_city", new String[]{"1", "Астана"});
        q.respOptions[1] = new DGraphRespOption("dt_city", new String[]{"2", "Алматы"});
        q.respOptions[2] = new DGraphRespOption("dt_city", new String[]{"3", "Атырау"});
        q.quickReply = new QRFamily();
        q.quickReply.title = "Купить недвижимость";
        q.quickReply.children.add(new QRNode("Астана","1",2));
        q.quickReply.children.add(new QRNode("Алматы","2",2));  
        q.quickReply.children.add(new QRNode("Атырау","3",2));                                
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_for_owners";
        q.required = 3;
        q.respOptions = new DGraphRespOption[4];        
        q.respOptions[0] = new DGraphRespOption("dt_handing_keys", new String[]{"1", "Передача ключей"});
        q.respOptions[1] = new DGraphRespOption("dt_getting_docs", new String[]{"2", "Передача документов"});
        q.respOptions[2] = new DGraphRespOption("dt_certificate", new String[]{"3", "ДОД справка на цессию"});
        q.quickReply = new QRFamily();
        q.quickReply.title = "Владельцам";
        q.quickReply.children.add(new QRNode("Передача ключей","1",2));
        q.quickReply.children.add(new QRNode("Передача документов","2",2));  
        q.quickReply.children.add(new QRNode("ДОД справка на цессию","3",2));                                
        a.add(q);
        
        
        q = new DGraphNode();
        q.id = "dt_contacts";
        q.message = "Астана - 360 360\nАлматы - 33 150 33\nАтырау - 360 360";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_handing_keys";
        q.message = "Передача ключей тут";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_getting_docs";
        q.message = "Передача документов тут";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_certificate";
        q.message = "ДОД справка тут";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_city";
        //q.next = "db_type";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_call";
        q.next = "dt_call_set";
        a.add(q);

        q = new DGraphNode();
        q.id = "dt_call_set";           
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_type";
        //q.next = "dt_object";               
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_object";
        
        a.add(q);
        
        
        q = new DGraphNode();
        q.id = "dt_price";
        q.next = "dt_price_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_price_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_price_office";
        q.next = "dt_price_office_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_price_office_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_square_office";
        q.next = "dt_square_office_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_square_office_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_price_cottage";
        q.next = "dt_price_cottage_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_price_cottage_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_square_cottage";
        q.next = "dt_square_cottage_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_square_cottage_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_real_estate";
        a.add(q);
        
        
        q = new DGraphNode();
        q.id = "dt_room_count";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_room_count_set";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_real_estate_by_room";
        a.add(q);
        
        
        q = new DGraphNode();
        q.id = "dt_complex";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_complex_set";        
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_special_offers";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "dt_cottage_type";
        a.add(q);
         
        q = new DGraphNode();
        q.id = "dt_bi";
        q.message = "BI-Group";
        a.add(q);        
        //endOf CUSTOMIZATION
        
        q = new DGraphNode();
        q.id = "command_finish";
        q.message = "Действие завершено, чтобы выйти в главное меню впишите /menu";
        q.tg_answer = "{\"answer_type\":1,\"value\":\"Действие завершено\"}";        
        a.add(q);
        
        q = new DGraphNode();
        q.id = "_FOR";
        q.message = "Вы переключены на оператора. Пожалуйста ожидайте";
        a.add(q);        
        
        //_RATE-Bot        
        q = new DGraphNode();
        q.id = "_RATE-Bot";
        q.message = "Оцените пожалуйста сервис по шкале от 1 до 5, где 5 = отлично, 1 = очень плохо"; 
        q.required = 3;//respOption:yes,no        
        /*
        q.respOptions = new DGraphRespOption[5];
        q.respOptions[0] = new DGraphRespOption("rbot_rate",new String[]{"1","_1","one"});        
        q.respOptions[1] = new DGraphRespOption("rbot_rate",new String[]{"2","_2","two"});        
        q.respOptions[2] = new DGraphRespOption("rbot_rate",new String[]{"3","_3","three"});        
        q.respOptions[3] = new DGraphRespOption("rbot_rate",new String[]{"4","_4","four"});        
        q.respOptions[4] = new DGraphRespOption("rbot_rate",new String[]{"5","_5","five"});        
        */
        q.quickReply = new QRFamily();
        q.quickReply.title = "Ваша оценка:";
        q.quickReply.children.add(new QRNode("1","1",2));
        q.quickReply.children.add(new QRNode("2","2",2));
        q.quickReply.children.add(new QRNode("3","3",2));
        q.quickReply.children.add(new QRNode("4","4",2));
        q.quickReply.children.add(new QRNode("5","5",2));
        a.add(q);
        
        q = new DGraphNode();
        q.id = "rbot_text";                    
        q.message = "Большое спасибо за оценку! Пожалуйста, напишите нам что Вам понравилось или не понравилось в сервисе";
        q.required = 1; 
        q.next = "rbot_rate";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "rbot_rate";                    
        q.event = "RateBot.Rate";                
        q.next = "rbot_success";
        a.add(q);
        
        q = new DGraphNode();
        q.id = "rbot_success";                           
        q.message = "Спасибо!";
        a.add(q);   
        //endOf RATE_Bot
                                                             
        
        
        graphs = new DGraphNode[a.size()];
        a.toArray(graphs);   
                
    }
    
    public DGraphNode getNodeById(String id) throws Exception {       
        for (DGraphNode node : graphs)
            if (node.id.equals(id)) {
                DGraphNode newNode = node.clone();                
                return newNode;
            }
        return null;
    }    
    
    public void importDispatches(List<DGraphNode> a) throws Exception{
        DB dbQ = new DB();
        DGraphNode q;
        List<DispatchInfo> dispatches = dbQ.getALLDispatchInfos();
        for (DispatchInfo ds : dispatches){
            q = new DGraphNode();
            q.id = "ds_"+ds.id;
            q.message = ds.getDefaultMessageContent();            
            if (ds.responses!=null && ds.responses.length>0){
                q.required = 3;
                q.respOptions = new DGraphRespOption[ds.responses.length];
                q.quickReply = new QRFamily();
                q.quickReply.title = "";
                for (int i=0;i<ds.responses.length;i++){
                    String val = "ds_"+ds.id+"_"+i;
                    q.respOptions[i] = new DGraphRespOption("ds_"+ds.id+"_final",new String[]{val});
                    q.quickReply.children.add(new QRNode(ds.responses[i],val,2));                    
                }
            } else q.required = 1;
            q.next = "ds_"+ds.id+"_final";
            a.add(q);       
            
            //final message
            q = new DGraphNode();
            q.id = "ds_"+ds.id+"_final";
            q.message = ds.final_message;            
            a.add(q);                           
        }
        dbQ.finish();
    }
            
}


