/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

/**
 *
 * @author admin
 */
public class QRNode {
    public String title,ansId;
    int type=1;//1-qid,2-reply
    public QRNode(String title,String ansId,int type){
        this.title = title;
        this.ansId = ansId;
        this.type = type;
    }
}
