/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iis.ut.artificialplagiarismcreator.translator.impl;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iis.ut.artificialplagiarismcreator.translator.TranslateBurstInter;

/**
 *
 * @author Hossein
 */
public class GoogleTranslateEnFaBurst implements TranslateBurstInter{
    Semaphore semaphore,end;
    int windowSize=50;
    private ArrayList<ArrayList<String>> result;
    int counter=0;
    public GoogleTranslateEnFaBurst(int windowSize) {
        this.windowSize=windowSize;
    }
    
    public ArrayList<ArrayList<String>> fromFirstToSecond(ArrayList<String> words) {
        return sendAll(words, 1);
        
    }

    public ArrayList<ArrayList<String>> fromSecondToFirst(ArrayList<String> words) {
        return sendAll(words,-1);
    }

    private void submitQuery(String words, int i, int dir) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(GoogleTranslateEnFaBurst.class.getName()).log(Level.SEVERE, null, ex);
        }
        GoogleTranslateEnFa g = new GoogleTranslateEnFa();
        g.initialize(words, i, this, dir);
        g.start();
        
    }

    synchronized void submitResult(ArrayList<String> ans, int i) {
        counter--;
        result.set(i, ans);
        semaphore.release();
        if(counter%10==0){
            System.out.println(counter+" query left");
        }
        if(counter==0){
            end.release();
        }
    }

    private ArrayList<ArrayList<String>> sendAll(ArrayList<String> words, int dir) {
        result = new ArrayList<ArrayList<String>>();
        
//        System.err.println(result.size());
        semaphore= new Semaphore(windowSize);
        end=new Semaphore(0);
        counter=words.size();
        for(int i=0;i<words.size();i++){
            result.add(null);
            submitQuery(words.get(i),i,dir);
        }
        try {
            end.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(GoogleTranslateEnFaBurst.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
