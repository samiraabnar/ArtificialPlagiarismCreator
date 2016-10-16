package org.iis.ut.artificialplagiarismcreator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


public class StandorfPOSTaggerFormatCovertor {

	private final static String toBeConvertedFile = "/Users/MacBookPro/Documents/Uni-MS/FinalProject/TOOLS/POS_Tagger/BijanKhan_Full_Corpus/Collection UNI.txt";
	private final static String convertedFile = "/Users/MacBookPro/Documents/Uni-MS/FinalProject/TOOLS/POS_Tagger/BijanKhan_Full_Corpus/persian_train.txt";
	private  final static  String eol = System.getProperty("line.separator");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedReader inputFile = new BufferedReader(new InputStreamReader(
					new FileInputStream(toBeConvertedFile), "utf-8"));
			BufferedWriter outputFile = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(convertedFile), "utf-8"));
			String readLine = "";
			while((readLine = inputFile.readLine()) != null)
			{
				if(readLine.contains("DELM"))
				{
					while(readLine.contains("DELM"))
					{
						readLine = inputFile.readLine();
						if(readLine == null)
							break;
					}
				}

				if(readLine != null)
				{
				
					String sentence = "";
					
					while(!readLine.contains("DELM"))
					{
						readLine = readLine.replaceAll("\\s+", "/");
						sentence += readLine+ " ";
						readLine = inputFile.readLine();
					}
					readLine = readLine.replaceAll("\\s+", "/");
					sentence += readLine+ " ";
					outputFile.write(sentence);
					outputFile.write(eol);

				}
			}
			inputFile.close();
			outputFile.close();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(NullPointerException e)
		{
			
		}
		

	}

}
