package net.iselink.jobsscraper.dataclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Item entry for prace.cz
 */
public class PraceCzEntry extends BaseEntry {

	@Expose
	@SerializedName("title")
	private final String title;

	@Expose
	@SerializedName("salary")
	private final String salary;

	@Expose
	@SerializedName("location")
	private final String location;

	@Expose
	@SerializedName("company")
	private final String company;

	@Expose
	@SerializedName("employment_type")
	private final String employmentType;

	@Expose
	@SerializedName("time")
	private final String time;


	public PraceCzEntry(String link, String title, String salary, String location, String company, String employmentType, String time) {
		super(link);
		this.title = title;
		this.salary = salary;
		this.location = location;
		this.company = company;
		this.employmentType = employmentType;
		this.time = time;
	}

	public String getTitle() {
		return title;
	}
}
