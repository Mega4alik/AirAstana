/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cm;

import global.Global;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import static java.lang.Compiler.command;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


// Imports the Google Cloud client library
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import dialogflow.DFNode;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jdesktop.swingx.util.OS;
/**
 *
 * @author raushan
 */

public class API {
    Global g = new Global();
    public String getResultsFor(String[] command){        
        String outputString = "";
        Process curlProc;
        try {
            curlProc = Runtime.getRuntime().exec(command); 

            BufferedReader curlIn = new BufferedReader(new InputStreamReader(curlProc.getInputStream()));

            String line = "";
            try {
               while ((line = curlIn.readLine()) != null){
                //System.out.println(line.toString());
                outputString +=line.toString() + "\n";
               }


            } catch (IOException e) {
            
            }        

            curlProc.destroy();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            //System.out.println(e1);
            return null;
        }
    
        return outputString;
    }

    

  // [START dialogflow_detect_intent_text]
  /**
   * Returns the result of detect intent with texts as inputs.
   *
   * Using the same `session_id` between requests allows continuation of the conversation.
   * @param projectId Project/Agent Id.
   * @param texts The text intents to be detected based on what a user says.
   * @param sessionId Identifier of the DetectIntent session.
   * @param languageCode Language code of the query.
   */
  public static DFNode detectIntentTexts(DFNode node, String projectId, String text, String sessionId,
      String languageCode) throws Exception {
    text = text.replace("-", " "); // remove unwanted symbols
    System.out.println(text);
    // Instantiates a client
    try (SessionsClient sessionsClient = SessionsClient.create()) {
      // Set the session name using the sessionId (UUID) and projectID (my-project-id)
      SessionName session = SessionName.of(projectId, sessionId);
      System.out.println("Session Path: " + session.toString());
      
      // Detect intents for each text input
        // Set the text (hello) and language code (en-US) for the query
        Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(languageCode);

        // Build the query with the TextInput
        QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

        // Performs the detect intent request
        DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
        // Display the query result
        QueryResult queryResult = response.getQueryResult();
        
        System.out.println("====================");
        System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
        System.out.format("Detected Intent: %s (confidence: %f)\n",
            queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
        System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());
        
        node.speech = queryResult.getFulfillmentText();
        String intentName = queryResult.getIntent().getDisplayName();
            /*
                if (fulfillment.has("messages")){
                   String message = fulfillment.getJSONArray("messages").getJSONObject(0).getString("speech");
                   System.out.println(message);
            }*/
            Boolean allRequiredParamsPresent = queryResult.getAllRequiredParamsPresent();
            Struct l = queryResult.getParameters();
            switch (intentName){
                case ("through_registration"):{
                    node.intentName = intentName;
                    if (allRequiredParamsPresent){
                        
                            Value city_from = l.getFieldsOrDefault("city_from", null);
                            Value city_to = l.getFieldsOrDefault("city_to", null);                        
                            node.speech = "Информация о сквозной регистрации " + city_from.getStringValue() + "-" +city_to.getStringValue();

                    }
                    break;
                }
                case ("flight_time"):{
                    node.intentName = intentName;
                    if (allRequiredParamsPresent){
                             Value city_from = l.getFieldsOrDefault("city_from", null);
                             Value city_to = l.getFieldsOrDefault("city_to", null);                                   
                             node.speech = "Время вылета " + city_from.getStringValue() + "-" +city_to.getStringValue();
                        }
                    
                    break;
                }
                case ("rebook_ticket"):{
                    node.intentName = intentName;
                    if (allRequiredParamsPresent){
                            Value ticketNumber = l.getFieldsOrDefault("ticket_numer", null);                        
                            node.speech = "Информация брони по перебронированию билета "+ ticketNumber.getStringValue();
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
                    Value baggage_allowed = l.getFieldsOrDefault("baggage_allowed", null);

                    if (!baggage_allowed.getStringValue().isEmpty()){  

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
        
        return node;
  }
    
    



  public static void main(String[] args) throws Exception{
        
    }
}
    


