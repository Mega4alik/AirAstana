/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphs;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author admin
 */
public class DGraphRespOption {
    @SerializedName("@dgnId")  
    public String dgnId;
    
    public int action = 1;//1 - goToNode, 0 - doNothing
        
    public String[] values;   
    
    public DGraphRespOption(String dgnId, String[] values){
        this.dgnId = dgnId;
        this.values = values;
    }
}
