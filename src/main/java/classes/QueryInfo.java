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
public class QueryInfo {
    public double[] PIds, PIdsQA;
    public boolean hasUnknownWords = false;
    public QueryInfo(double[] PIds, double[] PIdsQA, boolean hasUnknownWords){
        this.PIds = PIds;
        this.PIdsQA = PIdsQA;
        this.hasUnknownWords = hasUnknownWords;
    }
}
