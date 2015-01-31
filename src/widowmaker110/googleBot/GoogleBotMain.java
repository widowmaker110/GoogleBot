/**
 * @author Alexander Miller
 * Version: 1/31/2014
 * 
 * This program, using Google Search as the tunnel of information, attempts to find out as much data about 
 * any given person. It searches through websites and figures out if the site actually contains information 
 * on said person.
 * 
 * Scenario: You are a student about to graduate and about to apply to a bunch of jobs. You had fun but are now 
 * worried something out there may damage your image. You had already used Googles Image search on yourself but 
 * now need to worry about all of the text written pages. You don't want to spend weeks doing this, so you need
 * a program which can search hundreds of pages in mere minutes for you.
 * 
 * I received some help on searching from this stackoverflow question.
 * http://stackoverflow.com/questions/3727662/how-can-you-search-google-programmatically-java-api
 * 
 */

package widowmaker110.googleBot;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleBotMain {
	
	private static int pages;
	private static String query;
	private static ArrayList<webPageObject> array;
	
	// a series of brute force attributes to look up
	// TODO make sure to change these attributes to things
	// TODO which are distinct in your resume. The more you
	// TODO put into these attributes, the better the results
	private static String[] Attributes = {
		  "class of 2002",
		  "John Smith",
		  "John Cater Smith", 
		  "Ivy Tech Community College", 
		  "Computer Science", 
		  "Eagle Scout",
		  "Computer Science",
		  "Programmer",
		  "E-Portfolio",
		  "123-456-7891"
		  };
	
	/**
	 * search() performs a lot of the google result page scraping. As it finds a result page url + title
	 * it adds it to the global array to be processed at a later time. 
	 * @throws IOException
	 */
	private static void search() throws IOException
	{
		String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; // Change this to your company's name and bot homepage!
		Elements links;
		
		for(int i = 0; i < pages; i++)
		{
			// if its the first page of google results
			if(i == 0)
			{
				links = Jsoup.connect(google + URLEncoder.encode(query, charset)).userAgent(userAgent).get().select("li.g>h3>a");
			}
			// handling all the pages of google results after the first page
			else
			{
				String page = "&start=" + String.valueOf(i*10); // for extended pages beyond the first set of results
				links = Jsoup.connect(google + URLEncoder.encode(query, charset) + page).userAgent(userAgent).get().select("li.g>h3>a");
			}
			
			for (Element link : links) {
			    String title = link.text();
			    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
			    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

			    if (!url.startsWith("http")) {
			        continue; // Ads/news/etc.
			    }
			    
			    /*
			     * Debugging
			     * 
			    	System.out.println("Title: " + title);
			    	System.out.println("URL: " + url);
			    	System.out.println("");
			    */
			    
			    // add objects to the array
				webPageObject object = new webPageObject(title, url, 0 );
				array.add(object);
			}
		}
	}
	
	/**
	 * getQuery simply asks the user what they would like to search
	 */
	private static void getQuery()
	{
		System.out.println("Enter the name of the person you would like to search. Then press enter.");
		Scanner s = new Scanner(System.in);
		query = s.nextLine();
		
		System.out.println("How many pages would you like this bot to search? (Note: each results page has about 10 links) Then press enter.");
		pages = s.nextInt();
		s.close();
	}
	
	/**
	 * getWebPages grabs each of the url pages and sends it off to cycle(Element, webPageObject)
	 * to find out just how useful the information is to the user 
	 */
	private static void getWebPages()
	{
		// http://jsoup.org/cookbook/extracting-data/dom-navigation
		
		// web page document using Jsoup
		Document doc;
		Elements body;
		for(int i = 0; i < array.size(); i++)
		{	
			try 
			{
				doc = Jsoup.connect(array.get(i).getUrl()).get();
				body = doc.select("body");
			
				cycle(body, array.get(i));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * count how many possible attributes match the web page.
	 * the more attributes which match the HTML text, the more 
     * likely the page is what the user wants 
	 * 
	 * @param e a non-null JSoup Element
	 * @param w a non-null webPageObject which already has a title and url associated with it
	 */
	private static void cycle(Elements e, webPageObject w)
	{
		for(int i = 0; i < Attributes.length; i++)
		{
			if(e.html().contains(Attributes[i]))
			{
				int amount = w.getAttributes();
				w.setAttributes( amount + 1);
			}
		}
	}
	
	/**
	 * Sort through each web page. Theoretically, if a page
	 * has more "Attributes" found in it, it should be more desired 
	 */
	private static void sort()
	{
		Collections.sort(array, new Comparator<webPageObject>() {
			@Override
			public int compare(webPageObject arg0, webPageObject arg1) {
				if(arg0.getAttributes() < arg1.getAttributes()){
					return 1;
				}else if(arg0.getAttributes() > arg1.getAttributes()){
					return -1;
				}else{
					return 0;
				}
			}
		});
	}
	
	private static void print()
	{
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Below are the following top ranked websites regarding your information");
		
		for(int i = 0; i < array.size(); i++)
		{
			// only want links which have 1 or more attributes matching
			if(!(array.get(i).getAttributes() == 0))
			{
				System.out.println("Title: " + array.get(i).getTitle());
				System.out.println("URL: " + array.get(i).getUrl());
				System.out.println("# of Attributes matched: " + array.get(i).getAttributes());
				System.out.println("");
			}		
		}	
	}
	
	public static void main(String[] args) throws IOException 
	{
		// initialize variables
		array = new ArrayList<webPageObject>();	

		// main components
		getQuery();
		search();
		getWebPages();
		sort();
		print();
	}
}