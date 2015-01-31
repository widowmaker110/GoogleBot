package widowmaker110.googleBot;

public class webPageObject {
	
	private String title, url;
	private int attributes;
	
	/**
	 * 
	 * @param titl title of the page String
	 * @param ur url of the page String
	 * @param attrib is the number of attributes found in the webpage. The higher the found attributes, the more likely
	 *  its a page the user wants.
	 */
	public webPageObject(String titl, String ur, int attrib)
	{	
		this.setUrl(ur);
		this.setTitle(titl);
		this.setAttributes(attrib);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getAttributes() {
		return attributes;
	}

	public void setAttributes(int attributes) {
		this.attributes = attributes;
	}
}
