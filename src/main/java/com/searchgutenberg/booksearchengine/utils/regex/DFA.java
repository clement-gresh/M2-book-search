package com.searchgutenberg.booksearchengine.utils.regex;

import java.util.HashSet;

public class DFA {
    /** ASCII transitionTable*/
    protected int[][] dfaTransitions;
    /**IMPLICIT REPRESENTATION HERE: INIT STATE IS ALWAYS 0,
     * So we didn't  use a HashSet to store the init state*/

    /**Set for storing the final states */
    protected HashSet<Integer> setFinal;

    public DFA(int[][] dfaTransitions,HashSet setFinal) {
        this.dfaTransitions = dfaTransitions;
        this.setFinal = setFinal;
    }

    public HashSet<Integer> getSetFinal() {
        return setFinal;
    }


    public int[][] getTransition(){
        return  dfaTransitions;
    }

    public String toString() {

        HashSet<Integer> etatFinal = getSetFinal();
        String result = "\n";
        for (int i=0;i<dfaTransitions.length;i++) for (int col=0;col<256;col++)
            if (dfaTransitions[i][col]!=-1) result+="  "+i+" -- "+(char)col+" --> "+dfaTransitions[i][col]+"\n";
        result += "\n  Initial state: 0";
        result += "\n  Final State:";
        for(int i: etatFinal){
            result += i +" ";
        }
        return result;
    }
}
