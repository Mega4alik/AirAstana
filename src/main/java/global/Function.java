/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;

import classes.DNode;
import classes.SWNode;
import classes.RelationObject;
import classes.Stem;
import classes.Candidate;
import classes.Chunk;
import classes.GDNode;
import classes.GenericElement;
import classes.QueryInfo;
import classes.StemAnalysis;
import cm.AutoCorrector;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import dialogflow.DF;
import dialogflow.DFNode;
import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import ml.Classifier;
import org.json.JSONObject;

public class Function {
    Gson gson = new Gson();
    Global g = new Global();    
    public Classifier cl;
    
    public static ArrayList<String> questions = new ArrayList<String>();    
    public static ArrayList<String[]> tests = new ArrayList();        
    public static ArrayList<String[]> tests2 = new ArrayList();    
    public static ArrayList<Integer> qids = new ArrayList<Integer>();        
    public static HashMap<Integer,Integer> qidx = new HashMap<Integer,Integer>();
    public static ArrayList<String> answers = new ArrayList<String>(), fb_answers = new ArrayList<String>(), tg_answers = new ArrayList<String>();               
    public static ArrayList<Integer> answer_types = new ArrayList<Integer>();        
    public static SWNode[] SWTree;    
    public static List<List<Stem[]>> P = new ArrayList<>(), PQA = new ArrayList<>();  
    public static HashMap<String, RelationObject> ROMap;    
    public static GDNode[] GDList;        
    public static boolean initialized = false;                               
        
    
    public static void main(String args[]) throws Exception {
        Function f = new Function(1);              
        //System.out.println("idx-"+f.answerToIdx("_USER-Ask"));
        System.out.println(f.gson.toJson(f.getClosestQAV1("заказать звонок"))); //найти бухгалтера
    } 
    

    public Function(int mode) throws Exception{                
        if (mode == 1) ini();        
    }
    
    public void ini() throws Exception{
        if (g.QAML_version == 2)  cl = new Classifier();
        
        if (!initialized){
            initialized = true;            
            System.out.println("Initializing");                        
            getAllQuestions();     
            loadData();            
            System.out.println("Finished Initializing");
        }        
    }
    
    public void reini() throws Exception{
        initialized = false;
        qids.clear();qidx.clear();questions.clear();answers.clear();
        fb_answers.clear();tg_answers.clear();answer_types.clear();
        ini();
    }        
        
    
    void getAllQuestions() throws Exception {
        Connection db = Conn.ConnectMain();
        Statement stmt = db.createStatement();    
        ResultSet rs = stmt.executeQuery("SELECT * FROM QA ORDER BY id");
        int index = 0;
        while(rs.next()){                                                
            int id = rs.getInt("id");
            qids.add(id);   
            qidx.put(id,index);
            questions.add(rs.getString("question"));
            answers.add(rs.getString("answer"));                       
            fb_answers.add(rs.getString("fb_answer"));  
            tg_answers.add(rs.getString("tg_answer"));  
            answer_types.add(rs.getInt("answer_type"));                          
            index++;
        }
        stmt.close();
        db.close();
        stmt = null;db = null;
    }
    
    public void loadData() throws Exception {
        if (g.QAML_version == 2){ //new
            Type tt1_0 = new TypeToken<List<List<Stem[]>>>(){}.getType();
            PQA = gson.fromJson(g.getFileContent(g.Path+"/model/phrasesQA.txt"), tt1_0);        
        
            Type tt1 = new TypeToken<List<List<Stem[]>>>(){}.getType();
            P = gson.fromJson(g.getFileContent(g.Path+"/model/phrasesJoined.txt"), tt1);        
        } 
        
        if (g.QAML_version == 1 || g.QAML_version == 2){
            SWTree = gson.fromJson(g.getFileContent(g.Path+"/data/semanticweb_data.txt"), SWNode[].class);


            Type tt2 = new TypeToken<HashMap<String, RelationObject>>(){}.getType();
            ROMap = gson.fromJson(g.getFileContent(g.Path+"/data/objects_data.txt"), tt2);                

            GDList = gson.fromJson(g.getFileContent(g.Path+"/data/general_dialog_data.txt"), GDNode[].class);                                    
        }
    }
    
    //Closest Question    
    public Stem[] queryStems;
    int QmaxLen;
    ArrayList<SWNode> QNodes = new ArrayList<SWNode>();    
    
    public DNode getClosestQA(String question, Stem[] qStems) throws Exception{ //v2
        System.out.println("QueryV2 = " + question);        
        DNode res = new DNode();            
        queryStems = (qStems == null ? getStems(question) : qStems);
        QueryInfo qi = getQueryInfo();       
        
        JSONObject obj = cl.getQuestionIdx(qi.PIds, qi.PIdsQA);
        int mode = obj.getInt("mode"), qIdx = obj.getInt("qIdx");  //mode (1-QA, 2-GD)                                
        res.prob = obj.getDouble("prob");               
        System.out.println("getQAv2.mode="+mode+"-> " + (mode==1 ? questions.get(qIdx) : GDList[qIdx].answer) + "prob= "+res.prob);                                
                
        if (mode == 1 && res.prob > 0.03){ //new 
            String ans = answers.get(qIdx); //new
            res.id = qids.get(qIdx);
            if (ans.startsWith("_MAP")){
                res.r = 3;
                res.question = ans;
                res.answer = "Отправьте пожалуйста ваше местоположение:\n• Нажмите 📍 \n• Выберите 'Нынешнее местонахождение'\n• Нажмите 'Отправить'";                
            } else if (ans.startsWith("_QR")){
                res.r = 6;                                
                res.qrFamily = ans.replace("_QR","");                                
            } else if (ans.startsWith("_RATE") || ans.startsWith("dt_")){
                res.r = 8;
                res.GraphNodeId = ans;
            } else if (ans.startsWith("ds_")){//dispatch
                res.r = 81;
                res.GraphNodeId = ans;
            } else if (ans.startsWith("_FOR") || ans.startsWith("_OPERATOR")){
                res.r = 9;
            } else {
                //int index = cand.idx; 
                int index = qIdx;                
                res.r = answer_types.get(index);
                res.qid = ans;//?
                res.question = questions.get(index);
                res.answer = answers.get(index);
                res.fb_answer = fb_answers.get(index);
                res.tg_answer = tg_answers.get(index);
            }
        } else if (mode == 2 && res.prob > 0.2 && !qi.hasUnknownWords){
            //General Dialogs                      
            //ML based
            res.r = 2;
            res.answer = GDList[qIdx].answer;
            if (res.answer.equals("_DATETIME")) res.answer = "Сегодня "+g.getCurrDateIn("yyyy-MM-dd");
            else if (res.answer.equals("_TIME")) res.answer = g.getCurrDateIn("H:m:s");
            //endOf General Dialogs                                                              
        } else {
            //Google API
            /*String APIJsonSt = APISearch(question);
            if (APIJsonSt != null) {
                res.r = 7;
                res.answer = APIJsonSt;
                return res;
            } */
            //endOf Google API
            res.question = question;
            res.answer = "Ответа на данный вопрос в моей базе нет, но вы можете связаться с оператором вписав \"Перейти на оператора\" ";
        }        
            
        
        return res;                       
    }    
    
               
    
    public QueryInfo getQueryInfo() throws Exception{        
        Stem[] stems = getStemsClean(queryStems);                                        
        for (int i=0;i<stems.length;i++) stems[i].idx = i;//Set new indexes for clean array to reduce indexes about twice                    
        double[] PIds = new double[P.size()], PIdsQA = new double[P.size()];
        boolean hasUnknownWords = false;
        
        List<Chunk> chunks = getChunks(stems);                    
        
        for (Chunk chunk : chunks) {                
            for (int i=0;i<PQA.size();i++) 
                for (Stem[] Pstems : PQA.get(i)){                        
                    SWNode tempNode = new SWNode();
                    tempNode.type = 1;tempNode.val_syntax = Pstems;
                    if (Qmatching(tempNode, chunk.stemsAsArray())){                                           
                        PIdsQA[i] = 1;
                        //System.out.println("PQAidx "+i+" - "+stemsToText(Pstems));
                        PIds[i]= 1;                                                                        
                        break;
                    }
                }                             
        }
                                        
        for (Stem s : stems) block2:{
           for (int i=0;i<P.size();i++) 
                for (Stem[] Pstems : P.get(i))
                    if (Pstems.length == 1 && s.hasEqLemmas(Pstems[0])) {
                        //if (PIds[i]!=1) System.out.println(i+" - "+stemsToText(Pstems));
                        System.out.println("Pidx "+i+" - "+stemsToText(Pstems));
                        PIds[i] = 1;                                   
                        break block2;
                    }                      
           if (s.text.trim().length() > 4) hasUnknownWords = true;//4 - random small number to avoid, a, b, bro, ok, etc
        }
        //System.out.println("hasUnknownWords->"+hasUnknownWords);
        return new QueryInfo(PIds, PIdsQA, hasUnknownWords);
    }
    
    void QRec(SWNode node, ArrayList<Chunk> chunks, int len) throws Exception {    
        boolean matchingFound = false;
        for (Chunk chnk : chunks)  
            if (Qmatching(node,chnk.stemsAsArray())) matchingFound = true;    
        if (!matchingFound) return;
        System.out.println(node.val+" = "+node.ansId);
        len++;
        if (len > QmaxLen) {QmaxLen = len;QNodes.clear();QNodes.add(node);}
        else if (len == QmaxLen) QNodes.add(node);
        for (SWNode child : node.children) QRec(child, chunks, len);        
    }
    
    boolean Qmatching(SWNode node, Stem[] a) throws Exception {
        //System.out.println(gson.toJson(node)+" "+gson.toJson(a));
        if (node.type == 1){                        
            for (Stem kwi : node.val_syntax) 
                if (kwi.existsInSentence(a) || kwi.existsInSentenceAsSyn(a)){} //  || kwi.existsInSentenceAsW2V(a) 
                else return false;                           
        } else if (node.type == 2){                                  
            RelationObject ro = ROMap.get(node.val);                            
            boolean matchingFound = false;                        
            for (Stem[] kw : ro.values_syntax) {                
                boolean found = true;
                for (Stem kwi : kw) if (!kwi.existsInSentence(a)) {found = false;break;}
                if (found) matchingFound = true;
            }
            if (!matchingFound) return false;                
        }
        return true;
    }
    
    ArrayList<Chunk> getChunks(Stem[] stemsGiven){
        ArrayList<Chunk> chunks = new ArrayList<Chunk>();
        Stem[] stems = getStemsNoSpace(stemsGiven);        
        Chunk chunk = null;        
        for (int i=0;i<stems.length;i++){
            Stem s = stems[i];
            if (g.indexOf(s.text.trim(), new String[]{".",",","?","!"})!=-1) continue;
            String pos = s.getPOS();  
            if (g.indexOf(pos, new String[]{"ADV","APRO","PR","PART","CONJ","ADVPRO"})!=-1){
                if (chunk == null || !chunk.phrase.equals("OP")){
                    chunk = new Chunk("OP");
                    chunks.add(chunk);
                }
                chunk.stems.add(s);
            } else if (g.indexOf(pos, new String[]{"A","ANUM","NUM","S","SPRO",""})!=-1){
                if (chunk!=null && chunk.phrase.equals("OP")) chunk.phrase = "NP";
                else if (chunk == null || !chunk.phrase.equals("NP") || !chunk.isRule(1, pos)) {
                    chunk = new Chunk("NP");                                    
                    chunks.add(chunk);
                }
                chunk.stems.add(s);                
            } else if (g.indexOf(pos, new String[]{"A","ANUM","NUM","S","SPRO",""})!=-1){
            
            } else if (s.isPOSeq("V")){
                if (chunk!=null && chunk.phrase.equals("OP")) chunk.phrase = "VP";
                else if (chunk == null || chunk.phrase.equals("VP")) {
                    chunk = new Chunk("VP");                                    
                    chunks.add(chunk);
                }
                chunk.stems.add(s);                
            } else chunk = null;
        }    
        return chunks;
    }    
    //endOf Closest Question  
    
    public String APISearch(String query){
        try{
            String question_query = g.encodeURL(query);
            String jsonSt = g.getURLContent("https://www.googleapis.com/customsearch/v1?key=AIzaSyAy5KOZpm8nKZ4twGqmBWWd4JS3Ryna_aY&cx=017305686966967909084:juxexb7mrw4&q="+question_query);
            //017305686966967909084:juxexb7mrw4 - anuararman2017@gmail.com 
            //001870476649241053389:m_rkzqohfyq - mega4alik@gmail.com
            
            List<GenericElement> elements = new ArrayList<GenericElement>();
            JsonElement jelement = new JsonParser().parse(jsonSt);
            JsonObject  jobject = jelement.getAsJsonObject();
            if (jobject.has("items")){
                JsonArray jarr = jobject.getAsJsonArray("items");
                for (int i=0;i<Math.min(jarr.size(),10);i++){
                     jobject = jarr.get(i).getAsJsonObject();
                     String title = jobject.get("title").getAsString();
                     String link = jobject.get("link").getAsString();
                     String snippet = jobject.get("snippet").getAsString();                                  
                     GenericElement el = new GenericElement();
                     el.title = title;
                     el.subtitle = snippet;
                     el.addWebButton(link, link.contains("userecho") ? "Открыть на форуме" : "Открыть на сайте");
                     elements.add(el);                 
                }            
                if (elements.size()>0){
                    elements = elements.subList(0, Math.min(elements.size(),10));                    
                    return gson.toJson(elements);                    
                } else return null;                
            }
        } catch(Exception e){
            //e.printStackTrace();
        }
        return null;
    }
    
    public String getQuestionBySID(String sid){
        return questions.get(qidx.get(sid));
    }
    
    
    String stemsToText(Stem[] stems){
        String ans = "";
        for (Stem s : stems) ans+=s.text+" ";
        return ans;
    }
            
    public Stem[] getStemsClean(Stem[] stems){        
        ArrayList<Stem> arr = new ArrayList<Stem>();
        for (Stem s : stems ){                        
            String text = s.text.trim();            
            if (text.length() == 0) continue;
            char ch = text.trim().toLowerCase().charAt(0);
            if (text.length() > 1) arr.add(s);
            else if ((ch>='0' && ch<='9') || (ch>='a' && ch<='z') || (ch>='а' && ch<='я') || ch == '-') arr.add(s);
        }
        return arr.toArray(new Stem[0]);        
    }
    
    public void getStemsCorrected(Stem[] stems){
        try{
            AutoCorrector corrector = new AutoCorrector();
            for (Stem s : stems ){                 
                String word = corrector.correct(s.text);                    
                    if (word!=null) {
                        System.out.println("corrected="+s.text+"-"+word);
                        s.text = word;                                                
                        if (s.analysis.size() > 0) s.analysis.get(0).lex = word.toLowerCase();                                                    
                }
               /*
               if (s.analysis==null || s.analysis.size()==0){
                    
                }
               */
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    Stem[] getStemsNoSpace(Stem[] stems){        
        ArrayList<Stem> arr = new ArrayList<Stem>();
        for (Stem s : stems ){            
            String text = s.text.trim();
            if (text.length() == 0) continue;      
            arr.add(s);
        }
        return arr.toArray(new Stem[0]);        
    }
    
    public Stem[] getStems(String text) throws Exception{        
        Stem[] stems = gson.fromJson(getSyntax(text),Stem[].class);
        for (int i=0;i<stems.length;i++) stems[i].idx = i;
        return stems;
    }
    
    String getDepParse(String text) throws Exception{
        text = text.replace("'","");        
        //String path = "/Users/Admin/Desktop/NLP/tensorflow/models/syntaxnet";
        //String command = "cat "+path+"/kkb_questions.txt | "+path+"/syntaxnet/models/parsey_universal/parse.sh "+path+"/syntaxnet/models/Russian-SynTagRus > output.conll";        
        String path = "/Users/Admin/Desktop/NLP/parser/";                        
        //Process p = Runtime.getRuntime().exec("sh "+path+"dependencyParse.sh");
        //p.waitFor(2, TimeUnit.DAYS);                       
        ProcessBuilder pb = new ProcessBuilder("sh","dependencyParse.sh");
        pb.directory(new File(path)); //"\\Users\\Admin\\Desktop\\NLP\\mystem\\"
        Process p = pb.start();                
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line = null; while ((line = reader.readLine()) != null) System.out.println(line);                        
        return null;
    }
    
    
    String getSyntax(String text) throws Exception {        
        text = text.replace("\"","");
        text = text.replace("\n"," ");
        Random rand = new Random(); int num = rand.nextInt(1000);
        printFile(text,num);
        String runResult = "";
        final Process p = Runtime.getRuntime().exec(g.Path+"mystem"+(g.OS==2 ? "_linux" : "")+" -cgi --eng-gr -e UTF8 --format json "+g.Path+"/input/"+num);
        //p.waitFor();
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;      
        try {
           while ((line = input.readLine()) != null){
               runResult+=line.replace("\\n","");
           }
        } catch (IOException e) {}        
        p.destroy();
        return runResult;
    }    
    
    public void ROMapUpdate(String jsonSt) throws Exception {
        Type tt = new TypeToken<HashMap<String, RelationObject>>(){}.getType();
        HashMap<String, RelationObject> ROMapNew = gson.fromJson(jsonSt, tt);                    
        for (Map.Entry<String, RelationObject> entry : ROMapNew.entrySet()) {
            String name = entry.getKey();
            RelationObject ro = entry.getValue();
            if (ROMap.containsKey(name) && Arrays.equals(ROMap.get(name).values, ro.values)) ro.values_syntax = ROMap.get(name).values_syntax; 
            else {
                System.out.println("ROMapUpdate getSyntax"+gson.toJson(ro.values));
                ro.values_syntax = new ArrayList<Stem[]>();
                for (String val : ro.values)  ro.values_syntax.add(getStemsClean(gson.fromJson(getSyntax(val), Stem[].class)));
            }            
        }     
        //ROMap = ROMapNew; //save but not load
        g.printToFile(g.Path+"/data/objects_data.txt",gson.toJson(ROMapNew));
    }
    
    public void GeneralDialogsUpdate(String jsonSt) throws Exception {
        GDNode[] GDListNew = gson.fromJson(jsonSt, GDNode[].class);        
        for (GDNode node : GDListNew) block1:{
            for (GDNode node2 : GDList)
                if (node.questions.equals(node2.questions)) {
                    node.questions_syntax = node2.questions_syntax;
                    break block1;
                }
            System.out.println("GeneralDialogsUpdate getSyntax "+gson.toJson(node.questions));
            node.questions_syntax = new ArrayList<Stem[]>();
            for (String val : node.questions)  node.questions_syntax.add(getStemsClean(gson.fromJson(getSyntax(val), Stem[].class)));
        }
        //GDList = GDListNew; //save but not load        
        g.printToFile(g.Path+"/data/general_dialog_data.txt",gson.toJson(GDListNew));
    }  
    
    public int QAUpdate(int id, String question, String[] tests, String answer, String fb_answer, String tg_answer) throws Exception{        
        Connection db = Conn.ConnectMain();        
        ArrayList<Stem[]> tests_syntax = new ArrayList<Stem[]>();                    
        tests_syntax.add(getStemsClean(getStems(question)));        
        for (String st : tests) {
            System.out.println("QAUpdate getSyntax - "+st);
            tests_syntax.add(getStemsClean(getStems(st)));
        }        
        if (id == -1) { //create new
            PreparedStatement pstmt = db.prepareStatement("INSERT INTO QA(question,tests,questions_syntax,answer,fb_answer,tg_answer) VALUES(?,?,?,?,?,?)", new String[]{"id"});
            pstmt.setString(1, question);
            pstmt.setString(2, gson.toJson(tests));
            pstmt.setString(3, gson.toJson(tests_syntax));
            pstmt.setString(4, answer);            
            pstmt.setString(5, fb_answer);
            pstmt.setString(6, tg_answer);
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) id = rs.getInt(1);                            
            pstmt.close();
        } else {
            PreparedStatement pstmt = db.prepareStatement("UPDATE QA SET question=?, tests=?, questions_syntax=?, answer=?, fb_answer=?, tg_answer=? WHERE id = ?");        
            pstmt.setString(1, question);
            pstmt.setString(2, gson.toJson(tests));
            pstmt.setString(3, gson.toJson(tests_syntax));
            pstmt.setString(4, answer);
            pstmt.setString(5, fb_answer);
            pstmt.setString(6, tg_answer);
            pstmt.setInt(7, id);                                
            pstmt.execute();
            pstmt.close();
        }        
        db.close();
        return id;
    }

    void printFile(String st, int num) throws Exception {
        PrintWriter out = new PrintWriter(new File(g.Path+"/input/"+num));
        st = st.replace("\\n", "");         
        out.print(st);
        out.close();
    }
    
    
    
    //OldVersion    
    public DNode getClosestQAV1(String question) throws Exception {                                                
        DNode res = new DNode();        
        try {
            System.out.println("QueryV1 = " + question);
            ArrayList<Candidate> cands = new ArrayList<Candidate>();                            
            queryStems = getStems(question);                        
            queryStems = getStemsClean(queryStems);
            getStemsCorrected(queryStems);
            ArrayList<Chunk> chunks = getChunks(queryStems);                                           
            for (int idx=0;idx<SWTree.length;idx++){
                    QmaxLen = 0;QNodes.clear();
                    QRec(SWTree[idx],chunks,0);
                    for (SWNode node : QNodes){
                        if (node.ansId!=null){                            
                            cands.add(new Candidate( (node.ansId.startsWith("_")  || node.ansId.startsWith("dt_")) ? answerToIdx(node.ansId) : qidx.get(g.toInt(node.ansId))));                            
                        }
                }
            }

            if (cands.size() > 0) {
                Candidate cand = cands.get(cands.size()-1);      
                System.out.println(cand.idx+". "+questions.get(cand.idx));
                String ans = answers.get(cand.idx);
                if (ans.startsWith("_MAP")){
                    res.r = 3;
                    res.question = ans;
                    res.answer = "Отправьте пожалуйста ваше местоположение:\n• Нажмите 📍 \n• Выберите 'Нынешнее местонахождение'\n• Нажмите 'Отправить'";                
                } else if (ans.startsWith("_CURRENCY")){
                    res.r = 5;                
                } else if (ans.startsWith("_QR")){
                    res.r = 6;                                
                    res.qrFamily = ans.replace("_QR","");                                
                } else if (ans.startsWith("_RATE") || ans.equals("_LANG-Select") || ans.startsWith("dt_")){
                    res.r = 8;
                    res.GraphNodeId = ans;
                } else if (ans.startsWith("_FOR") || ans.startsWith("_OPERATOR")){
                    res.r = 9;
                } else {
                    int index = cand.idx;
                    res.r = answer_types.get(index);
                    res.qid = ans;
                    res.question = questions.get(index);
                    res.answer = answers.get(index);
                    res.fb_answer = fb_answers.get(index);
                    res.tg_answer = tg_answers.get(index);
                }
            } else {             
                //General Dialog            
                int max = 0, min = 999;
                //Stem[] a = getStemsClean(queryStems);  
                for (int i = GDList.length-1;i>=0;i--){
                    GDNode node = GDList[i];
                    int size = GDExistsInSentence(node, queryStems);
                    if (size!=-1 && size > max){
                        res.r = 2;
                        res.question = question;
                        res.answer = node.answer;                        
                        max = size;
                        //min = size;
                    }
                }      
                if (res.r == 2){
                    if (res.answer.equals("_DATETIME")) res.answer = "Сегодня "+g.getCurrDateIn("yyyy-MM-dd");
                    else if (res.answer.equals("_TIME")) res.answer = g.getCurrDateIn("H:m:s");
                    return res;
                }                
                //endOf General Dialog                                                    

                res.question = question;
                res.answer = "Ответа на данный вопрос в моей базе нет, но вы можете связаться с оператором вписав \"Перейти на оператора\"";
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;                       
    }
    
    int answerToIdx(String answer){
        for (int i=0;i<answers.size();i++)
            if (answers.get(i).trim().equals(answer.trim())) return i;
        return -1;
    }
    
    int GDExistsInSentence(GDNode node, Stem[] a) throws Exception{ //-1 = does not exist, > 0 means size of GD question                 
        int ans = -1;
        for (Stem[] stems : node.questions_syntax){
            if (stems.length + 10 < a.length) continue;               
            boolean exists = true;
            for (Stem s : stems)
                if (!s.existsInSentence(a)) {exists = false;break;}
            if (exists && ans < stems.length)  ans = stems.length;
        }
        return ans;
    }
    //endOf OldVersion
    
    
    //dialogflow
    public DNode getClosestQADF(String question) throws Exception{
        DNode res = new DNode();
        DF df = new DF();        
        DFNode dfn = df.getResponse(question);
        if (dfn!=null){            
            if (dfn.intentName!=null) { 
                if (dfn.intentName.equals("operator_connect")) res.r = 9;
                else res.r = 1;
            } else if (dfn.action!=null && dfn.action.startsWith("smalltalk.")) res.r = 2;                        
            res.answer = dfn.speech;
        }
        return res;
    }
    //endOf dialogflow 
    
    
}
