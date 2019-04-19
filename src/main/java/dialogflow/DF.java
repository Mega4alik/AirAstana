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
        DFNode node = new DFNode();
        API api = new API();
        node = api.detectIntentTexts(node, "tourism-fec23", question,
        "1cf9c76b-c880-0535-7416-426430832775", "ru");
        
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
