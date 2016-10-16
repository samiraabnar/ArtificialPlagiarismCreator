/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iis.ut.artificialplagiarismcreator.translator.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iis.ut.artificialplagiarismcreator.translator.Translate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 *
 * @author Hossein
 */
public class GoogleTranslateEnFa extends Thread implements Translate {

	private int emergent = 0;
	String words;
	int i, dir;
	GoogleTranslateEnFaBurst dad;

	public void initialize(String words, int i, GoogleTranslateEnFaBurst dad,
			int dir) {
		this.words = words;
		this.i = i;
		this.dir = dir;
		this.dad = dad;
	}

	@Override
	public void run() {
		boolean done = false;
		try {
			if (dir > 0) {
				dad.submitResult(fromFirstToSecond(words), i);
				done = true;
			} else {
				dad.submitResult(fromSecondToFirst(words), i);
				done = true;
			}
		} finally {
			if (!done) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
						"Exception in these words : " + words);
				dad.submitResult(new ArrayList<String>(), i);
			}
		}

	}

	public ArrayList<String> fromFirstToSecond(String words) {
		Logger logger = Logger.getLogger(this.getClass().getName());
		logger.setLevel(Level.FINE);
		if (words == null || words.equals("")) {
			System.out.println("You should input some word!");
			logger.warning("You should input some word!");
			ArrayList<String> arrayList = new ArrayList<String>();
			arrayList.add("");
			return arrayList;
		}
		String words2 = null;
		try {

			words2 = URLEncoder.encode(words, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			logger.severe("ENC ERROR");
		}
		// preparing string for url. spaces are replaced with %20
		String url = "http://translate.google.com/translate_a/t?client=t&hl=en&sl=en&tl=fa&ie=UTF-8&oe=UTF-8&multires=1&ssel=0&tsel=0&sc=1&text="
				+ words2;
		// String url =
		// "http://baranak.com/translator.php?lang1=en&lang2=fa&txt=" + words2;
		// logger.info("Fetching " + url + " ...");
		String result = null;
		while (true) {
			try {
				Document doc = Jsoup
						.connect(url)
						.header("Cache-Control", "no-cache")
						.header("Pragma", "no-cache")
						.header("User-Agent",
								" Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.91 Safari/537.11")
						.header("Accept", "*/*")
						.header("X-Chrome-Variations",
								"CNi1yQEIlLbJAQiZtskBCKO2yQEIp7bJAQiqtskBCL22yQEIt4PKAQ==")
						.header("Referer", "http://translate.google.com/")
						.header("Accept-Encoding", "gzip,deflate,sdch")
						.header("Accept-Language", "en-US,en;q=0.8")
						.header("Accept-Charset",
								"ISO-8859-1,utf-8;q=0.7,*;q=0.3").get();

				result = doc.text();

				if (result == null || result.equals("")) {
					result = words;
				} else {
					// GoogleTranslate g= new GoogleTranslate();
					result = parse(result).getMainTranslation();
				}
				break;
			} catch (Exception ex) {

				if (ex.getMessage().contains("403")) {
					if (emergent == 1) {
						result = "";
						break;
					}
					emergent = 1;
					String[] split = words.split(" ");
					result = "";
					for (int i = 0; i < split.length; i++) {
						result = result + " "
								+ fromFirstToSecond(split[i]).get(0);
					}
					break;
				}
				logger.severe(ex.getMessage());
			}
		}
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(result);
		return arrayList;
	}

	public ArrayList<String> fromSecondToFirst(String words) {
		Logger logger = Logger.getLogger(this.getClass().getName());
		logger.setLevel(Level.FINE);
		if (words == null) {
			System.out.println("You should input some word!");
			logger.warning("You should input some word!");
			return null;
		}
		String words2 = null;
		try {

			words2 = URLEncoder.encode(words, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			logger.severe("ENC ERROR");
		}
		String url = "http://translate.google.com/translate_a/t?client=t&hl=en&sl=fa&tl=en&ie=UTF-8&oe=UTF-8&multires=1&trs=1&inputm=1&vkb=1&inputm=2&prev=btn&ssel=5&tsel=5&sc=1&text="
				+ words2;
		// logger.info("Fetching " + url + " ...");
		String result = null;

		while (true) {
			try {
				Document doc = Jsoup
						.connect(url)
						.header("Cache-Control", "no-cache")
						.header("Pragma", "no-cache")
						.header("User-Agent",
								" Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.91 Safari/537.11")
						.header("Accept", "*/*")
						.header("X-Chrome-Variations",
								"CNi1yQEIlLbJAQiZtskBCKO2yQEIp7bJAQiqtskBCL22yQEIt4PKAQ==")
						.header("Referer", "http://translate.google.com/")
						.header("Accept-Encoding", "gzip,deflate,sdch")
						.header("Accept-Language", "en-US,en;q=0.8")
						.header("Accept-Charset",
								"ISO-8859-1,utf-8;q=0.7,*;q=0.3").get();
				result = doc.text();

				if (result == null || result.equals("")) {
					result = words;
				} else {
					// GoogleTranslate g= new GoogleTranslate();
					result = parse(result).getMainTranslation();
				}
				break;
			} catch (Exception ex) {

				if (ex.getMessage().contains("403")) {
					if (emergent == 1) {
						result = "";
						break;
					}
					emergent = 1;
					String[] split = words.split(" ");
					for (int i = 0; i < split.length; i++) {
						result = result + " "
								+ fromSecondToFirst(split[i]).get(0);
					}
					break;
				}
				logger.severe(ex.getMessage());
			}
		}
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(result);
		return arrayList;
	}

	public GoogleTranslationObject parse(String sb) throws Exception {
		GoogleTranslationObject goo = new GoogleTranslationObject();
		if (sb.charAt(0) == '[') {
			sb = removeFLBarac(sb);
		}
		String[] splited = splitWithBarac(sb, 2);
		if (splited.length < 1) {
			return goo;
		}
		if (splited.length >= 1) {
			goo.mainTranslation = findMainTranslates(splited[0]);
		}
		if (splited.length > 1) {
			goo.otherTranslation = findOtherTranslation(splited[1]);
		}
		return goo;
	}

	private String removeFLBarac(String sb) {
		if (sb.length() >= 2) {
			return sb.substring(1, sb.length() - 1);
		}
		return "";
	}

	private String[] splitWithBarac(String sb, int max) {
		int depth = 0;
		ArrayList<String> ans = new ArrayList<String>();
		int begin = 0;
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '[') {
				if (depth == 0) {
					// begin = i;
				}
				depth++;
			}
			if (sb.charAt(i) == ']') {
				depth--;
			}

			if (sb.charAt(i) == ',' && depth == 0) {
				ans.add(sb.substring(begin, i));
				begin = i + 1;
				max--;
				if (max < 0) {
					break;
				}
			}
		}
		if (depth == 0) {
			ans.add(sb.substring(begin, sb.length()));
		}
		return ans.toArray(new String[0]);

	}

	private String findMainTranslates(String s) {
		s = removeFLBarac(s);
		s = removeFLBarac(s);

		String[] split = s.split(",");
		return split[0].substring(1, split[0].length() - 1);

	}

	private Map<String, ArrayList<RatedTranslation>> findOtherTranslation(
			String s) throws Exception {
		Map<String, ArrayList<RatedTranslation>> ans = new HashMap<String, ArrayList<RatedTranslation>>();
		s = removeFLBarac(s);
		String[] trans = splitWithBarac(s, 100);
		for (String tran : trans) {
			try {
				tran = removeFLBarac(tran);
				String[] catagoryComponent = splitWithBarac(tran, 100);
				if (catagoryComponent.length < 3) {
					continue;
				}
				String catagory = catagoryComponent[0].substring(1,
						catagoryComponent[0].length() - 1);
				String translationsInThisCategoryStr = removeFLBarac(catagoryComponent[1]);
				String[] translationsInThisCategory = translationsInThisCategoryStr
						.split(",");
				String transWithRatesStr = removeFLBarac(catagoryComponent[2]);
				String[] transWithRates = splitWithBarac(transWithRatesStr, 100);
				ArrayList<RatedTranslation> catAns = new ArrayList<RatedTranslation>();
				for (String transWithRate : transWithRates) {
					try {
						transWithRate = removeFLBarac(transWithRate);
						String[] bargs = splitWithBarac(transWithRate, 10);
						if (bargs.length == 4)
							catAns.add(new RatedTranslation(bargs[0].substring(
									1, bargs[0].length() - 1), Double
									.parseDouble(bargs[3])));
						else
							catAns.add(new RatedTranslation(bargs[0].substring(
									1, bargs[0].length() - 1), -1));

					} catch (Exception ex) {
						throw ex;
					}
				}
				ans.put(catagory, catAns);
			} catch (Exception ex) {
				throw ex;
			}

		}
		return ans;

	}

	public class RatedTranslation implements Comparable<RatedTranslation> {

		String word;
		double rate;

		public RatedTranslation(String word, double rate) {
			this.word = word;
			this.rate = rate;
		}

		public int compareTo(RatedTranslation o) {
			return Double.compare(rate, o.rate);
		}
	}

	public class GoogleTranslationObject {

		String mainTranslation;
		Map<String, ArrayList<RatedTranslation>> otherTranslation;

		public String getMainTranslation() {
			return mainTranslation;
		}

		public Map<String, ArrayList<RatedTranslation>> getOtherTranslation() {
			return otherTranslation;
		}

		public GoogleTranslationObject() {
			otherTranslation = new HashMap<String, ArrayList<RatedTranslation>>();
		}
	}

}
