package net.iselink.jobsscraper.scrapers;

import java.util.List;

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
