/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iis.ut.artificialplagiarismcreator.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author mosi
 */
public class ExtractInfo {

	private static final String Ghoghnous2014 = "/Users/MacBookPro/Documents/Uni-MS/FinalProject/Code/PlagiarismDetection/evaluations/Ghoghnous2014/corpus/";
	private static final String GhoghnousDir = "/Users/MacBookPro/Documents/Uni-MS/FinalProject/Code/PlagiarismDetection/evaluations/Ghoghnous2014-jan/Ghoghnous14Jan-plagiarism-text-aliagnment-training-corpus/";

	private static final String PAN2013Dir = "/Users/Sam/Education/MyMasterThesis/Codes/evaluations/PAN2013/pan13-text-alignment-training-corpus-2013-01-21/";

	public static String CORPUS_DIR = "../evaluations/Ghoghnous2014/corpus/";//PAN2013Dir;// "/Users/MacBookPro/Documents/Uni-MS/FinalProject/Code/artificial_plagiarism/dataset-v3/";

	public static void getStat(String dirPath)
			throws ParserConfigurationException, SAXException, IOException {

		// String dirPath =
		// "/home/mosi/NetBeansProjects/Samira/Evaluation/Ghoghnous2013/Ghoghnous13-plagiarism-text-aliagnment-training-corpus/02-no-obfuscation/";
		File dir = new File(dirPath);
               if( dir.exists())
                   System.out.println("exist!");
		File[] files = dir.listFiles(new FilenameFilter() {

                    public boolean accept(File dir, String name) {  
                        return name.startsWith("susp");
                   }
                });
		Integer[] caseNo = new Integer[files.length];
		Integer[] srcCaseLengthAvg = new Integer[files.length];
		Integer[] suspCaseLengthAvg = new Integer[files.length];
		String[] srcName = new String[files.length];
		String[] suspName = new String[files.length];
		ArrayList<Integer> srcCaseLength = new ArrayList<Integer>();
		ArrayList<Integer> suspCaseLength = new ArrayList<Integer>();
		for (int k = 0; k < files.length; k++) {
			System.out.println(k + ":" + files[k].getName());
			int cNo = 0;
			int srcCLens = 0;
			int suspCLens = 0;
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(files[k]);
				doc.getDocumentElement().normalize();
				System.out.println("---------------------");
				// System.out.println(doc.getDocumentElement().getAttribute("reference"));
				suspName[k] = doc.getDocumentElement()
						.getAttribute("reference");
				NodeList nList = doc.getElementsByTagName("feature");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						cNo++;
						int tmpSrcCLens = Integer.parseInt(eElement
								.getAttribute("source_length"));
						int tmpSuspCLens = Integer.parseInt(eElement
								.getAttribute("this_length"));
						srcCLens += tmpSrcCLens;
						suspCLens += tmpSuspCLens;
						srcCaseLength.add(tmpSrcCLens);
						suspCaseLength.add(tmpSuspCLens);

						srcName[k] = eElement.getAttribute("source_reference");

						// System.out.println("name: " +
						// eElement.getAttribute("name"));
						// System.out.println("source_length: " +
						// eElement.getAttribute("source_length"));
						// System.out.println("source_offset: " +
						// eElement.getAttribute("source_offset"));
						// System.out.println("source_reference: " +
						// eElement.getAttribute("source_reference"));
						// System.out.println("this_length: " +
						// eElement.getAttribute("this_length"));
						// System.out.println("this_offset: " +
						// eElement.getAttribute("this_offset"));
					}
				}
				caseNo[k] = cNo;
				srcCaseLengthAvg[k] = srcCLens / cNo;
				suspCaseLengthAvg[k] = suspCLens / cNo;
                                
                                
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String[] dname = dirPath.split("/");
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				"statistics_caselength_" + dname[(dname.length) - 1] + ".csv")));
		bw.write("srcLength,suspLength\n");
		for (int i = 0; i < srcCaseLength.size(); i++) {
			bw.write(srcCaseLength.get(i) + "," + suspCaseLength.get(i) + "\n");
		}
		bw.close();

		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(
				CORPUS_DIR + "statistics/" + "statistics_pairsInfo_"
						+ dname[(dname.length) - 1] + ".csv")));
		bw1.write("srcName,suspName,caseNo,srcCaseLengthAvg,suspCaseLengthAvg\n");
		for (int i = 0; i < files.length; i++) {
                    if(  (suspCaseLengthAvg[i] != null)&& (suspCaseLengthAvg[i] > 0))
			bw1.write(srcName[i] + "," + suspName[i] + "," + caseNo[i] + ","
					+ srcCaseLengthAvg[i] + "," + suspCaseLengthAvg[i] + "\n");
		}
		bw1.close();
	}

	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException {
		String dirPath = "../evaluations/Ghoghnous2014/corpus/02-no-obfuscation/";//CORPUS_DIR + "05-summary-obfuscation/";// "Evaluation/PAN2013/pan13-plagiarism-text-aliagnment-training-corpus-2013-01-21/02-no-obfuscation/";
		getStat(dirPath);
                
                dirPath = "../evaluations/Ghoghnous2014/corpus/03-shuffle-sentences/";//CORPUS_DIR + "05-summary-obfuscation/";// "Evaluation/PAN2013/pan13-plagiarism-text-aliagnment-training-corpus-2013-01-21/02-no-obfuscation/";
		getStat(dirPath);
                
                dirPath = "../evaluations/Ghoghnous2014/corpus/04-circular-translation/";//CORPUS_DIR + "05-summary-obfuscation/";// "Evaluation/PAN2013/pan13-plagiarism-text-aliagnment-training-corpus-2013-01-21/02-no-obfuscation/";
		getStat(dirPath);
                
                dirPath = "../evaluations/Ghoghnous2014/corpus/05-replace-words/";//CORPUS_DIR + "05-summary-obfuscation/";// "Evaluation/PAN2013/pan13-plagiarism-text-aliagnment-training-corpus-2013-01-21/02-no-obfuscation/";
		getStat(dirPath);
                
	}
}
