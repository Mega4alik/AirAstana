/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class QRFamily {
    public String title,ansId,parentId,parentTitle,lastsid = null;
    public List<QRNode> children = new ArrayList<QRNode>();    
    public QRFamily normalized(String sid) throws Exception{        
        QRFamily q = new QRFamily();
        q.title = this.title;
        q.ansId = this.ansId;        
        q.parentId = this.parentId;
        q.parentTitle = this.parentTitle;
        q.lastsid = sid;
        int idx = 0, size = this.children.size();
        if (sid!=null)
            for (int i=0;i<size;i++) 
                if (this.children.get(i).ansId.equals(sid)) idx = i;        
        int leftIdx = idx - 5, rightIdx = idx + 5;
        if (leftIdx < 0) {rightIdx+=0 - leftIdx; leftIdx=0;}
        else if (rightIdx > (size-1)) {leftIdx-=rightIdx - (size-1); rightIdx = (size-1);}
        q.children = this.children.subList(Math.max(leftIdx,0), Math.min(rightIdx, size-1)+1);               
        
        return q;
    }
}
