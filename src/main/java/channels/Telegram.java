/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package channels;

import classes.QRNode;
import com.google.gson.Gson;
import graphs.DGraphNode;
import java.util.Arrays;

/**
 *
 * @author admin
 */
public class Telegram {
   Gson gson = new Gson();
    public void toKeyboardButtons(DGraphNode dgn, int perLine){       
        try{
           if (dgn.quickReply!=null && dgn.quickReply.children!=null){
               int len = dgn.quickReply.children.size();
               String[][] keyboard = new String[len/perLine + (len%perLine)][perLine];
               int x = 0, y=0;
               keyboard[0] = new String[perLine];
               for (int i=0;i<len;i++){                            
                   keyboard[x][y] = dgn.quickReply.children.get(i).title;
                   y++;
                   if (y==perLine){
                       y=0;x++;
                       keyboard[x] = new String[Math.min(perLine,len-i-1)];
                   }
               }
               System.out.println("keyboard = "+gson.toJson(keyboard));
               String title = (dgn.message!=null ? dgn.message : "") + "\n" + (dgn.quickReply.title!=null ? dgn.quickReply.title : "");
               dgn.tg_answer = "{\"answer_type\":13, \"value\":{\"text\":\""+title+"\",\"addon\": {\"reply_markup\": {\"keyboard\": "+gson.toJson(keyboard)+" }}}}";
           }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
   
}
