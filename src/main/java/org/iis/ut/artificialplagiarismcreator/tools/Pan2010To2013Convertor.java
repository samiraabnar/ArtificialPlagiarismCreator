package org.iis.ut.artificialplagiarismcreator.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Pan2010To2013Convertor {

	private static final String CONVERTED_DATASET_DIR = "../evaluations/Ghoghnous2014/corpus/";
                //"../evaluations/PAN2010/pan-plagiarism-test-corpus-2010-standard/";
	private static final String CORPUS_MAIN_DIR = "../evaluations/PAN2010/SamiDataset/pan10-plagiarism-detection-test-corpus-2010-05-17/";

	public static void main(String[] args) {
		Pan2010To2013Convertor pc = new Pan2010To2013Convertor();
		try {
			//pc.readAndConvert2010DatasetSuspFiles(CORPUS_MAIN_DIR);
			// pc.removeNonExistingPairs(CONVERTED_DATASET_DIR + "/pairs",
			// CONVERTED_DATASET_DIR + "susp/simulated",
			// CONVERTED_DATASET_DIR + "src", CONVERTED_DATASET_DIR
			// + "pairs_simulated_filtered");

			
			//  pc.pairToTrecJudgeFileFormatConvertor("../evaluations/Ghoghnous2014/corpus/02-no-obfuscation/pairs","../evaluations/Ghoghnous2014/corpus/02-no-obfuscation/judges");
			//  pc.pairToTrecJudgeFileFormatConvertor("../evaluations/Ghoghnous2014/corpus/03-shuffle-sentences/pairs","../evaluations/Ghoghnous2014/corpus/03-shuffle-sentences/judges");
			//  pc.pairToTrecJudgeFileFormatConvertor("../evaluations/Ghoghnous2014/corpus/01-no-plagiarism/pairs","../evaluations/Ghoghnous2014/corpus/01-no-plagiarism/judges");
			      pc.pairToTrecJudgeFileFormatConvertor("../evaluations/Ghoghnous2014/corpus/05-replace-words/pairs","../evaluations/Ghoghnous2014/corpus/05-replace-words/judges");

                          //  "../evaluations/PAN2013/pan13-text-alignment-test-corpus1-2013-03-08/03-random-obfuscation/pairs",
			 //   CONVERTED_DATASET_DIR  +  "simulatedpairs", 
			 // CONVERTED_DATASET_DIR  + "simulatedpairs_trec"
			 // "../evaluations/PAN2013/pan13-text-alignment-test-corpus1-2013-03-08/03-random-obfuscation/judges"
			//  );
			 
                       //  pc.readAndConvertSimorghII("../evaluations/Ghoghnous2014/corpus/05-replace-words/","../evaluations/Ghoghnous2014/corpus/");
                        // pc.createSourceFolderForSimorghII();
                      //   pc.filterPairSimorghII("../evaluations/SimorghII/corpus/");
			// pc.removeDupPairs(
			// "/Users/MacBookPro/Documents/Uni-MS/FinalProject/Code/PlagiarismDetection/evaluations/PAN2010/sourceRetrieval/sourceRetrieval/simulated/pairs_simulated_filtered",
			// "/Users/MacBookPro/Documents/Uni-MS/FinalProject/Code/PlagiarismDetection/evaluations/PAN2010/sourceRetrieval/sourceRetrieval/simulated/pairs_simulated_filtered");
			
		/*	  pc.makeASpecificSuspFolder(
			  "../evaluations/Ghoghnous2014/corpus/replace-words-susps/"
			  ,
			  "../evaluations/Ghoghnous2014/corpus/05-replace-words/pairs"
			  ,
			  "../evaluations/Ghoghnous2014/corpus/susp/"
			  );*/
			 } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readAndConvert2010DatasetSuspFiles(String corpusMainDir)
			throws FileNotFoundException {

		Map<String, Map<String, Set<String>>> pairMap = new HashMap<String, Map<String, Set<String>>>();
		File suspFoler = new File(corpusMainDir + "suspicious-documents");

		/*
		 * for (String partFolderName : suspFoler.list(new FilenameFilter() {
		 * 
		 * public boolean accept(File dir, String name) { return
		 * name.startsWith("part"); } })) { File partFolder = new
		 * File(suspFoler.getPath() + "/" + partFolderName);
		 */
		String[] files = suspFoler/* partFolder */.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith(".xml");
			}

		});
		for (int k = 0; k < files.length; k++) {
			System.out.println(k + ":" + files[k]);

			try {
				File theFile = new File(suspFoler.getPath() + /*
															 * "/" +
															 * partFolderName +
															 */"/" + files[k]);
				transferASuspFile(pairMap, theFile,suspFoler);

			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
		}
		writeoutPairs(pairMap);
	}

	private void transferASuspFile(
			Map<String, Map<String, Set<String>>> pairMap, File theFile,
			File partFolder) throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(theFile);
		doc.getDocumentElement().normalize();
		System.out.println("---------------------");
		// System.out.println(doc.getDocumentElement().getAttribute("reference"));
		String suspName = doc.getDocumentElement().getAttribute("reference");
		NodeList nList = doc.getElementsByTagName("feature");
		Boolean acceptable = false;
		String mainType = null;
		Set<String> sources = new HashSet<String>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String name = eElement.getAttribute("name");
				if (name.contains("plagiarism")) {
					acceptable = true;
					String type = name.split("-")[0];
					if(mainType == null)
						mainType = type;
					else if(!type.equals(mainType))
					{
						acceptable = false;
						break;
					}
					
					Boolean intraCluster = Boolean.parseBoolean(eElement
							.getAttribute("intra_cluster"));
					
					sources.add(eElement.getAttribute("source_reference"));
				}	
			}
		}
			if(acceptable)
			{
				if (!pairMap.containsKey(mainType)) {
					pairMap.put(mainType, new HashMap<String, Set<String>>());
				}
				if (!pairMap.get(mainType).containsKey(suspName)) {
					pairMap.get(mainType).put(suspName, sources);
				}
				File typeFolderForXML = new File(CONVERTED_DATASET_DIR
						+ mainType);
				if (!typeFolderForXML.exists())
					typeFolderForXML.mkdir();
				File typeFolderForTXT = new File(CONVERTED_DATASET_DIR
						+ "susp/" + mainType);
				if (!typeFolderForTXT.exists())
					typeFolderForTXT.mkdir();
				Files.copy(
						new File(theFile.getPath()).toPath(),
						new File(typeFolderForXML + "/"
								+ theFile.getName()).toPath(),
						StandardCopyOption.REPLACE_EXISTING);

				File suspTextFile = new File(typeFolderForTXT + "/"
						+ suspName);
				if (!suspTextFile.exists())
					Files.copy(new File(partFolder.getPath() + "/"
							+ suspName).toPath(), suspTextFile.toPath());
		}
	}

	public void writeoutPairs(Map<String, Map<String, Set<String>>> pairs)
			throws FileNotFoundException {
		for (String type : pairs.keySet()) {
			String txt = "";
			for (String susp : pairs.get(type).keySet()) {
				for (String src : pairs.get(type).get(susp)) {
					txt += susp + " " + src + "\n";
				}
			}
			PrintWriter pairsfile = new PrintWriter(type + "pairs");
			pairsfile.write(txt);
			pairsfile.flush();
			pairsfile.close();

		}
	}

	
        public void writeoutSimplePairs(Map<String, Set<String>> pairs)
			throws FileNotFoundException {
			String txt = "";
                        String txt2 = "";
			for (String susp : pairs.keySet()) {
				for (String src : pairs.get(susp)) {
					txt += susp + " " + src + "\n";
				}
                                txt2 += susp + "\n";
			}
			PrintWriter pairsfile = new PrintWriter("../evaluations/Ghoghnous2014/corpus/"+"pairs");
			pairsfile.write(txt);
			pairsfile.flush();
			pairsfile.close();
                        
                        PrintWriter suspFileList = new PrintWriter("suspList");
			suspFileList.write(txt2);
			suspFileList.flush();
			suspFileList.close();

	}
        
        public void removeNonExistingPairs(String pairFile, String suspFolder,
			String srcFolder, String destPAir) throws IOException {
		File SuspDir = new File(suspFolder);
		File SrcDir = new File(srcFolder);

		File[] susps = SuspDir.listFiles();
		File[] srcs = SrcDir.listFiles();
		List<File> suspList = Arrays.asList(susps);
		List<File> srcList = Arrays.asList(srcs);
		List<String> suspFileNames = new ArrayList<String>();
		for (File susp : suspList) {
			suspFileNames.add(susp.getName());
		}

		List<String> srcFileNames = new ArrayList<String>();
		for (File src : srcList) {
			srcFileNames.add(src.getName());
		}
		BufferedReader br = new BufferedReader(new FileReader(pairFile));
		String line = null;
		String filteredPairText = "";
		while ((line = br.readLine()) != null) {
			String[] pair = line.split(" ");
			if (pair.length >= 2) {
				String susp = pair[0].trim();
				String src = pair[1].trim();
				if (suspFileNames.contains(susp)) {
					if (srcFileNames.contains(src)) {
						filteredPairText += line + "\n";
					}
				}
			}
		}
		br.close();

		PrintWriter pairsfile = new PrintWriter(destPAir);
		pairsfile.write(filteredPairText);
		pairsfile.flush();
		pairsfile.close();
	}

	public void pairToTrecJudgeFileFormatConvertor(String pairFileName,
			String trecFileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(pairFileName));

		String line = null;
		String trecJudgeString = "";
		while ((line = br.readLine()) != null) {
			String[] pair = line.split(" ");
			String susp = pair[0].replace(".txt", "").trim();
			String src = pair[1].replace(".txt", "").trim();

			trecJudgeString += susp + " 0 " + src + " 1\n";
		}
		br.close();
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				trecFileName)));
		bw.write(trecJudgeString);
		bw.close();
	}

	public void removeDupPairs(String pairFileName, String outFileString)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(pairFileName));

		String line = null;

		Set<String> fileContent = new HashSet<String>();
		while ((line = br.readLine()) != null) {
			fileContent.add(line);
		}
		br.close();
		String pairString = "";
		for (String pair : fileContent)
			pairString += pair + "\n";

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				outFileString)));
		bw.write(pairString.trim());
		bw.close();

	}

	public void makeASpecificSuspFolder(String newSuspFolderAdd,
			String pairFileAdd, String suspPath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				pairFileAdd)));

		String line = null;
		while ((line = br.readLine()) != null) {
			String[] pair = line.split(" ");
			File suspFile = new File(suspPath + pair[0]);
			Files.copy(new File(suspFile.getPath()).toPath(), new File(
					newSuspFolderAdd + "/" + suspFile.getName()).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		}
	}



        private void extractPairFromDetail(
			Map<String, Set<String>> pairMap, File theFile,
			File partFolder) throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(theFile);
		doc.getDocumentElement().normalize();
		System.out.println("---------------------");
		// System.out.println(doc.getDocumentElement().getAttribute("reference"));
		String suspName = doc.getDocumentElement().getAttribute("reference").replace(".xml",".txt");
                String mainSuspName = "";
                NodeList nList = doc.getElementsByTagName("feature");
                int count = 0;
		Set<String> sources = new HashSet<String>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String name = eElement.getAttribute("name");
				if (name.contains("plagiarism")) {
					 sources.add(eElement.getAttribute("source_reference"));
                                        count++;
				}	
			}
		}
			
			if (!pairMap.containsKey(suspName)) {
				pairMap.put(suspName, sources);
			}
                        else
                        {
                            pairMap.get(suspName).addAll(sources);
                        }
                        System.out.println("MainSuspMap: "+ suspName+" "+mainSuspName);
				File folderForXML = new File(CONVERTED_DATASET_DIR + "details/"
						);
				if (!folderForXML.exists())
					folderForXML.mkdir();
				File folderForCandidateSusp = new File(CONVERTED_DATASET_DIR
						+ "candidate_susp/");
				if (!folderForCandidateSusp.exists())
					folderForCandidateSusp.mkdir();
				Files.copy(
						new File(theFile.getPath()).toPath(),
						new File(folderForXML + "/"
								+ theFile.getName()).toPath(),
						StandardCopyOption.REPLACE_EXISTING);

				File suspTextFile = new File(folderForCandidateSusp + "/"
						+ suspName);
				if (!suspTextFile.exists())
					Files.copy(new File(partFolder.getPath() + "/"
							+ suspName).toPath(), suspTextFile.toPath());
		
	}


    public void readAndConvertSimorghII(String detailsDir,String corpusMainDir)
			throws FileNotFoundException {

		Map<String, Set<String>> pairMap = new HashMap<String, Set<String>>();
		File infoFolder = new File(detailsDir);

		/*
		 * for (String partFolderName : suspFoler.list(new FilenameFilter() {
		 * 
		 * public boolean accept(File dir, String name) { return
		 * name.startsWith("part"); } })) { File partFolder = new
		 * File(suspFoler.getPath() + "/" + partFolderName);
		 */
		String[] files = infoFolder/* partFolder */.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith(".xml");
			}

		});
		for (int k = 0; k < files.length; k++) {
			System.out.println(k + ":" + files[k]);

			try {
				File theFile = new File(infoFolder.getPath() + /*
															 * "/" +
															 * partFolderName +
															 */"/" + files[k]);
				extractPairFromDetail(pairMap, theFile,new File("../evaluations/Ghoghnous2014/corpus/susp/susp-replace-words"));

			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
		}
		writeoutSimplePairs(pairMap);
	}


    public void createSourceFolderForSimorghII() throws FileNotFoundException, IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("../evaluations/SimorghII/corpus/mainSuspMap"));
                List<String> mainSuspFiles = new ArrayList<String>();
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] pair = line.split(" ");
			String susp = pair[0].replace(".txt", "").trim();
			String src = pair[1].replace(".txt", "").trim();
                        mainSuspFiles.add(src);
		}
		br.close();
                
                File srcFolder = new File("../evaluations/SimorghI/src/");

		/*
		 * for (String partFolderName : suspFoler.list(new FilenameFilter() {
		 * 
		 * public boolean accept(File dir, String name) { return
		 * name.startsWith("part"); } })) { File partFolder = new
		 * File(suspFoler.getPath() + "/" + partFolderName);
		 */
		String[] files = srcFolder.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.startsWith("H-");
			}

		});
		for (int k = 0; k < files.length; k++) {
                    if(!mainSuspFiles.contains(files[k]))
                    {
                        Files.copy(
						new File(srcFolder.getPath()+"/"+files[k]).toPath(),
						new File("../evaluations/SimorghII/corpus/src" + "/"
								+ files[k]).toPath(),
						StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                
		
    }

    private void filterPairSimorghII(String evaluationsSimorghIIcorpus) throws FileNotFoundException, IOException {
                BufferedReader br = new BufferedReader(new FileReader("../evaluations/SimorghII/corpus/backup/mainSuspMap"));
                List<String> mainSuspFiles = new ArrayList<String>();
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] pair = line.split(" ");
			String susp = pair[0].replace(".txt", "").trim();
			String src = pair[1].replace(".txt", "").trim();
                        mainSuspFiles.add(src);
		}
		br.close();  
                
                
                br = new BufferedReader(new FileReader("../evaluations/SimorghII/corpus/pairs"));
                BufferedWriter bw = new BufferedWriter(new FileWriter("../evaluations/SimorghII/corpus/filtered_pairs"));

		line = null;
		while ((line = br.readLine()) != null) {
			String[] pair = line.split(" ");
			String susp = pair[0].replace(".txt", "").trim();
			String src = pair[1].replace(".txt", "").trim();
                        if(!mainSuspFiles.contains(src))
                        {
                            bw.write(line+"\n");
                            
                        }
		}
		br.close();   
                bw.close();
                
    
    }

}
