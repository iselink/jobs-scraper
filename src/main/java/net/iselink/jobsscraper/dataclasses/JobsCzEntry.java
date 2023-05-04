package net.iselink.jobsscraper.dataclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.iselink.jobsscraper.utils.Utils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry for job item.
 */
public class JobsCzEntry extends BaseEntry {

	@Expose
	@SerializedName("title")
	private String title = null;

	@Expose
	@SerializedName("secondary_info")
	private List<String> secInfo = new ArrayList<>();

	@Expose
	@SerializedName("tags")
	private List<String> tags = new ArrayList<>();

	@Expose
	@SerializedName("salary")
	private String salary = null;

	@Expose
	@SerializedName("status")
	private String status = null;

	public JobsCzEntry(String link) {
		super(link);
	}

	public static JobsCzEntry fromElement(Element element) {

		Elements titleLink = element.getElementsByClass("search-list__main-info__title__link");
		if (titleLink.isEmpty()) {
			return null;
		}

		Elements secInfo = element.getElementsByClass("grid__item e-20--desk search-list__secondary-info");
		if (secInfo.isEmpty())
			return null;

		Elements tags = element.getElementsByClass("search-list__tags__default");

		JobsCzEntry e = new JobsCzEntry(Utils.cleanUrl(titleLink.get(0).attr("href")));
		e.title = titleLink.get(0).text();
		secInfo.forEach(element1 -> {
			element1.getElementsByClass("search-list__secondary-info").forEach(element2 -> {
				e.secInfo.add(element2.text());
			});
		});

		tags.forEach(element1 -> {
			e.tags.add(element1.text());
		});

		Elements salaryElement = element.getElementsByClass("search-list__tags__label search-list__tags__salary--label");
		salaryElement.forEach(element1 -> {
			e.salary = element1.text();
		});

		Elements statusElements = element.getElementsByClass("search-list__status__data");
		if (!statusElements.isEmpty()) {
			e.status = statusElements.get(0).text();
		}

		return e;
	}

	public String getTitle() {
		return title;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder()
				.append(title)
				.append('\n').append('\t').append(link)
				.append('\n').append('\t').append("Secondary info:");
		secInfo.forEach(s -> {
			builder.append("\n\t\t").append(s);
		});
		builder.append('\n').append('\t').append("Tags:");
		tags.forEach(s -> {
			builder.append("\n\t\t").append(s);
		});

		if (salary != null) {
			builder.append("\n\tSalary:").append(salary);
		}
		builder.append("\n\tStatus:").append(status);

		return builder.toString();
	}
}
