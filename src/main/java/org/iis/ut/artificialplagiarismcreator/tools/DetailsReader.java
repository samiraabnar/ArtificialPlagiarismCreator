package org.iis.ut.artificialplagiarismcreator.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DetailsReader {

    public static final String detailsPath = "/Users/Sam/Education/MyMasterThesis/Codes/evaluations/Simorgh/details/";
    //"/Users/MacBookPro/Documents/Uni-MS/FinalProject/Code/artificial_plagiarism/dataset/details-replace/";

    public static void main(String[] args) throws XMLStreamException, FileNotFoundException {
        DetailsReader dreader = new DetailsReader();
        /*  File dir = new File(detailsPath);
         File[] files = dir.listFiles(new FilenameFilter() {
         public boolean accept(File dir, String name) {
         return !name.startsWith(".") && name.endsWith(".xml");
         }
         });
         HashMap<String, List<PlagiarismCase>> caseMap = new HashMap<String, List<PlagiarismCase>>();
         for (File file : files) {
         dreader.readDetailsFile(caseMap, file);

         }

         System.out.println("Number of Cases:" + dreader.numberOfCases(caseMap.values()));
         System.out.println(dreader.averageOnFeature("obfuscaton_degree", caseMap.values()));
         dreader.convertToCSV(caseMap.values());*/

        writeoutPairs(dreader.getPairsFromDetails());
    }

    public static void writeoutPairs(Map<String, Set<String>> pairs)
            throws FileNotFoundException {

        String txt = "";
        for (String susp : pairs.keySet()) {
            for (String src : pairs.get(susp)) {
                txt += susp + " " + src + "\n";
            }
        }
        PrintWriter pairsfile = new PrintWriter("/Users/Sam/Education/MyMasterThesis/Codes/evaluations/Simorgh/pairs");
        pairsfile.write(txt);
        pairsfile.flush();
        pairsfile.close();

    }

    public Map<String, Set<String>> getPairsFromDetails() throws XMLStreamException {
        Map<String, Set<String>> pairs = new HashMap<String, Set<String>>();

        File dir = new File(detailsPath);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.startsWith(".") && name.endsWith(".xml");
            }
        });
        for (File file : files) {
            XMLInputFactory factory = XMLInputFactory.newInstance();

            InputStream in = null;
            in = read(file.getPath());
            XMLEventReader reader = factory.createXMLEventReader(in);
            String suspDoc = "";
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    String tagName = event.asStartElement().getName().getLocalPart();
                    if (tagName.equals("document")) {
                      
                        Attribute reference = event.asStartElement().getAttributeByName(new QName("reference"));
                        suspDoc = reference.getValue().replaceAll(".xml","");

                            System.out.println(suspDoc);
                    }
                    if (tagName.equals("feature")) {
                        Attribute reference = event.asStartElement().getAttributeByName(new QName("source_reference"));
   
                                String srcDoc = reference.getValue();
                                if (!pairs.containsKey(suspDoc)) {
                                    pairs.put(suspDoc, new HashSet<String>());
                                }
                                pairs.get(suspDoc).add(srcDoc);
                            
                        

                    }

                }

            }
        }

        return pairs;
    }

    public List<PlagiarismCase> readDetailsFile(
            HashMap<String, List<PlagiarismCase>> caseMap, File file)
            throws FactoryConfigurationError, XMLStreamException {
        List<PlagiarismCase> caseList = new ArrayList<PlagiarismCase>();
        PlagiarismCase currentCase = new PlagiarismCase();
        String suspFile = "";

        XMLInputFactory factory = XMLInputFactory.newInstance();

        InputStream in = null;
        in = read(file.getPath());
        XMLEventReader reader = factory.createXMLEventReader(in);

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                String tagName = event.asStartElement().getName().getLocalPart();
                if (tagName.equals("document")) {
                    Attribute reference = event.asStartElement().getAttributeByName(new QName("reference"));
                    suspFile = reference.getValue();
                }
                if (tagName.equals("feature")) {
                    Iterator itr = event.asStartElement().getAttributes();
                    while (itr.hasNext()) {
                        Attribute element = (Attribute) itr.next();
                        currentCase.setFeature(element.getName().toString(), element.getValue());
                    }
                    currentCase.setSuspDocument(suspFile);
                    caseList.add(currentCase);
                    currentCase = new PlagiarismCase();
                }

            }

        }

        if (caseMap != null) {
            caseMap.put(suspFile, caseList);
        }
        return caseList;
    }

    private void convertToCSV(Collection<List<PlagiarismCase>> collection) {
        List<String> featurNames = new ArrayList<String>();
        StringBuilder csvString = new StringBuilder("");
        for (List<PlagiarismCase> pcases : collection) {
            for (PlagiarismCase pcase : pcases) {
                if (featurNames.isEmpty()) {
                    featurNames.addAll(pcase.getFeatures().keySet());
                    //Write Header
                    for (String feature : featurNames) {
                        csvString.append(feature + " ");
                    }

                    csvString.append("\n");
                }

                for (String feature : featurNames) {
                    csvString.append(pcase.getFeature(feature) + " ");
                }
                csvString.append("\n");
            }
        }

        PrintWriter writer;
        try {
            writer = new PrintWriter("detailsCSV.csv", "UTF-8");
            writer.write(csvString.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private Integer numberOfCases(Collection<List<PlagiarismCase>> collection) {
        Integer count = 0;
        for (List<PlagiarismCase> pcases : collection) {
            count += pcases.size();
        }

        return count;
    }

    private Double averageOnFeature(String fname, Collection<List<PlagiarismCase>> collection) {
        Double value = 0D;
        double count = 0;
        for (List<PlagiarismCase> pcases : collection) {
            for (PlagiarismCase pcase : pcases) {
                value += Double.parseDouble(pcase.getFeature(fname));
                count++;
            }
        }

        return value / count;
    }

    

    private InputStream read(String url) {
        try {
            return new FileInputStream(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
