/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iis.ut.artificialplagiarismcreator.plagiarism.dataset.artificial;

/**
 *
 * @author Samira
 * 
 */
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.iis.postagger.HunPOSTagger;
import org.iis.postagger.TaggedWord;
import org.iis.ut.STEPOne.stemmer.Morphology;
import org.iis.ut.STEPOne.stemmer.Stemmer;
import org.iis.ut.artificialplagiarismcreator.tools.RandomGaussian;
import org.iis.ut.artificialplagiarismcreator.translator.Translation;
import org.iis.ut.artificialplagiarismcreator.translator.impl.GoogleTranslateEnFaBurst;
import org.xml.sax.SAXException;

import com.sun.tools.javac.util.Pair;
//import opennlp.tools.util.model.GenericModelSerializer;
//import opennlp.tools.util.model.BaseModel;

public class PlagiarizedTextGenerator {

	public final static String[] OBFUSCATION_STRATEGY = { "NO_OBFUSCATION",
			"CIRCULAR_TRANSLATION", "REPLACE_WORDS", "SHUFFLE_SENTENCES",
			"COMBINATION" };
	private static String[] type_of_plagiarismPrefix = { "02", "04", "05",
			"03", "06" };

	public final static String[] type_of_plagiarism = { "no-obfuscation",
			"circular-translation", "replace-words", "shuffle-sentences",
			"combination","no-plagiarism" };
	public static Integer obfuscation_type_index = 2;
	public static FarsnetUtility farsnetUtility;
	public static Stemmer stemmer;
	public static String DATASET_MAIN_DIR = "corpus/";
	public static String dataset_suspcandid_foldername = DATASET_MAIN_DIR
			+ "norm_susp_candid/";
	public static String dataset_srcfoldername = DATASET_MAIN_DIR + "src/";
	public static String dataset_suspdetailsfoldername = DATASET_MAIN_DIR
			+ type_of_plagiarismPrefix[obfuscation_type_index] + "-"
			+ type_of_plagiarism[obfuscation_type_index] + "/";
	public static String dataset_suspfoldername = DATASET_MAIN_DIR
			+ "susp/susp-" + type_of_plagiarism[obfuscation_type_index] + "/";

	public final static Integer CASE_NORMAL_DIST_MEAN = 538;
	public final static Integer CASE_NORMAL_DIST_STDEV = 245;
	protected static String pairsFileName = dataset_suspdetailsfoldername
			+ "pairs";
	private final static String SimilarityFilePath = "FindSimilarSrcFiles/res.simple_kl_jm_2";
	// khorooji e lemur

	private final static String eol = System.getProperty("line.separator");
	private static final Integer NUMBER_OF_REPORTED_SILIMARITIES_PER_FILE = 3;
	Integer randNumberOfFiles = 0;
	Integer randFileNumber = 0;
	Integer randNumberOfPlagiarismCases = 0;
	Random random = new Random();

	static DataInputStream in = null;
	static BufferedReader br;
	// FarsiDicAPI translator = new FarsiDicAPI();
	GoogleTranslateEnFaBurst g = new GoogleTranslateEnFaBurst(50);
	ArrayList<String> input_En = new ArrayList<String>();
	ArrayList<ArrayList<String>> output_Fa = new ArrayList<ArrayList<String>>();

	ArrayList<String> input_Fa = new ArrayList<String>();
	ArrayList<ArrayList<String>> output_En = new ArrayList<ArrayList<String>>();

	PrintWriter writer;
	PrintWriter XmlWriter;
	// PrintWriter transWriterInfo;
	// PrintWriter transWriter;
	PrintWriter pairs;
	ArrayList<Double> similarity = new ArrayList<Double>();
	ArrayList<String> suspCandidFileNames = new ArrayList<String>();
	//StringBuffer stringSuspMatn;
	Double obfuscationDegree = 0D;

	public static void main(String[] args) {
		PlagiarizedTextGenerator suspFileCreator = new PlagiarizedTextGenerator();
		// suspFileCreator.cleanFiles();
		try {
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {

					if (args[i].startsWith("-")) {
						if (args[i].equals("-obfuscation_type_index")) {
							obfuscation_type_index = Integer
									.parseInt(args[i + 1]);
							i++;

						}
						if (args[i].equals("-dataset_dir")) {
							DATASET_MAIN_DIR = args[i + 1];
							i++;
						}
					}

				}
			}
			dataset_suspcandid_foldername = DATASET_MAIN_DIR
					+ "norm_susp_candid/";
			dataset_srcfoldername = DATASET_MAIN_DIR + "src/";
			dataset_suspdetailsfoldername = DATASET_MAIN_DIR
					+ type_of_plagiarismPrefix[obfuscation_type_index] + "-"
					+ type_of_plagiarism[obfuscation_type_index] + "/";
			dataset_suspfoldername = DATASET_MAIN_DIR + "susp/susp-"
					+ type_of_plagiarism[obfuscation_type_index] + "/";

			pairsFileName = dataset_suspdetailsfoldername + "pairs";
		} catch (Exception e) {
			System.err.println("Wrong Format Input Arguments!");
			System.exit(0);
		}
		if (OBFUSCATION_STRATEGY[obfuscation_type_index]
				.equals("REPLACE_WORDS")
				|| OBFUSCATION_STRATEGY[obfuscation_type_index]
						.equals("COMBINATION")) {
			farsnetUtility = new FarsnetUtility();
		}
		try {
			suspFileCreator.createArtificialDataset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createArtificialDataset() throws SAXException, IOException,
			ParserConfigurationException {
		stemmer = new Stemmer();
		pairs = new PrintWriter(pairsFileName);

		List<String> sourcesSortedBasedOnCandidateSusps = makeSimilarFiles();
		for (int i = 0; i < suspCandidFileNames.size()
				/ NUMBER_OF_REPORTED_SILIMARITIES_PER_FILE; i++) {
			randNumberOfFiles = makeRands(1,
					NUMBER_OF_REPORTED_SILIMARITIES_PER_FILE);
			createASuspiciousFile(
					sourcesSortedBasedOnCandidateSusps.subList(i
							* NUMBER_OF_REPORTED_SILIMARITIES_PER_FILE, i
							* NUMBER_OF_REPORTED_SILIMARITIES_PER_FILE
							+ randNumberOfFiles), suspCandidFileNames.get(i), i);

		}
	}

//	public void start() {
//
//		System.out.println("hello world");
//
//		try {
//			stemmer = new Stemmer();
//			pairs = new PrintWriter(pairsFileName);
//			Map<String, File> FileList = makeListOfFiles(dataset_srcfoldername);
//			List<String> FileList2 = makeSimilarFiles();
//			int iterator = 0;
//			for (int i = 0; i < suspCandidFileNames.size()
//					/ NUMBER_OF_REPORTED_SILIMARITIES_PER_FILE; i++) {
//				randNumberOfFiles = makeRands(1,
//						NUMBER_OF_REPORTED_SILIMARITIES_PER_FILE);
//				// file e susp ee keh ijad mikonim!
//				writer = new PrintWriter(dataset_suspfoldername
//						+ "suspicious-document" + String.format("%05d", i)
//						+ ".txt", "utf-8");
//
//				int numberOfFilesIndexer = 0;
//				for (int nextfile = iterator; nextfile < iterator
//						+ randNumberOfFiles; nextfile++) {
//					// randFileNumber = makeRands(0, FileList.length - 1);
//					String FileName = FileList2.get(nextfile);
//					// int FileNumber= FileList[FileNumber2];
//					randNumberOfPlagiarismCases = makeRands(1, 5);
//					// meta data!
//					XmlWriter = new PrintWriter(dataset_suspdetailsfoldername
//							+ "suspicious-document"
//							+ String.format("%05d", i)
//							+ "-"
//							+ FileList.get(FileName).getName()
//									.replace(".txt", ".xml"), "utf-8");
//
//					creatXmlStart("suspicious-document"
//							+ String.format("%05d", i) + ".txt");
//
//					pairs.append("suspicious-document"
//							+ String.format("%05d", i) + ".txt" + " "
//							+ FileList.get(FileName).getName());
//					pairs.println();
//					pairs.flush();
//					processFile(FileList.get(FileName),
//							randNumberOfPlagiarismCases,
//							suspCandidFileNames.get(nextfile),
//							randNumberOfFiles, numberOfFilesIndexer, i);
//					XmlWriter.flush();
//					XmlWriter.close();
//					numberOfFilesIndexer++;
//
//				}// number of files used for making plagiarism
//					// transWriter.close();
//					// transWriterInfo.close();
//				writer.print(stringSuspMatn);
//				writer.close();
//				stringSuspMatn = null;
//				iterator = iterator + NUMBER_OF_REPORTED_SILIMARITIES_PER_FILE;
//
//			}
//		} catch (Exception e) {
//			System.err.print(e);
//			e.printStackTrace();
//		}
//
//	}

	RandomGaussian gRandom = new RandomGaussian();

	public Integer makeRands(Integer l, Integer m) {
		Integer range = m - l + 1;
		Integer fraction = (int) (random.nextDouble() * range);
		Integer randomNumber = (Integer) (fraction + l);
		return randomNumber;

	}

	public Map<String, File> makeListOfFiles(String directory) {
		Map<String, File> listOfFiles = new HashMap<String, File>();
		try {
			String Path = directory;
			// file haieh src
			File folder = new File(Path);
			for (File file : folder.listFiles()) {
				listOfFiles.put(file.getName(), file);
			}
		} catch (Exception e) {
			System.err.println(e);
		}

		return listOfFiles;

	}

	public List<String> makeSimilarFiles() {
		List<String> listOfFiles = new ArrayList<String>();
		String[] lineSplit = null;
		String temp;
		try {

			FileInputStream fstream = new FileInputStream(SimilarityFilePath);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

			String line;
			while ((line = br.readLine()) != null) {
				lineSplit = line.split(" ");
				temp = lineSplit[2];// .replace("source-document", "");
				// temp = temp.replace(".txt", "");
				listOfFiles.add(temp);
				similarity.add(Double.parseDouble(lineSplit[4]));
				suspCandidFileNames.add(lineSplit[0]);
			}

		} catch (Exception e) {
			System.err.println(e);
		}
		return listOfFiles;

	}

	public static String getMatn(File f) {
		String matn = "";
		try {

			FileInputStream fstream = new FileInputStream(f);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in, "UTF8"));

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

	public String[] makeSentences(File f) {

		List<String> Allsentences = new ArrayList<String>();

		String matn = getMatn(f);
		Pattern re = Pattern
				.compile(
						"# Match a sentence ending in punctuation or EOS.\n"
								+ "[^.!?.!؟\\s]    # First char is non-punct, non-ws\n"
								+ "[^.!?.!؟]*      # Greedily consume up to punctuation.\n"
								+ "(?:          # Group for unrolling the loop.\n"
								+ "  [.!?.!؟]      # (special) inner punctuation ok if\n"
								+ "  (?!['\"]?\\s|$)  # not followed by ws or EOS.\n"
								+ "  [^.!?.!؟]*    # Greedily consume up to punctuation.\n"
								+ ")*           # Zero or more (special normal*)\n"
								+ "[.!?.!؟]?       # Optional ending punctuation.\n"
								+ "['\"]?       # Optional closing quote.\n"
								+ "(?=\\s|$)", Pattern.MULTILINE
								| Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(matn);
		while (reMatcher.find()) {
			String group = reMatcher.group();
			Integer startPoint = 0;
			Integer eolIndex = group.indexOf(eol, startPoint);
			while (eolIndex < group.length()) {
				try {
					if (eolIndex < 0) {
						Allsentences.add(group.substring(startPoint));
						break;
					} else {
						Allsentences.add(group.substring(startPoint,
								eolIndex + 1));
						startPoint = eolIndex + 1;
						eolIndex = group.indexOf(eol, startPoint);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return Allsentences.toArray(new String[Allsentences.size()]);
	}

	public String[] makeSentences(String matn) {

		List<String> Allsentences = new ArrayList<String>();

		Pattern re = Pattern
				.compile(
						"# Match a sentence ending in punctuation or EOS.\n"
								+ "[^.!?.!؟\\s]    # First char is non-punct, non-ws\n"
								+ "[^.!?.!؟]*      # Greedily consume up to punctuation.\n"
								+ "(?:          # Group for unrolling the loop.\n"
								+ "  [.!?.!؟]      # (special) inner punctuation ok if\n"
								+ "  (?!['\"]?\\s|$)  # not followed by ws or EOS.\n"
								+ "  [^.!?.!؟]*    # Greedily consume up to punctuation.\n"
								+ ")*           # Zero or more (special normal*)\n"
								+ "[.!?.!؟]?       # Optional ending punctuation.\n"
								+ "['\"]?       # Optional closing quote.\n"
								+ "(?=\\s|$)", Pattern.MULTILINE
								| Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(matn);
		while (reMatcher.find()) {
			String group = reMatcher.group();
			Integer startPoint = 0;
			Integer eolIndex = group.indexOf(eol, startPoint);
			while (eolIndex < group.length()) {
				try {
					if (eolIndex < 0) {
						Allsentences.add(group.substring(startPoint));
						break;
					} else {
						Allsentences.add(group.substring(startPoint,
								eolIndex + 1));
						startPoint = eolIndex + 1;
						eolIndex = group.indexOf(eol, startPoint);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return Allsentences.toArray(new String[Allsentences.size()]);
	}

	int totalsuspLength = 0;

	public void createASuspiciousFile(List<String> srcFilesNamesList,
			String suspFileName, Integer suspCount)
			throws FileNotFoundException, UnsupportedEncodingException {
		List<Pair<Integer, Map<String, Object>>> casesForTheSuspFile = new ArrayList<Pair<Integer, Map<String, Object>>>();

		File suspFile = new File(dataset_suspcandid_foldername + suspFileName);
		StringBuffer stringSuspMatn = new StringBuffer(getMatn(suspFile));
		List<String> suspSentences = Arrays.asList(makeSentences(suspFile));

		for (String srcFileName : srcFilesNamesList) {
                        File testDone = new File(dataset_suspdetailsfoldername
					+ "suspicious-document" + String.format("%05d", suspCount)
					+ "-" + srcFileName.replace(".txt", ".xml"), "utf-8");
                        if(testDone.exists())
                            continue;
			File srcFile = new File(dataset_srcfoldername + srcFileName);
			int randomNumberofPlagiarismCases = makeRands(1, 5);
			StringBuffer sourceFileMatn = new StringBuffer(getMatn(srcFile));
			System.out.println(srcFileName+" length: "+sourceFileMatn.toString().toCharArray().length);
			List<String> srcSentences = Arrays.asList(makeSentences(srcFile));

			while ((srcSentences.size() / randomNumberofPlagiarismCases) == 1) {
				randomNumberofPlagiarismCases--;
				if (randomNumberofPlagiarismCases <= 0)
					break;
			}
			Integer srcPartLength = srcSentences.size()
					/ randomNumberofPlagiarismCases;
			Integer suspPartLength = suspSentences.size()
					/ randomNumberofPlagiarismCases;

			Integer minSrcOffsetSentenceCount = 0;

			for (int round = 0; round < randomNumberofPlagiarismCases; round++) {
				Integer srcOffset_SentenceBased = Math.max(
						makeRands(round * srcPartLength,
								((round + 1) * srcPartLength) - 1),
						minSrcOffsetSentenceCount);
				Integer srcOffset_Charbased = lengthSum(srcSentences, 0,
						srcOffset_SentenceBased);

				if(srcOffset_Charbased > sourceFileMatn.toString().toCharArray().length)
				{
					System.out.println( "Error!!");
					System.exit(0);
				}
				
				Integer sourceCaseSentencesCount = generateCaseRandomLengthSentenceCount(
						srcSentences, srcOffset_SentenceBased);
				Integer sourceCaseLength = lengthSum(srcSentences,
						srcOffset_SentenceBased, srcOffset_SentenceBased
								+ sourceCaseSentencesCount);
				if (sourceCaseLength == 0)
					break;
				minSrcOffsetSentenceCount = srcOffset_SentenceBased
						+ sourceCaseSentencesCount;

				Integer suspOffset_SentenceBased = makeRands(round
						* suspPartLength, ((round + 1) * suspPartLength) - 1);
				Integer suspOffset_Charbased = lengthSum(suspSentences, 0,
						suspOffset_SentenceBased);

				String plagiarizedText = obfucateSrcString(srcSentences,
						srcOffset_SentenceBased, sourceCaseSentencesCount);

				Map<String, Object> caseFeatures = new HashMap<String, Object>();

				caseFeatures.put("source_length", sourceCaseLength);
				caseFeatures.put("source_offset", srcOffset_Charbased);
				caseFeatures.put("this_offset", suspOffset_Charbased);
				caseFeatures.put("this_length", plagiarizedText.toCharArray().length);
				caseFeatures.put("source_reference", srcFile.getName());
				caseFeatures.put("this_reference", suspFileName);
				caseFeatures.put("plagiarized_text", plagiarizedText);

				casesForTheSuspFile.add(new Pair<Integer, Map<String, Object>>(
						suspOffset_Charbased, caseFeatures));
			}
		}

		Map<String, List<Map<String, Object>>> casesPerSource = new HashMap<String, List<Map<String, Object>>>();

		generateSuspFile(srcFilesNamesList, suspCount, casesForTheSuspFile,
				stringSuspMatn, casesPerSource);

		createXMLFiles(suspCount, casesPerSource);
	}

	public void generateSuspFile(List<String> srcFilesNamesList,
			Integer suspCount,
			List<Pair<Integer, Map<String, Object>>> casesForTheSuspFile,
			StringBuffer stringSuspMatn,
			Map<String, List<Map<String, Object>>> casesPerSource)
			throws FileNotFoundException, UnsupportedEncodingException {
		Collections.sort(casesForTheSuspFile,
				new Comparator<Pair<Integer, Map<String, Object>>>() {

					public int compare(Pair<Integer, Map<String, Object>> arg0,
							Pair<Integer, Map<String, Object>> arg1) {
						return arg0.fst.compareTo(arg1.fst);
					}

				});

		Integer insertedTextLength = 0;

		for (String sourceFileName : srcFilesNamesList) {
			File sourceFile = new File(dataset_srcfoldername + sourceFileName);
			casesPerSource.put(sourceFile.getName(),
					new ArrayList<Map<String, Object>>());
		}
		for (int i = 0; i < casesForTheSuspFile.size(); i++) {
			Integer suspOffset = (Integer) casesForTheSuspFile.get(i).snd
					.get("this_offset");
			suspOffset += insertedTextLength;
			casesForTheSuspFile.get(i).snd.put("this_offset", suspOffset);

			stringSuspMatn.insert(suspOffset,
					casesForTheSuspFile.get(i).snd.get("plagiarized_text"));

			insertedTextLength += (Integer) casesForTheSuspFile.get(i).snd
					.get("this_length");

			casesPerSource.get(
					casesForTheSuspFile.get(i).snd.get("source_reference"))
					.add(casesForTheSuspFile.get(i).snd);
		}

		writer = new PrintWriter(dataset_suspfoldername + "suspicious-document"
				+ String.format("%05d", suspCount) + ".txt", "utf-8");

		writer.print(stringSuspMatn);
		writer.close();
	}

	public void createXMLFiles(Integer suspCount,
			Map<String, List<Map<String, Object>>> casesPerSource)
			throws FileNotFoundException, UnsupportedEncodingException {
		for (String srcFileName : casesPerSource.keySet()) {

			pairs.append("suspicious-document"
					+ String.format("%05d", suspCount) + ".txt" + " "
					+ srcFileName);
			pairs.println();
			pairs.flush();
			XmlWriter = new PrintWriter(dataset_suspdetailsfoldername
					+ "suspicious-document" + String.format("%05d", suspCount)
					+ "-" + srcFileName.replace(".txt", ".xml"), "utf-8");

			creatXmlStart("suspicious-document"
					+ String.format("%05d", suspCount) + ".txt");

			for (Map<String, Object> casePerSource : casesPerSource
					.get(srcFileName)) {

				creatXmlEnd((Integer) casePerSource.get("source_offset"),
						(Integer) casePerSource.get("source_length"),
						(Integer) casePerSource.get("this_offset"),
						(Integer) casePerSource.get("this_length"),
						srcFileName, obfuscationDegree);
			}
			XmlWriter.append("</document>");

			XmlWriter.flush();
			XmlWriter.close();
		}
	}

	public String obfucateSrcString(List<String> srcSentences,
			Integer srcOffset_SentenceBased, Integer sourceCaseSentencesCount) {
		String plagiarizedText = "";
		try {
			if (OBFUSCATION_STRATEGY[obfuscation_type_index]
					.equals("NO_OBFUSCATION")) {
				plagiarizedText = noObfuscation(srcSentences,
						srcOffset_SentenceBased, srcOffset_SentenceBased
								+ sourceCaseSentencesCount);

			} else if (OBFUSCATION_STRATEGY[obfuscation_type_index]
					.equals("CIRCULAR_TRANSLATION")) {
				plagiarizedText = circularTranslation(srcSentences,
						srcOffset_SentenceBased, srcOffset_SentenceBased
								+ sourceCaseSentencesCount);

			} else if (OBFUSCATION_STRATEGY[obfuscation_type_index]
					.equals("REPLACE_WORDS")) {
				obfuscationDegree = 0D;
				plagiarizedText = replaceWords(srcSentences,
						srcOffset_SentenceBased, srcOffset_SentenceBased
								+ sourceCaseSentencesCount);

			} else if (OBFUSCATION_STRATEGY[obfuscation_type_index]
					.equals("SHUFFLE_SENTENCES")) {
				plagiarizedText = shuffleSentences(srcSentences,
						srcOffset_SentenceBased, srcOffset_SentenceBased
								+ sourceCaseSentencesCount);
			} else if (OBFUSCATION_STRATEGY[obfuscation_type_index]
					.equals("COMBINATION")) {

				plagiarizedText = noObfuscation(srcSentences,
						srcOffset_SentenceBased, srcOffset_SentenceBased
								+ sourceCaseSentencesCount);

				for (String obfuscation_method : OBFUSCATION_STRATEGY) {
					if (!((obfuscation_method.equals("NO_OBFUSCATION")) || (obfuscation_method
							.equals("COMBINATION")) || (obfuscation_method
							.equals("REPLACE_WORDS")) ) ) {

						Integer applyOrNot = random.nextInt(2);
						if (applyOrNot == 1) {
							obfuscation_type_index = Arrays.asList(
									OBFUSCATION_STRATEGY).indexOf(
									obfuscation_method);
							List<String> pSentences = Arrays
									.asList(makeSentences(plagiarizedText));
							plagiarizedText = obfucateSrcString(pSentences, 0,
									pSentences.size());
						}
					}
				}
				obfuscation_type_index = Arrays.asList(OBFUSCATION_STRATEGY)
						.indexOf("COMBINATION");
			}
		} catch (Exception e) {
			System.out.println("exception in obfuscation method!");
			e.printStackTrace();
		}

		return plagiarizedText;
	}

//	public void processFile(File f, Integer randNumberOfPlagiarismCases2,
//			String roman, Integer randNumberOfFiles2, int zarib, int i) {
//
//		List<String> sentences = Arrays.asList(makeSentences(f));
//		StringBuffer stringSrcMatn = new StringBuffer(getMatn(f));
//		// file susp
//		File ff = new File(dataset_suspcandid_foldername + roman);
//		List<String> romanSentences = Arrays.asList(makeSentences(ff));
//
//		if (stringSuspMatn == null) {
//			stringSuspMatn = new StringBuffer(getMatn(ff));
//			totalsuspLength = 0;
//
//		}
//
//		Integer charBasedStartPointInSrc = 0;
//		Integer charBasedStartPointInSusp = 0;
//		Integer round = 0;
//		Integer sentenceBasedStartPointInSrc = 0;
//		Integer sentenceBasedStartPointInSusp = 0;
//		Integer firstStartIndexSusp = 0;
//		try {
//			for (round = 0; round < randNumberOfPlagiarismCases2; round++) {
//
//				sentenceBasedStartPointInSrc = makeRands(
//						round
//								* (sentences.size() / randNumberOfPlagiarismCases2),
//						(round + 1)
//								* (sentences.size() / randNumberOfPlagiarismCases2)
//								- 3);
//				try {
//					charBasedStartPointInSrc = stringSrcMatn.indexOf(
//							sentences.get(sentenceBasedStartPointInSrc),
//							lengthSum(sentences, 0,
//									sentenceBasedStartPointInSrc));
//				} catch (Exception e) {
//					e.printStackTrace();
//					// System.out.println("Size of src: "+
//					// sentences.size()+" randStart: "+randStart);
//				}
//
//				sentenceBasedStartPointInSusp = makeRands(
//						(int) round
//								* (romanSentences.size() / (randNumberOfFiles2 * randNumberOfPlagiarismCases2)),
//						(int) ((round + 1)
//								* (romanSentences.size() / (randNumberOfFiles2 * randNumberOfPlagiarismCases2)) - 1));
//				try {
//					charBasedStartPointInSusp = Math
//							.max(charBasedStartPointInSusp,
//									stringSuspMatn.indexOf(
//											romanSentences
//													.get(sentenceBasedStartPointInSusp),
//											lengthSum(romanSentences, 0,
//													sentenceBasedStartPointInSusp)
//													+ totalsuspLength));
//					// System.out.println("calculated: "+(startIndexSusp+totalsuspLength)+" "+" read"+stringSuspMatn.indexOf(romanSentences.get(randStartSusp)));
//				} catch (Exception e) {
//					e.printStackTrace();
//					// System.out.println("Size of suspRoman: "+
//					// romanSentences.size()+" randStartSusp: "+randStartSusp);
//				}
//
//				if (firstStartIndexSusp > charBasedStartPointInSusp)
//					System.out.println("Overlap in inserting Plagiarism !!");
//				firstStartIndexSusp = charBasedStartPointInSusp;
//				Integer sourceCaseSentencesCount = generateCaseRandomLengthSentenceCount(
//						sentences, charBasedStartPointInSrc);
//
//				Integer sourceCaseLength = lengthSum(sentences,
//						sentenceBasedStartPointInSrc,
//						sentenceBasedStartPointInSrc + sourceCaseSentencesCount);
//				// System.out.println("randLength: "+randLength);
//				Integer suspCaseLength = 0;
//				// System.out.println("FirstStartIndex: "+firstStartIndexSusp+" "+" startIndex"+startIndexSusp);
//				String plagiarizedText = "";
//
//				try {
//					if (OBFUSCATION_STRATEGY[obfuscation_type_index]
//							.equals("NO_OBFUSCATION")) {
//						plagiarizedText = noObfuscation(sentences,
//								sentenceBasedStartPointInSrc,
//								sentenceBasedStartPointInSrc
//										+ sourceCaseSentencesCount);
//
//						if (!plagiarizedText.equals(stringSrcMatn.substring(
//								charBasedStartPointInSrc,
//								charBasedStartPointInSrc + sourceCaseLength))) {
//							System.out
//									.println("error in no obfuscation plagiarism creation");
//						}
//					} else if (OBFUSCATION_STRATEGY[obfuscation_type_index]
//							.equals("CIRCULAR_TRANSLATION")) {
//						plagiarizedText = circularTranslation(sentences,
//								sentenceBasedStartPointInSrc,
//								sentenceBasedStartPointInSrc
//										+ sourceCaseSentencesCount);
//						/*
//						 * transWriterInfo.write("#suspFile: "+"suspicious-document"
//						 * + String.format("%05d", i)
//						 * +" #srcFile: "+f.getName()+
//						 * " srcIndx: "+startIndexSrc+
//						 * " sourceLenght: "+lengthSrc
//						 * +" suspIndx: "+firstStartIndexSusp);
//						 * transWriterInfo.write(eol);
//						 * transWriter.write(noObfuscation(sentences, randStart,
//						 * randStart + randLength ).replace("\n", " "));
//						 * transWriter.write(eol);
//						 */
//					} else if (OBFUSCATION_STRATEGY[obfuscation_type_index]
//							.equals("REPLACE_WORDS")) {
//						obfuscationDegree = 0D;
//						plagiarizedText = replaceWords(sentences,
//								sentenceBasedStartPointInSrc,
//								sentenceBasedStartPointInSrc
//										+ sourceCaseSentencesCount);
//
//					} else if (OBFUSCATION_STRATEGY[obfuscation_type_index]
//							.equals("SHUFFLE_SENTENCES")) {
//						plagiarizedText = shuffleSentences(sentences,
//								sentenceBasedStartPointInSrc,
//								sentenceBasedStartPointInSrc
//										+ sourceCaseSentencesCount);
//					} else if (OBFUSCATION_STRATEGY[obfuscation_type_index]
//							.equals("COMBINATION")) {
//
//					}
//					// System.out.println(plagiarizedText);
//				} catch (Exception e) {
//					// System.out.println("exception in obfuscation method!");
//					e.printStackTrace();
//				}
//				suspCaseLength = plagiarizedText.length();
//				if (sourceCaseLength == 0) {
//					// System.out.println("here");
//				}
//				try {
//					stringSuspMatn.insert(firstStartIndexSusp, plagiarizedText);
//				} catch (Exception e) {
//					System.out.println("Exception while inserting");
//					e.printStackTrace();
//				}
//
//				try {
//
//					if (firstStartIndexSusp > stringSuspMatn.length()) {
//						System.out.println("Something bad happened 2!");
//						System.exit(0);
//					}
//					if (!stringSuspMatn.substring(firstStartIndexSusp,
//							firstStartIndexSusp + suspCaseLength).equals(
//							stringSrcMatn
//									.substring(charBasedStartPointInSrc,
//											charBasedStartPointInSrc
//													+ sourceCaseLength)))
//						System.out.println("src and susp are not equal :)");
//
//					if ((sourceCaseLength > 0) && (suspCaseLength > 0)) {
//						System.out.println("src offset:"
//								+ charBasedStartPointInSrc + " susp-offset:"
//								+ firstStartIndexSusp + " " + f.getName()
//								+ "\n" + plagiarizedText + "\n" + "real Index:"
//								+ stringSuspMatn.indexOf(plagiarizedText));
//						creatXmlEnd(charBasedStartPointInSrc, sourceCaseLength,
//								firstStartIndexSusp, suspCaseLength,
//								f.getName(), obfuscationDegree);
//						charBasedStartPointInSusp = charBasedStartPointInSusp
//								+ plagiarizedText.length();
//						totalsuspLength += suspCaseLength;
//						firstStartIndexSusp += plagiarizedText.length();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					System.out.println("Exception in createXMLEnd! ");
//				}
//
//			}
//
//			// writer.flush();
//			XmlWriter.append("</document>");
//			XmlWriter.flush();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}

	private Integer generateCaseRandomLengthSentenceCount(
			List<String> sentences, Integer startSentenceIndex) {
		Long randomNumber = gRandom.getGaussian(CASE_NORMAL_DIST_MEAN,
				CASE_NORMAL_DIST_STDEV).longValue();

		Long sentencesRealLength = 0L;
		Integer sentenceCount = 0;
		int i = startSentenceIndex;
		int countLastZeroLength = 0;
		for (; i < sentences.size(); i++) {
			sentenceCount++;
			sentencesRealLength += sentences.get(i).toCharArray().length+ 1;
			if (sentences.get(i).replaceAll("[\n\\s]+", "").trim().toCharArray().length == 0)
				countLastZeroLength++;
			else {
				countLastZeroLength = 0;
			}
			if ((sentencesRealLength - countLastZeroLength) >= randomNumber)
				break;
		}
		return sentenceCount - countLastZeroLength;
	}

	private Integer lengthSum(List<String> sentences,
			Integer startSentenceIndex, Integer endSentenceIndex) {
		// TODO Auto-generated method stub
		Integer length = 0;

		for (Integer j = startSentenceIndex; j < endSentenceIndex; j++) {
			if (sentences.get(j).endsWith(eol))
				length += sentences.get(j).toCharArray().length;
			else
				length += sentences.get(j).toCharArray().length+ 1;

		}
		return length;
	}

	public String circularTranslation(List<String> sentences, int from, int to) {
		List<String> srcStrings = new ArrayList<String>();
		String wholeText = "";
		String result = "";
		String srcString = "";
		for (int i = from; i < to; i++) {
			if ((srcString.toCharArray().length + sentences.get(i).toCharArray().length) >= 100) {
				if (srcString.toCharArray().length > 0)
					srcStrings.add(srcString);
				srcString = "";
			}
			srcString += sentences.get(i) + " ";
			wholeText += sentences.get(i) + " ";
		}
		if (srcString.toCharArray().length > 0)
			srcStrings.add(srcString);

		Translation tranlator = new Translation();
		try {
			List<String> trans = new ArrayList<String>();
			List<String> retrans = new ArrayList<String>();
			for (String src : srcStrings) {
				List<String> translationResult = tranlator.transGoogleFaEn(src);
				if ((translationResult != null)
						&& (translationResult.size() > 0)) {
					trans.add(translationResult.get(0));
					List<String> retranslationResult = tranlator
							.transGoogleEnFa(translationResult.get(0));
					if ((retranslationResult != null)
							&& (retranslationResult.size() > 0)) {
						result += retranslationResult.get(0);
						retrans.add(retranslationResult.get(0));
					}
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * while (trans == "") { // trans =
		 * FarsiDicAPI.getInstance().TextEngToPersian(sentences[j]);
		 * input_Fa.clear(); output_En.clear(); input_Fa.add(srcString.trim());
		 * output_En = g.fromSecondToFirst(input_Fa); for (int i=0; i<
		 * output_En.get(0).size(); i++){ trans =trans +
		 * output_En.get(0).get(i); }
		 * 
		 * }
		 */

		/*
		 * while (retrans == "") { input_En.clear(); output_Fa.clear();
		 * input_En.add(trans); output_Fa = g.fromFirstToSecond(input_En); for
		 * (int i=0; i< output_Fa.get(0).size(); i++){ retrans =retrans +
		 * output_Fa.get(0).get(i); } }
		 */

		return result;
	}

	public String farsnetPOSconvertor(String pos) {
		if (pos.toLowerCase().equals("con")) {
			return "Adverb";
		}

		return pos;
	}

	public String noObfuscation(List<String> sentences, int startSentIndex,
			int endSentIndex) {

		String noObfuscatedStr = "";
		for (int j = startSentIndex; j < endSentIndex; j++) {
			if (!(sentences.get(j).endsWith(eol))) {
				noObfuscatedStr += sentences.get(j);// .trim();
				noObfuscatedStr += " ";
			} else {
				noObfuscatedStr += sentences.get(j);
			}
		}
		return noObfuscatedStr;
	}

	public List<TaggedWord> posTag(String sentence) {
		return HunPOSTagger.tagSentence(sentence);
	}

	public String replaceWords(List<String> sentences, int startSentIndex,
			int endSentIndex) throws SQLException {
		String plagiarizeText = "";
		Double totalWordCount = 0D;
		for (int j = startSentIndex; j < endSentIndex; j++) {
			List<TaggedWord> posTagged = posTag(sentences.get(j));

			if (posTagged != null) {
				TaggedWord[] words = posTagged.toArray(new TaggedWord[posTagged
						.size()]);
				String[] new_words = new String[posTagged.size()];
				totalWordCount += words.length;
				for (int i = 0; i < words.length; i++) {
					Morphology wordMorphem = stemmer.getStem(
							words[i].getWord(), farsnetUtility
									.convertToFarsnetPOS(words[i].getTag()));
					new_words[i] = farsnetUtility.getAlternateWord(sentences
							.subList(j, j + 1), wordMorphem.getRoot(),
							farsnetUtility.convertToFarsnetPOS(words[i]
									.getTag()));
					new_words[i] = wordMorphem.createSynonym(new_words[i]);
					obfuscationDegree++;
					if (new_words[i].toCharArray().length == 0) {
						new_words[i] = words[i].getWord();
						obfuscationDegree--;
					} else if (new_words[i].equals(words[i].getWord())) {
						obfuscationDegree--;
					}
				}

				String newSent = "";
				for (int i = 0; i < new_words.length; i++) {
					if (i > 0)
						newSent += " ";
					newSent += new_words[i];
				}
				System.out.println("###################" + sentences.get(j)
						+ " ---> " + newSent);
				plagiarizeText += newSent;
			} else {
				plagiarizeText += sentences.get(j);
			}
		}
		obfuscationDegree = obfuscationDegree / totalWordCount;
		return plagiarizeText;
	}

	public String shuffleSentences(List<String> sentences, int i, int j) {
		Collections.shuffle(sentences.subList(i, j));
		return noObfuscation(sentences, i, j);
	}

	public void creatXmlStart(String refName) {
		XmlWriter.append("<document reference=\"" + refName + "\">");
		XmlWriter.println();
		XmlWriter.flush();
	}

	public void creatXmlEnd(int startSrc, int lengthSrc, int startSusp,
			int lengthSusp, String srcName, Double obfuscationDegree) {
		XmlWriter.append("<feature name=\"plagiarism\" ");
		XmlWriter.append("type=\"" + type_of_plagiarism[obfuscation_type_index]
				+ "\" manual_obfuscation=\"false\" ");
		XmlWriter.append("obfuscaton_degree=\"" + obfuscationDegree + "\" ");
		XmlWriter.append("this_language=\"fa\" ");
		XmlWriter.append("this_offset=\"" + startSusp + "\" ");
		XmlWriter.append("this_length=\"" + lengthSusp + "\" ");
		XmlWriter.append("source_reference=\"" + srcName + "\" ");
		XmlWriter.append("source_language=\"fa\" ");
		XmlWriter.append("source_offset=\"" + startSrc + "\" ");
		XmlWriter.append("source_length=\"" + lengthSrc + "\" ");
		XmlWriter.append("/>");
		XmlWriter.println();
		XmlWriter.flush();

	}

	public void rename() {

		File folder = new File(dataset_srcfoldername);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {

				File f = new File(dataset_srcfoldername
						+ listOfFiles[i].getName());
				String s = listOfFiles[i].getName().replace(".xml", "");
				s = String.format("%05d", Integer.parseInt(s));
				f.renameTo(new File(dataset_srcfoldername + "source-document"
						+ s + ".txt"));
			}
		}

	}

	public void cleanFiles() {
		File dir = new File(dataset_srcfoldername);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		});

		for (File file : files) {
			cleanFile(file);
		}

		dir = new File(dataset_suspcandid_foldername);
		files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		});

		for (File file : files) {
			cleanFile(file);
		}
	}

	public void cleanFile(File file) {
		List<String> sentences = Arrays.asList(makeSentences(file));

		StringBuilder fileString = new StringBuilder("");
		for (String sent : sentences) {
			fileString.append(sent + " ");
		}

		try {
			writer = new PrintWriter(file.getPath(), "utf-8");
			writer.write(fileString.toString());
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void putTranslations() {
		ArrayList<TranslationCase> translations = readTranslations();
	}

	public ArrayList<TranslationCase> readTranslations() {
		ArrayList<TranslationCase> translations = new ArrayList<TranslationCase>();

		return translations;
	}
}

class TranslationCase {
	String translatedText;
	Integer suspFileIndex;
	Long srcIndex;
	Long srcLength;
	String srcFileName;
	String suspFileName;

	public void fillFields(String info, String text) {
		translatedText = text;
		String[] tokens = info.split("\\s+");
		for (int i = 0; i < tokens.length; i += 2) {
			if (tokens[i].equals("#suspFile:")) {
				suspFileName = tokens[i + 1].trim() + ".txt";
			} else if (tokens[i].equals("#srcFile:")) {
				srcFileName = tokens[i + 1].trim() + ".txt";
			} else if (tokens[i].equals("srcIndex:")) {
				srcIndex = Long.parseLong(tokens[i + 1]);
			} else if (tokens[i].equals("srcLength:")) {
				srcLength = Long.parseLong(tokens[i + 1]);
			} else if (tokens[i].equals("suspIndex:")) {
				suspFileIndex = Integer.parseInt(tokens[i + 1]);
			}
		}
	}

	public void pushInfile() {
		File ff = new File(PlagiarizedTextGenerator.dataset_suspfoldername
				+ suspFileName);
		StringBuffer stringSuspMatn = new StringBuffer(
				PlagiarizedTextGenerator.getMatn(ff));
		stringSuspMatn.insert(suspFileIndex, translatedText);
		ff.delete();

		try {
			PrintWriter suspwriter = new PrintWriter(
					PlagiarizedTextGenerator.dataset_suspfoldername
							+ suspFileName, "utf-8");
			suspwriter.write(stringSuspMatn.toString());
			suspwriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public String getTranslatedText() {
		return translatedText;
	}

	public void setTranslatedText(String translatedText) {
		this.translatedText = translatedText;
	}

	public Integer getSuspFileIndex() {
		return suspFileIndex;
	}

	public void setSuspFileIndex(Integer suspFileIndex) {
		this.suspFileIndex = suspFileIndex;
	}

	public Long getSrcIndex() {
		return srcIndex;
	}

	public void setSrcIndex(Long srcIndex) {
		this.srcIndex = srcIndex;
	}

	public Long getSrcLength() {
		return srcLength;
	}

	public void setSrcLength(Long srcLength) {
		this.srcLength = srcLength;
	}

	public String getSrcFileName() {
		return srcFileName;
	}

	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}

	public String getSuspFileName() {
		return suspFileName;
	}

	public void setSuspFileName(String suspFileName) {
		this.suspFileName = suspFileName;
	}

}
