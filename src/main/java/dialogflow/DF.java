/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogflow;

/**
 *
 * @author admin
 */
import cm.API;
import static cm.API.detectIntentTexts;
import com.google.gson.Gson;
import global.Conn;
import global.Global;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class DF {
    Global g = new Global();
    public static void main(String args[]) throws Exception {
        DF df = new DF();
        df.getResponse("привет");
    }
    
    public DFNode getResponse(String question) throws Exception {
        //curl 'https://api.dialogflow.com/v1/query?v=20170712&query=hi&lang=en&sessionId=1cf9c76b-c880-0535-7416-426430832774&timezone=Asia/Almaty' -H 'Authorization:Bearer 538073c6d4b54dc1a8bea4dea756ef56'
        //curl 'https://api.dialogflow.com/v1/query?v=20170712&query=hi&lang=en&sessionId=1cf9c76b-c880-0535-7416-426430832774&timezone=Asia/Almaty' -H 'Authorization:Bearer a44e30cc14bb4e5a95a3d8564f21de91'
	/*
        DFNode node = new DFNode();
        question = URLEncoder.encode(question);
        
        API api = new API();
        String[] command = new String[]{"curl", "https://api.dialogflow.com/v1/query?v=20170712&query="+question+"&lang="+(g.LangDefault == 2 ? "en":"ru")+"&sessionId=1cf9c76b-c880-0535-7416-426430832774&timezone=Asia/Almaty", "-H", "Authorization:Bearer "+(g.LangDefault == 2 ? "a44e30cc14bb4e5a95a3d8564f21de91":"91d2c7122e3e4c14b6ffcce6e4ae903a")};
        String jsonSt = api.getResultsFor(command);
        System.out.println(jsonSt);            
        
        JSONObject obj = new JSONObject(jsonSt);
        JSONObject result = obj.getJSONObject("result"); 
        if (result.has("metadata")){
            JSONObject metadata = result.getJSONObject("metadata");
            if (metadata.has("intentName")) node.intentName = metadata.getString("intentName");
        }
        if (result.has("action")) node.action = result.getString("action");
        if (result.has("fulfillment")){
            JSONObject fulfillment = result.getJSONObject("fulfillment");                
            node.speech = fulfillment.getString("speech");
            /*
                if (fulfillment.has("messages")){
                   String message = fulfillment.getJSONArray("messages").getJSONObject(0).getString("speech");
                   System.out.println(message);
            }*/                
	/*}*/       
	DFNode node = new DFNode();
        
        //API api = new API();
       
        /*String[] command = new String[]{"curl", "-H", "Content-Type: application/json; charset=utf-8", 
           "-H", "Authorization: Bearer "+(g.LangDefault == 2 ? "a44e30cc14bb4e5a95a3d8564f21de91":"ya29.GlwbBvFJeFO172bPC3s5McBaM4VG8JfgVkO9e3mEmy_0ReE0WPZSV3OTR1Zp9NV3kqTFV4puoGALqH1fI76xC4-ZZreFEXd7MeasguCUxTZUnTtO4bTz1CRJNQBDTg"), "-d", "{\"queryInput\":{\"text\":{\"text\":\""+question+"\",\"languageCode\":\""+(g.LangDefault == 2 ? "en":"ru")+"\"}},\"queryParams\":{\"timeZone\":\"Asia/Almaty\"}}", "https://dialogflow.googleapis.com/v2/projects/tourism-fec23/agent/sessions/1cf9c76b-c880-0535-7416-426430832775:detectIntent"};
        */
        node = API.detectIntentTexts(node, "tourism-fec23", question,
        "1cf9c76b-c880-0535-7416-426430832775", "ru");
        
        /*String jsonSt = api.getResultsFor(command);
        System.out.println(jsonSt);            
        
        JSONObject obj = new JSONObject(jsonSt);
        JSONObject result = new JSONObject();
        if (obj.has("queryResult")){
            result = obj.getJSONObject("queryResult");
        } 
        String intentName="";
        if (result.has("intent")){
            JSONObject intent = result.getJSONObject("intent");
            if (intent.has("displayName")) {
                intentName = intent.getString("displayName");
                
            }
            
        }
        if (result.has("action")) node.action = result.getString("action");
        if (result.has("fulfillmentText")){
            //JSONObject fulfillment = result.getJSONObject("fulfillment");                
            node.speech = result.getString("fulfillmentText");
           
            /*
                if (fulfillment.has("messages")){
                   String message = fulfillment.getJSONArray("messages").getJSONObject(0).getString("speech");
                   System.out.println(message);
            }*/
        /*}
        if (result.has("parameters")){
            JSONObject parameters = result.getJSONObject("parameters");
            
            switch (intentName){
                case ("through_registration"):{
                    node.intentName = intentName;
                    if (result.has("allRequiredParamsPresent")){
                        Boolean allParamsPresent = result.getBoolean("allRequiredParamsPresent");
                        if (allParamsPresent){
                            String city_from = parameters.getString("city_from");
                            String city_to = parameters.getString("city_to");                        
                            node.speech = "Информация о сквозной регистрации " + city_from + "-" +city_to;
                        }
                    }
                    break;
                }
                case ("flight_time"):{
                    node.intentName = intentName;
                    if (result.has("allRequiredParamsPresent")){
                        Boolean allParamsPresent = result.getBoolean("allRequiredParamsPresent");
                        if (allParamsPresent){
                            String city_from = parameters.getString("city_from");
                            String city_to = parameters.getString("city_to");                        
                            node.speech = "Время вылета " + city_from + "-" +city_to;
                        }
                    }
                    break;
                }
                case ("rebook_ticket"):{
                    node.intentName = intentName;
                    if (result.has("allRequiredParamsPresent")){
                        Boolean allParamsPresent = result.getBoolean("allRequiredParamsPresent");
                        if (allParamsPresent){                            
                            String ticketNumber = parameters.getString("ticket_numer");                        
                            node.speech = "Информация брони по перебронированию билета "+ ticketNumber;
                        }
                    }
                    break;
                }
                
                case ("change ticket"):{
                    node.intentName = "operator_connect";
                    break;
                }
                case ("boardingpass_lost"):{
                    node.intentName = intentName;
                    break;
                }
                case ("baggage_allowed"):{
                    node.intentName = intentName;
                    String baggage_allowed = "";
                    if (parameters.has("baggage_allowed")){
                        baggage_allowed = parameters.getString("baggage_allowed");
                    }
                    if (!baggage_allowed.isEmpty()){  

                    } else {
                        node.speech = "\"Предметы личного пользования разрешенные к перевозке :\n" +
                                            "один предмет верхней одежды или плед;\n" +
                                            "одна трость/пара костылей или скобы для ног, при условии, что пассажир не может без них обойтись;\n" +
                                            "одну женскую сумочку или портфель/сумку для компьютера;\n" +
                                            "один небольшой фотоаппарат или бинокль;\n" +
                                            "небольшое количество материалов для чтения во время полета;\n" +
                                            "одну переносную детскую кроватку;\n" +
                                            "одну складную детскую коляску (максимальный размер в свернутом состоянии 34х32х14см);\n" +
                                            "детское питание для употребления во время полета;\n" +
                                            "медикаменты, без которых пассажир не может обойтись;\n" +
                                            "маленький букет цветов.\"";
                    }
                    break;
                }
                default: break;

            }
        }
        */
        
        return node;            
    }
    
    
    public  void export() throws Exception {        
        Global g = new Global();
        Gson gson = new Gson();
        
        Connection db = Conn.ConnectMain();
        Statement stmt = db.createStatement();    
        ResultSet rs = stmt.executeQuery("SELECT * FROM QA ORDER BY id");
        
        while(rs.next()){              
            ArrayList<String> questions = new ArrayList<String>();
            questions.add(rs.getString("question"));            
            String[] tests = gson.fromJson(rs.getString("tests"), String[].class);
            for (String test : tests) if (test.length()>0) questions.add(test);
            
            JSONObject x = new JSONObject("{\n" +
"  \"id\": \"731232cc-6c64-4e99-be78-ef151e0990ed\", \n" +
"  \"name\": \"id1\",\n" +
"  \"auto\": true,\n" +
"  \"contexts\": [],\n" +
"  \"responses\": [\n" +
"    {\n" +
"      \"resetContexts\": false,\n" +
"      \"affectedContexts\": [],\n" +
"      \"parameters\": [],\n" +
"      \"messages\": [\n" +
"        {\n" +
"          \"type\": 0,\n" +
"          \"lang\": \"ru\",\n" +
"          \"speech\": \"Ответ на вопрос\"\n" +
"        }\n" +
"      ],\n" +
"      \"defaultResponsePlatforms\": {},\n" +
"      \"speech\": []\n" +
"    }\n" +
"  ],\n" +
"  \"priority\": 500000,\n" +
"  \"webhookUsed\": false,\n" +
"  \"webhookForSlotFilling\": false,\n" +
"  \"lastUpdate\": 1523340278,\n" +
"  \"fallbackIntent\": false,\n" +
"  \"events\": []\n" +
"}");
            
JSONObject qTemplate = new JSONObject("{\n" +
"    \"id\": \"c022469d-b984-43d6-acc6-a911fba54712\",\n" +
"    \"data\": [\n" +
"      {\n" +
"        \"text\": \"как открыть счет\",\n" +
"        \"userDefined\": false\n" +
"      }\n" +
"    ],\n" +
"    \"isTemplate\": false,\n" +
"    \"count\": 0,\n" +
"    \"updated\": 1523340278\n" +
"  }\n" +
"}");
///////////////////////////////////////
            int id = rs.getInt("id");
            System.out.println(id);
            
            x.put("name", "qid"+id);
            x.put("id","731232cc-6c64-4e99-be78-ef151e099"+id);
            JSONObject response = x.getJSONArray("responses").getJSONObject(0);
            response.getJSONArray("messages").getJSONObject(0).put("speech", rs.getString("answer"));
            
            JSONArray qArr = new JSONArray(); int idx = 0;
            for (String question : questions){
                JSONObject q = new JSONObject(qTemplate.toString());                
                q.put("id","c022469d-b984-43d6-acc6-a911fba54"+id);
                q.getJSONArray("data").getJSONObject(0).put("text", question);
                qArr.put(idx++,q);
            }
            
            //System.out.println(x.toString());
            //System.out.println(q.toString());
            g.printToFile(g.Path+"/temp/dialogflow/talk-asr/intents/id"+id+"_usersays_ru.json", qArr.toString(1));
            g.printToFile(g.Path+"/temp/dialogflow/talk-asr/intents/id"+id+".json", x.toString(1));
            
        }
        
    }
}
