package net.iselink.jobsscraper.scrapers;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.iselink.jobsscraper.dataclasses.BaseEntry;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all scrapers used through whole program.
 */
public abstract class Scraper<T extends BaseEntry> {

	/**
	 * Class name of the scraper which has been used for scraping these entries.
	 */
	@Expose
	@SerializedName("scraper")
	public final String type = getClass().getSimpleName();

	/**
	 * List of job offers from page.
	 */
	@Expose
	@SerializedName("items")
	protected final List<T> items = new ArrayList<>();

	@Expose
	@SerializedName("timestamp")
	private String timestamp = null;

	/**
	 * Perform scraping.
	 * Blocks thread and set timestamp.
	 */
	//TODO: make return future or some kind of processing parallelization
	public final void scrape() {
		timestamp = Instant.now().toString();
		onScrape();
	}

	/**
	 * Internal method for performing actual scraping.
	 * Executed after updating timestamp.
	 */
	protected abstract void onScrape();

	/**
	 * Load entries from file, useful in comparation for loading older entries..
	 * File must be a valid JSON.
	 *
	 * @param filename Name of the file.
	 * @throws IOException
	 */
	public void loadFromFile(String filename) throws IOException {
		try (FileReader reader = new FileReader(filename)) {
			new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(reader, getClass());
		}
	}

	/**
	 * Save current entries to the JSON file.
	 * This will also store some metadata about scraper itself, including address and timestamp.
	 *
	 * @param filename name of the file.
	 * @throws IOException
	 */
	public void saveToFile(String filename) throws IOException {
		try (FileWriter writer = new FileWriter(filename)) {
			new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this, getClass(), writer);
		}
	}


	/**
	 * Compare current scraped list with different list.
	 * Specified list will be treated as older list.
	 * Which mean items missing in scraper and present in param list will be treated as removed.
	 *
	 * @param list List to compare
	 * @return Returns comparation of both lists.
	 */
	public Comparation<T> compareWith(List<T> list) {
		//this is the place, where time complexity dies
		//added list - items in current list, which are not in second list
		//removed list - items missing in current list, which are in second list
		//unchanged list - items which are in both lists
		List<T> addedList = new ArrayList<>();
		List<T> removedList = new ArrayList<>();
		List<T> unchagedList = new ArrayList<>();

		//added
		for (T currentItem : items) {
			boolean found = false;
			for (T secondItem : list) {
				if (currentItem.getLink().equalsIgnoreCase(secondItem.getLink())) {
					found = true;
					break;
				}
			}

			if (!found) {
				addedList.add(currentItem);
			}
		}

		//removed
		for (T secondItem : list) {
			boolean found = false;
			for (T currentItem : items) {
				if (currentItem.getLink().equalsIgnoreCase(secondItem.getLink())) {
					found = true;
					break;
				}
			}

			if (!found) {
				removedList.add(secondItem);
			}
		}

		//unchanged
		for (T currentItem : items) {
			for (T secondItem : list) {
				if (currentItem.getLink().equalsIgnoreCase(secondItem.getLink())) {
					unchagedList.add(currentItem);
				}
			}
		}

		return new Comparation<T>(unchagedList, addedList, removedList);
	}

	public List<T> getItems() {
		return items;
	}

	public String getTimestamp() {
		return timestamp;
	}
}
