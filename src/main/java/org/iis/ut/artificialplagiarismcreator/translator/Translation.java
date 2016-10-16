/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iis.ut.artificialplagiarismcreator.translator;

/**
 *
 * @author javid
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Translation {


    boolean transliterated=false;

    public List<String> translate(String sl, String tl, String urlWithParams, String resource, String encoding) throws IOException {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 9666));
        URL url = new URL(urlWithParams);
        System.out.println("URL: "+url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
        int a = urlConnection.getResponseCode();
        String result;
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), encoding));
            result = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                result = result + "\n" + line;
            }
        
        String[] translations;
        if(resource.equals("aryanpour")) {
                translations = trimAryanpourResponse(result).split("،");
        }
        if(resource.equals("babylon")) {
                return trimBabylonResponse(result);
        }
        else
        {
                System.out.println(result);
                return trimGoogleResponse(result, false);
        }

       // return Arrays.asList(translations);
    }

    public List<String> transAryanpour(String term) throws UnsupportedEncodingException, IOException {
        return translate("en", "fa", "http://www.aryanpour.com/etop_query.asp?"
                + "keyword="
                + URLEncoder.encode(term, "Windows-1256")
                + "&criteria=0"
                + "&B1=Submit", "aryanpour", "Windows-1256");
    }

    public List<String> transGoogleFaEn(String term) throws UnsupportedEncodingException, IOException {
        return translate("en", "fa", "http://translate.google.com/translate_a/t?client=t"
                + "&text="
                + URLEncoder.encode(term, "utf-8")
                + "&hl=en"
                + "&sl=fa"
                + "&tl=en"
                +"&ie=UTF-8"
                + "&multires=1"
                +"&trs=1"+
                "trs=1" +
                "&inputm=1" +
                "&vkb=1" +
                "&inputm=2" +
                "&prev=btn" +
                "&ssel=5" +
                "&tsel=5"
                + "&otf=2"
                + "&pc=0"
                + "&sc=1", "google", "UTF-8");
        
    }

    
    public List<String> transGoogleEnFa(String term) throws UnsupportedEncodingException, IOException {
        return translate("fa", "en", "http://translate.google.com/translate_a/t?client=t"
                + "&text="
                + URLEncoder.encode(term, "utf-8")
                + "&hl=fa"
                + "&sl=en"
                + "&tl=fa"
                +"&ie=UTF-8"
                + "&multires=1"
                +"&trs=1"+
                "trs=1" +
                "&inputm=1" +
                "&vkb=1" +
                "&inputm=2" +
                "&prev=btn" +
                "&ssel=5" +
                "&tsel=5"
                + "&otf=2"
                + "&pc=0"
                + "&sc=1", "google", "UTF-8");
        
    }
    public List<String> transBritanica(String term) throws UnsupportedEncodingException, IOException {
        return translate("en", "fa", "http://www.britannica.com/search?query="
                + URLEncoder.encode(term, "Windows-1256"), "britanica", "UTF-8");
    }

    public List<String> transDictionaryDotCom(String term) throws UnsupportedEncodingException, IOException {
        return translate("en", "fa", "http://dictionary.reference.com/browse/"
                + URLEncoder.encode(term, "Windows-1256"), "dictionaryDotCom", "UTF-8");
    }

    public List<String> transCollins(String term) throws UnsupportedEncodingException, IOException {
        return translate("en", "fa", "http://www.collinsdictionary.com/dictionary/english/"
                + URLEncoder.encode(term, "Windows-1256"), "collins", "UTF-8");
    }

    List<String> transLongman(String term) throws IOException {
        return translate("en", "fa", "http://www.ldoceonline.com/search/?q="
                + URLEncoder.encode(term, "Windows-1256"), "longman", "UTF-8");
    }

    public List<String> babylon10(String term) throws IOException {
        return translate("en", "fa", "http://translation.babylon.com/english/to-persian/"
                + URLEncoder.encode(term, "Windows-1256"), "babylon", "UTF-8");
    }

    public String trimAryanpourResponse(String r) {
        Document doc;
        String equvalents = "";
        doc = (Document) Jsoup.parse(r);
        Elements fonts = doc.select("font");
        for (Iterator<Element> it = fonts.iterator(); it.hasNext();) {
            Element font = it.next();
            if (font.attr("color").contains("#F7B229")) {
                equvalents = equvalents + " " + font.html();
            } else if (font.html().equals("Your Keyword was not found")) {
                return "";
            }

        }
        return equvalents;
    }

    private List<String> trimGoogleResponse(String result, boolean MT) {
     
        List<String> equivalents = new ArrayList<String>();

        JsonParser parser = new JsonParser();
        JsonArray ja = (JsonArray) parser.parse(result);
        String firstString = "";
        String secondString = "";
        for (JsonElement jo : ja) {
            if(jo.isJsonArray())
            {
                for (JsonElement jjo : jo.getAsJsonArray())
                {
                    if(jjo.isJsonArray())
                    {
                        int i = 0;
                       for (JsonElement jjjo : jjo.getAsJsonArray())
                       {
                           if(i == 0)
                           {
                                if(jjjo.isJsonPrimitive())
                                {
                                    firstString += " "+jjjo.getAsString().trim();
                                }
                           }
                           else if(i == 1)
                           {    
                           if(jjjo.isJsonPrimitive())
                                {
                                    secondString += " "+jjjo.getAsString().trim();
                                }  
                           }
                           i++;
                       }
                    }
                }
                break;
            }
        }
        
        equivalents.add(firstString);
		return equivalents;
    }

    private List<String> trimBabylonResponse(String result) throws IOException {
        String line;
        Document doc;
        List<String> eqList = new LinkedList<String>();
        String equvalents = "";
        doc = (Document) Jsoup.parse(result);
        Elements fonts = doc.select("div");
        for (Iterator<Element> it = fonts.iterator(); it.hasNext();) {
            Element div = it.next();
            if (div.attr("style").contains("#6c8aa9")) {//English Persian Computer Dictionary
                equvalents = equvalents + "-" + div.html();
            }
        }
        equvalents = Jsoup.parse(equvalents).text();
        equvalents.replaceAll("،", "-");
        equvalents.replaceAll(",", "-");
        equvalents.replace("بازگشت به واژه", "");
        equvalents.replaceAll("کلمات مرتبط", "");
        equvalents.replaceAll("(11)", "");
        String[] eqArray = equvalents.split("[،,-.]");
        for (String term : eqArray) {
            if (!term.equals("")) {
                eqList.add(term);
            }
        }
        return eqList;
    }
}
