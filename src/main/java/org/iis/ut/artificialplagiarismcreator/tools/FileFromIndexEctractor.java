/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iis.ut.artificialplagiarismcreator.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;
/**
 *
 * @author Sam
 */
public class FileFromIndexEctractor {
    
    static String indexPath = "/Users/Sam/Education/MyMasterThesis/Codes/Simorgh/SourceRetrievalDataSet/Index/Index_Hamshahri1";
    
    
    public static void main(String[] args) throws IOException {
       IndexReader ireader = IndexReader.open(new SimpleFSDirectory(new File(
					indexPath)));
       
       for(int i =0; i< ireader.numDocs(); i++)
       {
           String text = ireader.document(i).get("TEXT");
           
           BufferedWriter writer = new BufferedWriter( new FileWriter("/Users/Sam/Education/MyMasterThesis/Codes/evaluations/SimorghI/src/"+ ireader.document(i).get("DOCID")));
           writer.write(text);
           writer.close();
       }
    }
}
