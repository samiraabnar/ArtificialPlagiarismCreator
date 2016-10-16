/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iis.ut.artificialplagiarismcreator.translator;

import java.util.ArrayList;

/**
 *
 * @author Hossein
 */
public interface TranslateBurstInter {
    /**
     * implementation classes must determine first and second language and this function gives list of words form first language and return list of translate of that query in second language.
     * it returns null or empty list on the failure.
     * @param words
     * @return 
     */
    public ArrayList<ArrayList<String>> fromFirstToSecond(ArrayList<String> words);
    /**
     * implementation classes must determine first and second language and this function gives list of words form second language and return list of translate of that query in first language.
     * it returns null or empty list on the failure.
     * @param words
     * @return 
     */
    public ArrayList<ArrayList<String>> fromSecondToFirst(ArrayList<String> words);
}
