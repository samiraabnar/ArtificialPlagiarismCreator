/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iis.ut.artificialplagiarismcreator.translator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hossein
 */
public class TranslateBurst {

    Semaphore semaphore, end;
    private final Class<? extends Translate> translateClass;
    int paralelizem;
    ArrayList<ArrayList<String>> answer;

    public TranslateBurst(Class<? extends Translate> translateImple, int paralelizem) {
        this.paralelizem = paralelizem;
        end = new Semaphore(0);
        semaphore = new Semaphore(paralelizem);
        translateClass = translateImple;
        Class<?>[] interfaces = translateClass.getInterfaces();
        int found = 0;
        for (Class<?> inter : interfaces) {
            if (inter.equals(Translate.class)) {
                found++;
                break;
            }
        }
        if (found == 0) {
            throw new IllegalArgumentException("input class must impelements ir.ac.ut.iis.Translate");
        }
        try {
            translateClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(ex.toString());
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException(ex.toString());
        }

    }
    
    public void submit(ArrayList<String> ans ,int i){
        
    }
    

    public synchronized ArrayList<ArrayList<String>> AllFromFirstToSecond(ArrayList<String> words) {
        for (int i = 0; i < words.size(); i++) {
//            submitQuery(words.get(i));
        }
        try {
            end.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(TranslateBurst.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answer;
    }

    private void submitQuery(String words, Method method) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(TranslateBurst.class.getName()).log(Level.SEVERE, null, ex);
        }
        Translate translate;
        try {
            translate = translateClass.newInstance();
            method.invoke(translate, words);
        } catch (InstantiationException ex) {
            Logger.getLogger(TranslateBurst.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(TranslateBurst.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(TranslateBurst.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(TranslateBurst.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
