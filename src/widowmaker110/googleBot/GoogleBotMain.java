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

package widowmaker110;

import java.awt.FlowLayout;
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

import java.awt.*; 
import java.awt.event.*; 

import javax.swing.*; 
import javax.swing.event.*; 

public class GoogleBotMain extends JFrame {

	private static boolean DeveloperMode = true;
	private static final long serialVersionUID = 1L;
	private static int pages = 10;
	private static String query;
	private static ArrayList<webPageObject> array;
	public static final int WINDOW_WIDTH = 600;
	public static final int WINDOW_HEIGHT = 600;
	
	// JFRAME Variables declaration 
 	private JLabel jLabel2; 
 	private JLabel jLabel3; 
 	private JLabel jLabel4; 
 	private JLabel jLabel7; 
 	private JList jList2; 
 	private JScrollPane jScrollPane2; 
 	private JTextArea jTextArea1; 
 	private JScrollPane jScrollPane3; 
 	private JButton jButton3; 
 	private JPanel contentPane; 
 	//----- 
 	private JPanel jPanel4; 
	
	// a series of brute force attributes to look up
	// TODO make sure to change these attributes to things
	// TODO which are distinct in your resume. The more you
	// TODO put into these attributes, the better the results
	private static String[] Attributes = {};
	
	/**
	 * search() performs a lot of the google result page scraping. As it finds a result page url + title
	 * it adds it to the global array to be processed at a later time. 
	 * @throws IOException
	 */
	private static void search() throws IOException
	{
		String google = "https://www.google.com/search?q=";
		String charset = "UTF-8";
		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; // Change this to your company's name and bot homepage!
		Elements links;
		String[] temp = query.split("\\s+");
		String broken = temp[0];
		String broken2 = temp[1];
		String newBroken = broken + "+" + broken2;
		
		for(int i = 0; i < pages; i++)
		{
			// if its the first page of google results
			if(i == 0)
			{
				links = Jsoup.connect(google + URLEncoder.encode( query, charset)).userAgent(userAgent).get().select("li.g>h3>a");
			}
			// handling all the pages of google results after the first page
			else
			{
				String page = "&start=" + String.valueOf(i*10); // for extended pages beyond the first set of results
				links = Jsoup.connect(google + URLEncoder.encode(query, charset) + page).userAgent(userAgent).get().select("li.g>h3>a");
			}
			
			for (Element link : links) 
			{  
				String title = link.text();
			    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
			    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

			    if (!url.startsWith("http")) {
			        continue; // Ads/news/etc.
			    }
			    
			  if(DeveloperMode == true)
			  {
				System.out.println("Title: " + title);
			    System.out.println("URL: " + url);
			    System.out.println("");
			  }
			    	  
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
		System.out.println("");
		
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
	
	public GoogleBotMain() 
 	{ 
 		super(); 
 		initializeComponent();   
 		this.setVisible(true); 
 	} 
	
 	private void initializeComponent() 
 	{ 
 		jLabel2 = new JLabel(); 
 		jLabel3 = new JLabel(); 
 		jLabel4 = new JLabel(); 
 		jLabel7 = new JLabel(); 
 		final JList<String> jList2 = new JList<String>(new DefaultListModel<String>());
 		jScrollPane2 = new JScrollPane(); 
 		jTextArea1 = new JTextArea(); 
 		jScrollPane3 = new JScrollPane(); 
 		jButton3 = new JButton(); 
 		contentPane = (JPanel)this.getContentPane();
 		ArrayList<String> arr = new ArrayList<String>();
 		jPanel4 = new JPanel();
 		for(int i = 0; i < 20; i++)
 		{
 			arr.add(String.valueOf(i));
 		}

 		jLabel2.setText("Welcome to the GoogleBot. This program will help you find out just how much is out there about you."); 
 		jLabel3.setText("Results"); 
 		jLabel4.setText("Please Enter the most important information about yourself."); 
 		jLabel7.setText("Make sure to separate all of these attributes with commas \" , \"" ); 
 		
 		jList2.addListSelectionListener(new ListSelectionListener() { 
 			public void valueChanged(ListSelectionEvent e) 
 			{ 
 				// makes sure only 1 is selected at a time and doesn't double tap the result
 				if(!e.getValueIsAdjusting()) 
 	 	 		{ 
 					Object object1 = jList2.getSelectedValue();
 			    	String string1 = object1.toString();
 			    	System.out.println(string1);
 	 	 		}
 			}
 		}); 
 		
 		jScrollPane2.setViewportView(jList2); 
 		jTextArea1.setText("Put your full name first then attributes. (e.g. John Smith, Eagle Scout, DePauw University, Computer Science Major)"); 
 		jScrollPane3.setViewportView(jTextArea1); 
 		jButton3.setText("Search"); 
 		jButton3.addActionListener(new ActionListener() { 
 			public void actionPerformed(ActionEvent e) 
 			{
 				// clear
 			    jList2.setModel(new DefaultListModel<String>());
 				String string1 = jTextArea1.getText(); // get the information
 				String[] strings = string1.split(","); // split it by commas 
 				
 				if(DeveloperMode == true)
 				{
 					System.out.println(string1);
 					System.out.println(strings[1].trim());
 				}
 				
 				Attributes = strings; // set the global attributes
			    query = strings[0];
			    
 				try {
					search();
				} catch (IOException e1) {
					((DefaultListModel<String>)jList2.getModel()).addElement("It appears something went wrong with the program. Error 1");
					e1.printStackTrace();
				}
 				getWebPages();
 				sort();
 				
			    for(int i = 0; i < array.size(); i++)
 				{
			    	if(!(array.get(i).getAttributes() == 0))
					{
 					((DefaultListModel<String>)jList2.getModel()).addElement("Matching Attributes: " + array.get(i).getAttributes() + "  Title: " + array.get(i).getTitle() + 
 							"  URL: " + array.get(i).getUrl());
					}
 				}
 			} 
  
 		}); 
 		 
 		contentPane.setLayout(null); 
 		addComponent(contentPane, jLabel2, 33,20,703,18); 
 		addComponent(contentPane, jLabel3, 358,179,60,18); 
 		addComponent(contentPane, jLabel4, 28,91,299,18); 
 		addComponent(contentPane, jLabel7, 27,166,301,18); 
 		addComponent(contentPane, jScrollPane2, 358,198,424,459); 
 		addComponent(contentPane, jScrollPane3, 25,198,295,268); 
 		addComponent(contentPane, jButton3, 234,472,83,28); 
 		addComponent(contentPane, jPanel4, 2,1,32767,0); 

 		jPanel4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); 

 		this.setName("GoogleBot"); 
 		this.setLocation(new Point(0, 0)); 
 		this.setSize(new Dimension(800, 800)); 
 	} 
 	
 	/** Add Component Without a Layout Manager (Absolute Positioning) */ 
 	private void addComponent(Container container,Component c,int x,int y,int width,int height) 
 	{ 
 		c.setBounds(x,y,width,height); 
 		container.add(c); 
 	}  
  
 	private void jButton3_actionPerformed(ActionEvent e) 
 	{ 
 		System.out.println("\njButton3_actionPerformed(ActionEvent e) called."); 
 	} 
	
	public static void main(String[] args) throws IOException 
	{
		// initialize variables
		array = new ArrayList<webPageObject>();	
		
		JFrame.setDefaultLookAndFeelDecorated(true); 
 		JDialog.setDefaultLookAndFeelDecorated(true); 
 		try 
 		{ 
 			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); 
 		} 
 		catch (Exception ex) 
 		{ 
 			System.out.println("Failed loading L&F: "); 
 			System.out.println(ex); 
 		} 
 		new GoogleBotMain();
		
		// main components
//		getQuery();
//		search();
//		getWebPages();
//		sort();
//		print();
	}
}
