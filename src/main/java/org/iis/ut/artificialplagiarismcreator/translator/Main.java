/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iis.ut.artificialplagiarismcreator.translator;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.iis.ut.artificialplagiarismcreator.translator.impl.GoogleTranslateEnFaBurst;

/**
 *
 * @author Hossein
 */
public class Main {

	public static void main(String[] args) throws UnsupportedEncodingException {

		/**
		 * determine implementation of the Translate interface
		 */
		GoogleTranslateEnFaBurst g = new GoogleTranslateEnFaBurst(50);
		ArrayList<String> input_En = new ArrayList<String>();
		input_En.add("Hello Mostafa");
		input_En.add("Are you Ok?");
		ArrayList<String> input_Fa = new ArrayList<String>();
		input_Fa.add("سلام سمیرا");
		ArrayList<ArrayList<String>> output_Fa = g.fromFirstToSecond(input_En);
		ArrayList<ArrayList<String>> output_En = g.fromSecondToFirst(input_Fa);
		for (int i = 0; i < output_Fa.size(); i++) {
			for (int j = 0; j < output_Fa.get(i).size(); j++) {
				System.out.println("Farsi:  " + output_Fa.get(i).get(j));
			}
		}
		System.err.println("--------------------------------------");
		for (int i = 0; i < output_En.size(); i++) {
			for (int j = 0; j < output_En.get(i).size(); j++) {
				System.out.println("English:  " + output_En.get(i).get(j));
			}
		}

		/*
		 * FNSynsetService fnSynsetService = new FNSynsetService();
		 * 
		 * Vector<FNSynset> words = fnSynsetService.FindSynsetsByWord("مقبره");
		 * Vector<FNSynset> words3 = fnSynsetService.FindSynsetsByWord("پدر");
		 * 
		 * for(FNSynset word: words) { System.out.println(word.getGloss()); }
		 */

		/*
		 * // FNWordDao fnword = new FNWordDao(); // Vector<FNWord> words2 =
		 * fnword.getWords(); FNSynsetDao fnSynsetDao = new FNSynsetDao();
		 * Vector<FNSynset> words2 =
		 * fnSynsetDao.findAllSynsetsBySemanticCategory("پدر");
		 */}
}
