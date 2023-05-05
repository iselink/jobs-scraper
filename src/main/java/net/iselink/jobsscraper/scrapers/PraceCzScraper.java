package net.iselink.jobsscraper.scrapers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.iselink.jobsscraper.dataclasses.PraceCzEntry;
import net.iselink.jobsscraper.utils.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class PraceCzScraper extends Scraper<PraceCzEntry> {

	@Expose
	@SerializedName("address")
	private String address;

	public PraceCzScraper(String url) {
		address = url;
	}

	@Override
	protected void onScrape() {
		try {
			int page = 0;
			boolean np;
			do {
				np = scrapePage(++page);
			} while (np);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean scrapePage(int index) throws IOException {
		String page = index > 1 ? "&page=" + index : "";
		Document doc = Jsoup
				.connect(address + page)
				.get();
		Elements searchElement = doc.getElementsByClass("search-result list-unstyled");
		Element elementList = searchElement.get(0);
		Elements entries = elementList.getElementsByClass("search-result__advert");
		entries.forEach(element -> {
			if (element.id().length() > 0 || !element.className().equalsIgnoreCase("search-result__advert")) {
				return;
			}

			Element titleElement = element.getElementsByClass("link").get(0);
			String title = titleElement.text();
			String link = Utils.cleanUrl(titleElement.attr("href"));
			String salary = "";
			String location = "";    //search-result__advert__box__item--location
			String company = "";    //search-result__advert__box__item--company
			String employmentType = "";    //search-result__advert__box__item--employment-type
			String time = "";    //search-result__advert__valid-from

			if (element.getElementsByClass("search-result__advert__box__item--salary").first() != null)
				salary = element.getElementsByClass("search-result__advert__box__item--salary").first().text();

			Elements locations = element.getElementsByClass("search-result__advert__box__item--location");
			if (!locations.isEmpty()) {
				location = locations.first().text();
			}

			Elements companies = element.getElementsByClass("search-result__advert__box__item--company");
			if (!companies.isEmpty()) {
				company = companies.first().text();
			}

			Elements employmentTypes = element.getElementsByClass("search-result__advert__box__item--employment-type");
			if (!employmentTypes.isEmpty()) {
				employmentType = employmentTypes.first().text();
			}
			Elements times = element.getElementsByClass("search-result__advert__valid-from");
			if (!times.isEmpty()) {
				time = times.first().text();
			}


			this.items.add(new PraceCzEntry(link, title, salary, location, company, employmentType, time));
		});

		return !doc.getElementsByClass("pager__next").isEmpty();
	}

}
