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
public class Candidate implements Comparable<Candidate>{    
    public double rank = 0;
    public int idx = 0;
    
    public Candidate(int idx){
        this.idx = idx;            
    }

    @Override
    public int compareTo(Candidate o) {
        if (this.rank < o.rank) return 1;
        return -1;
    }
    
    
}
