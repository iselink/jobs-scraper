package net.iselink.jobsscraper.scrapers;

import java.util.List;

/**
 * Result of comparation between items from scraper.
 * Added/removed items depends on which list is used for comparation.
 * For more info see {@link Scraper}'s compareWith method.
 *
 * @param unchangedList List of unchanged items.
 * @param addedList     List of newly added items.
 * @param removedList   List of removed items.
 * @param <T>           Held type.
 */
public record Comparation<T>(List<T> unchangedList, List<T> addedList, List<T> removedList) {

	public int getUnchangedCount() {
		return unchangedList.size();
	}

	public int getAddedCount() {
		return addedList.size();
	}

	public int getRemovedCount() {
		return removedList.size();
	}


}
