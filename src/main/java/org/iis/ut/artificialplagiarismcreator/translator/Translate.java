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
public interface Translate {
    /**
     * implementation classes must determine first and second language and this function gives words form first language and return translate of that query in second language.
     * it returns null or empty list on the failure.
     * @param words
     * @return 
     */
    ArrayList<String> fromFirstToSecond(String words);
    /**
     * implementation classes must determine first and second language and this function gives words form second language and return translate of that query in first language.
     * it returns null or empty list on the failure.
     * @param words
     * @return 
     */
    ArrayList<String> fromSecondToFirst(String words);
}
