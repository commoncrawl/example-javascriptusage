package edu.utwente.mbd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.google.common.collect.Iterables;

import static org.junit.Assert.*;

import edu.utwente.mbd.scriptparse.ScriptTagExtractor;
import edu.utwente.mbd.scriptparse.ScriptInformation;

public class TestScriptExtraction {
	private ScriptTagExtractor buildExtractor(String url, Document doc){
		try {
			return new ScriptTagExtractor(url, doc);
		} catch (Exception e){
			return null;			
		}
	}
	
	@Test
	public void testWikipediaPage() throws IOException{
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("html/wikipedia.html");
		String url = "http://en.wikipedia.org/wiki/JavaScript";
		
		Document doc = Jsoup.parse(is, null, url);
		
		basicAsserts(url, doc);		
	}
	
	@Test
	public void testSDK() throws IOException{
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("html/sdk.html");
		String url = "http://www.schoutsendekock.nl";
		
		Document doc = Jsoup.parse(is, null, url);
		
		int jsTagCount = doc.getElementsByTag("script").size();
		int total = 0, inline = 0, external = 0, unrecognized = 0;
		
		for(ScriptInformation s : buildExtractor(url, doc)){
			System.out.println(String.format("%s inline? %b".format(s.fileName, s.inline)));
		}
		
		// it has a jQuery plugin
		assertTrue(Iterables.contains(buildExtractor(url, doc), new ScriptInformation("jquery.colorbox-min.js", false)));
		// it has google CDN
		assertTrue(Iterables.contains(buildExtractor(url, doc), new ScriptInformation("https://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js", false)));
		
		basicAsserts(url, doc);			}
	
	@Test
	public void testExampleOrg() throws IOException{
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("html/exampleorg.html");
		String url = "http://www.example.org";
		
		Document doc = Jsoup.parse(is, null, url);
		
		// no javascript
		assertEquals(0, Iterables.size(buildExtractor(url,  doc)));
		
		basicAsserts(url, doc);		
	}
	
	private void basicAsserts(String url, Document doc){
		int jsTagCount = doc.getElementsByTag("script").size();
		int total = 0, inline = 0, external = 0, unrecognized = 0;
		
		for(ScriptInformation si : buildExtractor(url, doc)){
			if (si == null){
				unrecognized++;
			} else if (si.inline) {
				inline++;
			} else {
				external++;
			}
			
			total++;	
		}
		
		assertTrue (inline + external + unrecognized == total); // trivial really
		assertEquals(jsTagCount, total);
	}
}