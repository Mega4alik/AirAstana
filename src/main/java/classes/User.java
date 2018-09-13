/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author admin
 */
public class User{
    public int id;
    public String name="", fb_id;
    public Date lastMovedToOperator = null;
    public ArrayList<UserRate> rates = null;
}

