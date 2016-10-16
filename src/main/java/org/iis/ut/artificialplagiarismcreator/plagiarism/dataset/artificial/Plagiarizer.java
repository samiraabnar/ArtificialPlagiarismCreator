package org.iis.ut.artificialplagiarismcreator.plagiarism.dataset.artificial;

import java.util.ArrayList;

import org.iis.ut.artificialplagiarismcreator.translator.impl.GoogleTranslateEnFaBurst;

public class Plagiarizer {

	public String plagiarize(String paragraph) {
		String plagiarizedParagraph = "";

		return plagiarizedParagraph;

	}

	public String sentenceReplacement(String sentence) {
		GoogleTranslateEnFaBurst g = new GoogleTranslateEnFaBurst(50);
		ArrayList<String> input_sentence = new ArrayList<String>();
		input_sentence.add(sentence);
		ArrayList<ArrayList<String>> translated = g
				.fromFirstToSecond(input_sentence);
		ArrayList<ArrayList<String>> reTranslated = g
				.fromSecondToFirst(translated.get(0));
		return reTranslated.get(0).get(0);
	}

}
