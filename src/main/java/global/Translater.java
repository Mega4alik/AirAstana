/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.opencsv.CSVReader;

/**
 *
 * @author admin
 */


public class Translater {
    Global g = new Global();
    public static List<Translation> translations = new ArrayList<Translation>();
    static boolean initialized = false;
    
    public static void main(String arsgs[]) throws Exception{
        Translater t = new Translater();
        String st = t.getTranslation("_USER-Ask", "работа администрации", "eng");
        System.out.println(st);
    }
    
    public Translater(){     
        //importTranslations();
    }
    
    public void importTranslations(){
        try {
            if (initialized == true) return;
            System.out.println("Importing translations");
            initialized = true;
            String fileName = g.Path+"/files/translations.csv";
            CSVReader reader = new CSVReader(new FileReader(fileName ));
            // if the first line is the header
            String[] header = reader.readNext();
            // iterate over reader.readNext until it returns null        
            while (true){
                String[] line = reader.readNext();
                if (line == null) break;
                //System.out.println(Arrays.toString(line));
                Translation t = new Translation();           
                t.category = line[0];
                t.id = line[1];
                t.RU = line[2];
                t.KAZ = line[3];
                t.ENG = line[4];    
                translations.add(t);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public String getTranslation(String id, String RU, String toLang){        
        System.out.println("getTrans "+id+" "+RU+" "+toLang);
        if (toLang==null || toLang.equals("ru")) return RU;
        Translation ans = null;
        
        if (RU.length()>0)
            for (Translation t : translations) if (id!=null && t.id!=null && t.id.equals(id)) ans = t;
        
        if (ans==null) 
            for (Translation t : translations) 
                if (t.RU!=null && RU!=null && t.RU.toLowerCase().trim().equals(RU.toLowerCase().trim())) ans = t;
        
        if (ans!=null){
           if (toLang.equals("kaz")) return ans.KAZ;
           else if (toLang.equals("eng")) return ans.ENG;
        }
        
        return RU;
    }
    
}

class Translation{
    public String category, id, RU, KAZ, ENG;
    
}
