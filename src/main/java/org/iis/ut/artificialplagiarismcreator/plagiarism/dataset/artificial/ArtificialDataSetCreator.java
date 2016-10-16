package org.iis.ut.artificialplagiarismcreator.plagiarism.dataset.artificial;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;

public class ArtificialDataSetCreator {

	public static void main(String[] args) {
		ArtificialDataSetCreator artificialDataSetCreator = new ArtificialDataSetCreator();
		try {
			File dir = new File(
					"/Users/MacBookPro/Uni-MS/Final Project/Datasets/RomanDataset/Saman/");
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return !name.startsWith(".");
				}
			});

			for (File file : files) {

				File roman_dir = new File(file.getPath());
				File[] roman_files = roman_dir.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".normalized")
								&& !(name.endsWith("_0.txt.normalized"));
					}
				});
				for (File roman_file : roman_files) {
					artificialDataSetCreator.paragraphExtractor(new File(
							roman_file.getPath()));
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void paragraphExtractor(File file) throws IOException {

		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file.getPath() + ".paragraphs"), "UTF-8"));
		RandomAccessFile f = new RandomAccessFile(file.getPath(), "r");
		byte[] rtf_bytes = new byte[(int) f.length()];
		f.read(rtf_bytes);
		f.close();
		String fileString = new String(rtf_bytes, "UTF-8");
		fileString = fileString.replaceAll("(?m)^[ \t]*\r?\n", "");
		String[] paragraphs = fileString.split("\n");

		for (int i = 1; i < paragraphs.length; i++) {
			String tags = "<Paragraph>\n<ParagraphID>"
					+ file.getName().substring(0, file.getName().indexOf("."))
					+ "_" + i + "</ParagraphID>\n";
			String newParagraph = tags + paragraphs[i] + "\n</Paragraph>\n";
			out.append(newParagraph);
			System.out.println(paragraphs[i].length() + " ");
		}
		out.close();
	}

}
