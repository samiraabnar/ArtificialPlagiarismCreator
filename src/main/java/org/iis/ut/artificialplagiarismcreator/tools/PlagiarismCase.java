package org.iis.ut.artificialplagiarismcreator.tools;

import java.util.HashMap;
import java.util.Map;

public class PlagiarismCase {
	Map<String,Object> features;
	
	String sourceDocument = "";
	String suspDocunemt = "";

	private String sourceText;
	public String getSourceText() {
		return sourceText;
	}

	public String getPlagiarizedText() {
		return plagiarizedText;
	}

	private String plagiarizedText;
			
	public String getSuspDocunemt() {
		return suspDocunemt;
	}

	public void setSuspDocunemt(String suspDocunemt) {
		this.suspDocunemt = suspDocunemt;
	}

	public Map<String, Object> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, Object> features) {
		this.features = features;
	}

	public String getSourceDocument() {
		return sourceDocument;
	}

	public void setSourceDocument(String sourceDocument) {
		this.sourceDocument = sourceDocument;
	}

	public PlagiarismCase()
	{
		features = new HashMap<String,Object>();
	}

	public void setFeature(String key, String value) {
		features.put(key, value);
	}
	
	public String getFeature(String key)
	{
		return features.get(key).toString();
	}

	public void setSuspDocument(String suspFile) {
		suspDocunemt = suspFile;		
	}

	public void setSourceText(String srcText) {
		sourceText = srcText;
	}

	public void setPlagiarizedText(String pText) {
		plagiarizedText = pText;
	}

	public String toString()
	{
		StringBuilder string = new StringBuilder();
		
		for(String fkey: features.keySet())
		{
			string.append(fkey+": "+features.get(fkey)+" ");
		}
		string.append("\n");
		string.append("Source Text:\n"+sourceText+"\n");
		string.append("Plagiarized Text:\n"+plagiarizedText+"\n");
		return string.toString();
	}
}
