package org.iis.ut.artificialplagiarismcreator.tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

public class PlagiarizedCaseExtractor {

	private static final String eol =  System.getProperty("line.separator");
	public static String corpus_dir = "RepairCorpus/ghoghnous2014_corpus/Ghoghnous_replace-words/";
	public static  String SOURCE_FILES_DIR = corpus_dir+"src/";
	public static  String SUSP_FILES_DIR = corpus_dir + "susp/";
	public static final String DETAILS_FILES_DIR = corpus_dir+"05-replace-words/";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File dir = new File(DETAILS_FILES_DIR);
		File [] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.startsWith(".") && name.endsWith(".xml");
			}
		});
		for(File file: files)
		{
			try {
				getCasesContent(DETAILS_FILES_DIR,file.getName());
			} catch (FactoryConfigurationError e) {
				e.printStackTrace();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	public static List<String> getCasesContent(String detailsFilePath, String detailsFileName) throws FactoryConfigurationError, XMLStreamException
	{
		List<String> contents = new ArrayList<String>();
		
		DetailsReader dreader = new DetailsReader();
		List<PlagiarismCase> cases = dreader.readDetailsFile(null,  new File(detailsFilePath+detailsFileName));
		
		for(PlagiarismCase pcase: cases)
		{
			String srcDoc = pcase.getFeature("source_reference");
			String suspDoc = pcase.getSuspDocunemt();
			
			String srcFileText = getMatn(new File(SOURCE_FILES_DIR+srcDoc));
			String suspFileText = getMatn(new File(SUSP_FILES_DIR+suspDoc));
			
			String srcText = srcFileText.substring(Integer.parseInt(pcase.getFeature("source_offset")),Integer.parseInt(pcase.getFeature("source_offset"))+ Integer.parseInt(pcase.getFeature("source_length")));
			String plagiarizedText = suspFileText.substring(Integer.parseInt(pcase.getFeature("this_offset")), Integer.parseInt(pcase.getFeature("this_offset"))+Integer.parseInt(pcase.getFeature("this_length")));
			pcase.setSourceText(srcText);
			pcase.setPlagiarizedText(plagiarizedText);
			
			System.out.println(pcase);
		}
		
		return contents;
		
	}
	
	
	
	public static String getMatn(File f) {
		String matn = "";
		try {

			FileInputStream fstream = new FileInputStream(f);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF8"));

			String line;
			while ((line = br.readLine()) != null) {
				matn = matn + line + eol;
			}

			br.close();
			in.close();
			fstream.close();
		} catch (Exception e) {
			System.err.println(e);
		}
		return matn;
	}
}
