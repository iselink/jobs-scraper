package net.iselink.jobsscraper.dataclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Abstract class for job items.
 */
public abstract class BaseEntry {

	/**
	 * Link to the page containing offer.
	 * Must be unique for every item as this is used for comparation.
	 */
	@Expose
	@SerializedName("link")
	protected String link = null;

	public BaseEntry(String link) {
		this.link = link;
	}

	public String getLink() {
		return link;
	}
}
