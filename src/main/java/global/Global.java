/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;

import com.vdurmont.emoji.EmojiParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

public class Global {
            
    public static final int LangDefault = 1;//1-RUS, 2 - EN, 
    public static final int QAML_version = 9;//1 - old, 2 - new, 9 - dialogflow 
    
    public static final int run_mode = 1;//1-localhost, 2-production
    
    public static int OS = 1;//local 1-osx, 2 - local linux
    public String Path = "/Users/Admin/Documents/Projects/AIRA/",
    nodeJSURL = "http://localhost:3401",
    mysqlDB="ailabs_aira", mysqlUser="root", mysqlPassword="220693",
    fbToken = "EAAbK7dGSQ34BAFaBTKqBYVi28kBjNMnksZA4eRrXZAMmLiND3vlwk9kHXbK6QFs8EymvgcFy5vk8Rhy59b05Id1XQ7k79k5wIMAjZAo7ZBRM058C8EPnHZBzQf1Pz3U0zHg9JrD90Vy9ulUYVwCLHulj1bVmPAUqtUS6VVkdexQZDZD";//Dake Bot
    
    /*
    public String Path = "/home/raushan/work/bot/AICC_FILES/",
    nodeJSURL = "http://localhost:3201",
    mysqlDB="ailabs_aicc", mysqlUser="root", mysqlPassword="12345",
    fbToken = "EAAbK7dGSQ34BAFaBTKqBYVi28kBjNMnksZA4eRrXZAMmLiND3vlwk9kHXbK6QFs8EymvgcFy5vk8Rhy59b05Id1XQ7k79k5wIMAjZAo7ZBRM058C8EPnHZBzQf1Pz3U0zHg9JrD90Vy9ulUYVwCLHulj1bVmPAUqtUS6VVkdexQZDZD";//Dake Bot        
    */
    
    {
        if (run_mode == 2){//production
            Path = "/home/ubuntu/ai/AICC_FILES/";
            nodeJSURL = "http://104.196.181.22:3201";             
            fbToken = "EAAbK7dGSQ34BAFaBTKqBYVi28kBjNMnksZA4eRrXZAMmLiND3vlwk9kHXbK6QFs8EymvgcFy5vk8Rhy59b05Id1XQ7k79k5wIMAjZAo7ZBRM058C8EPnHZBzQf1Pz3U0zHg9JrD90Vy9ulUYVwCLHulj1bVmPAUqtUS6VVkdexQZDZD";//Dake Bot
            //fbToken = "EAAWkji7QfbsBAKfnszePq1ZCBiewzq3lIy9OPOyPYT3N3kFntwFoXDQlXXwnWQNQuJaLzZB11QHCl1zpohPT7ZAwcHgywZA18X3T2x096z7eoLCmKeApVu3aIbsoogm6qZC79wob3vjdvPC6kB7EMmI2aUH22qQVHvNh1hiQmZBGMnpGBSY3se";//AILabsDemoBot    
        }
    }
    
        
    HttpServletRequest request;        
    
    
    public Global(){
        
    }   
    
    public String getSt(String st){ 
        try{
            return request.getParameter(st);
        } catch(Exception e){}
        return "";
    }
    
    public Double getDouble(String st){
        try{
            return Double.parseDouble(request.getParameter(st));
        } catch(Exception e){}
        return null;
    }    
    
    public int getInt(String st){
        try{
            return Integer.parseInt(request.getParameter(st));
        } catch(Exception e){}
        return 0;
    }    
    
    public String[] StringLineSplit(String st){
        if (st!=null) {            
            List<String> list = Arrays.asList(st.split("\n"));            
            List<String> ans = new ArrayList<String>();
            for (String str : list){
                str = str.trim();
                if (!(str == null || str.equals("\n") || str.length() == 0)) ans.add(str);
            }
            return ans.toArray(new String[0]);
        }
        return new String[0];
    }
    
    
    
    public int toInt(String st){
        return Integer.parseInt(st);
    }
    public boolean isNumber(String st){
        try{
            Integer.parseInt(st);
            return true;
        } catch(Exception e){}
        return false;
    }
    
    public int indexOf(String st, String[] a){
        for (int i=0;i<a.length;i++)
            if (a[i].equals(st)) 
                return i;
        return -1;
    }
    
    public int indexOfInt(int k, int[] a){
        for (int i=0;i<a.length;i++)
            if (a[i] == k) 
                return i;
        return -1;
    }
    
    public void setRequest(HttpServletRequest request){
        this.request = request;
    }
    
    public String getFileContent(String path) throws Exception{
        StringBuffer st = new StringBuffer("");
        BufferedReader in = new BufferedReader(new FileReader(path));
        while(in.ready()) st.append(in.readLine());
        in.close();
        return st.toString();
    }
    
    public String[] getFileContentAsArray(String path) throws Exception{
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new FileReader(path));                
        while(in.ready()){
            String st = in.readLine().trim();
            if (st.isEmpty()) continue;
            list.add(st);
        }
        in.close();        
        
        String[] array = new String[list.size()];
        return list.toArray(array);        
    }
    
    public String getURLContent(String urlSt){
        try{
            URL url = new URL(urlSt);                
            InputStream is = url.openConnection().getInputStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );
            String line = null,pageText="";
            while( ( line = reader.readLine() ) != null )  {
               pageText+=line;
            }
            reader.close();
            return pageText;
        } catch(Exception e){}        
        return null;
    }
    
  
        
    public String getCurrDateIn(String formatSt){
        DateFormat dateFormat = new SimpleDateFormat(formatSt);
        Calendar cal = Calendar.getInstance();         
        return dateFormat.format(cal.getTime());
    }
    
    public String getCurrDateInStandard(){
        String formatSt = "yyyy-MM-dd HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(formatSt);
        Calendar cal = Calendar.getInstance();         
        return dateFormat.format(cal.getTime());
    }
    
    
    public Date getCurrDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();         
        return cal.getTime();
    }
    
    public long getDateDiff(Date d1, Date d2){
        if (d2 == null || d1 == null) return Integer.MAX_VALUE;        
        return (d1.getTime() - d2.getTime()) / 1000;        
    }
    
    public String encodeURL(String st){
        try {
            return URLEncoder.encode(st, "UTF-8");            
        } catch(Exception e){}
        return st.replace(" ","+");
    }
    
    public String encodeReqParam(String st){
        //URLEncoder.encode(st, "UTF-8");
        return st.replace("\"", "\\\"");
    }
    
    public String UnicodeToUTF8(String myString){
        try {
            String str = myString.split(" ")[0];
            str = str.replace("\\","");
            String[] arr = str.split("u");
            String text = "";
            for(int i = 1; i < arr.length; i++){
                int hexVal = Integer.parseInt(arr[i], 16);
                text += (char)hexVal;
            }        
            return text;
        } catch(Exception e){
            return myString;
        }
    }
    public String ISOToUTF8(String myString){
        try {
            return new String(myString.getBytes("ISO-8859-1"), "UTF-8");
        } catch(Exception e){
            return myString;    
        }
    }
    
    public String removeEmojis(String st){
        try {
            //st = EmojiParser.removeAllEmojis(st);
            //System.out.println("emojiRemove - "+st);
            //return st;
            //return st.replaceAll("\\p{So}+", "");
        } catch(Exception e){            
        }
        return st;
    }
    
    public void printToFile(String path, String st) throws Exception{
        PrintWriter out = new PrintWriter(new File(path));
        out.println(st);
        out.close();
    }
    
    
    public double pointsDistance(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371; // Radius of the earth
        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters        
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }
    
    public String distanceWithMetric(double lat1, double lat2, double lng1, double lng2){
        String metric = "м";
        double dist = pointsDistance(lat1,lat2,lng1,lng2);
        if (dist >= 1000) {
            dist/=1000.0;
            metric = "км";
            NumberFormat nf = NumberFormat.getInstance(); 
            nf.setMaximumFractionDigits(2);
            String s = nf.format(dist);
            return s+" "+metric;
        }
        
        return (int)dist+" "+metric;
    }    
    
    public int getRandomNumber(int max){
        Random rand = new Random();        
        return rand.nextInt(max) + 1;
    }
    
    
}

