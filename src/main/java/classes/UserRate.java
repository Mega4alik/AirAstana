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
public class UserRate{
    public int rate;
    public String feedback="", time;    
    public UserRate(int rate, String feedback, String time){
        this.rate = rate;
        this.feedback = feedback;
        this.time = time;
    }
}
