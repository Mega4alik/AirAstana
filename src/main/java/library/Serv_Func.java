/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library;

import classes.DNode;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import dispatch.DispatchEngine;
import global.Conn;
import global.Function;
import global.Global;
import global.Mind;
import global.Track;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import ml.DialogsEngine;
import org.json.JSONObject;


public class Serv_Func extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");//new UNTESTED
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            Global g = new Global();
            Mind io = new Mind();                                    
            Function f = new Function(1);                            
            
            g.setRequest(request);            
            String action = g.getSt("action"), callback = g.getSt("callback"); 
            int channel = g.getInt("channel");//1-whatsapp, 2-telegram, 3-facebook
            System.out.println(action+" <- Action");
            
            if (channel == 1){//wp
                String wp_id = g.getSt("wp_id");
                io.setWPSender(wp_id);
            } else if (channel == 2){//tg
                String chatId = g.getSt("chatId"), userId = g.getSt("userId");
                String firstName = g.getSt("firstName"), lastName = g.getSt("lastName"), userName = g.getSt("userName");
                io.setTelegramSender(chatId, userId, userName, firstName, lastName);
            } else if (channel == 4){ //web
                int user_id = g.getInt("user_id");
                io.setUser(user_id); //directly by user_id
            } else if (channel == 5){//skype
                String skypeId = g.getSt("skypeId"), name = g.getSt("name");
                io.setSkypeSender(skypeId,name);
            }             
            else 
            { //fb - 3
                String sender_id = g.getSt("sender_id");            
                io.setFBSender(sender_id); //fb is default
            }                                                                        
                    
            if (action.equals("UserResponsed")){
                DNode res = new DNode();
                String type = g.getSt("type");                
                if (type.equals("text")){                    
                    String text = g.getSt("text").trim();                    
                    String text_query = URLDecoder.decode(text, "UTF-8");
                    text = text_query;                    
                    res = io.textResponsed(text);                     
                    io.track.UserText(text, io.user_id, res);
                } else if (type.equals("location")){
                    double lat = g.getDouble("lat"), lng = g.getDouble("lng");                    
                    res = io.locationResponsed(lat,lng);
                    io.track.UserLocation(lat, lng, io.user_id, res);
                }                                                 
                //io.translate(res); //translate
                out.println(gson.toJson(res));
                System.out.println("Output = "+gson.toJson(res));//LOG
            } 
            
            else
                
            if (action.equals("quickReplied")){
                String type = g.getSt("type");                                  
                String reply = g.getSt("reply");
                String sid = g.getSt("qid");
                System.out.println("QuickReplied - "+type+", "+reply+", "+sid);
                DNode res = io.quickRepled(type,reply,sid);
                io.track.QuickReplied(io.user_id, type, reply, sid, res);
                out.println(gson.toJson(res));
                System.out.println("Output = "+gson.toJson(res));//LOG
            }
                        
           
            
            else
                
            if (action.equals("isMovedToOperator")){                
                JSONObject res = new JSONObject();
                int uid = g.getInt("user_id");
                res.put("ans", io.isMovedToOperator(uid));               
                out.println(res.toString());
            }
            
            
            
            else
                           
            
            if (action.equals("IRSearch")){
                String query = g.getSt("query");
                out.println(gson.toJson(f.APISearch(query)));
                /*
                JSONObject o = new JSONObject();
                o.append("CustomSearch", f.APISearch(query));
                o.append("TextResponsed", io.textResponsed(query));                
                out.println(o.toString());
                */
            }
            
            else
                
            //DialogsEngine
            if (action.equals("entitiesUpdate")){                                              
               String jsonSt = g.getSt("entitiesMap");               
               f.ROMapUpdate(jsonSt);
               out.println("{\"r\":200}");//out.println(callback+"({r:200})");
            } else if (action.equals("GeneralDialogsUpdate")){               
               String jsonSt = g.getSt("jsonSt");
               f.GeneralDialogsUpdate(jsonSt);
               out.println("{\"r\":200}");
            } else if (action.equals("QAUpdate")){
               int id = g.getInt("id");
               String question = g.getSt("question"),
                    answer = g.getSt("answer"), fb_answer = g.getSt("fb_answer"), tg_answer = g.getSt("tg_answer");
               System.out.println(fb_answer+" "+tg_answer);
               String[] tests = gson.fromJson(g.getSt("tests"), String[].class);
               id = f.QAUpdate(id, question, tests, answer, fb_answer, tg_answer);
               out.println("{\"intent_id\":"+id+"}");
            } else if (action.equals("getTrainingStatus")){
                DialogsEngine de = new DialogsEngine();
                out.println("{\"isTraining\":"+de.isTraining+"}");
            } else if (action.equals("trainingStart")){
                DialogsEngine de = new DialogsEngine();
                de.trainingStart();
                out.println("{\"r\":200}");
            }
            //endOf DialogsEngine
            
            else 
            
            //Dispatches
            if (action.equals("runDispatch")){
                int id = g.getInt("id");
                DispatchEngine ds = new DispatchEngine();
                ds.runDispatch(id);
                out.println("{\"r\":200}");
            }
            //endOf Dispatches            
                
            else 
                
            if (action.equals("ini")){
                out.println("Ini start ");                
                f.initialized = false;
                f.questions.clear();f.answers.clear();f.answer_types.clear();
                io.initialized = false;
                io.ini();
                out.println("Ini Finished");
            }
            
            else 
                
            if (action.equals("test")){
                Connection db = Conn.ConnectMain();
                Statement stmt = db.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM QA LIMIT 1 offset 3");
                if (rs.next()) out.println(rs.getString("question")+" --q");                               
                //out.println(f.getClosestQA("Как счет открыть?"));
            }
            
            //Finish: Close all connections
            io.finish();
            
        } catch(Exception e){
           System.out.println(e.getMessage());
           out.println(e.getMessage());          
        }        
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}


