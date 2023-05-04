package net.iselink.jobsscraper.dataclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Abstract class for job items.
 */
public abstract class BaseEntry {
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
