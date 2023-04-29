package net.iselink.jobsscraper.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits code in codeblocks into multiple codeblocks if character count reaches threshold.
 */
public class CodeSplitter {

	private final List<String> contents = new ArrayList<>();
	private final int threshold;
	private final String language;
	private final String begin = "```";
	private final String end = "```";
	private StringBuilder builder = new StringBuilder();

	public CodeSplitter(int threshold, String language) {
		this.threshold = threshold;
		this.language = language;
	}

	public void addLine(String line) {
		if (builder == null || builder.isEmpty()) {
			builder = new StringBuilder(begin).append(language);
		} else if (builder.length() + line.length() + getBlockLength() > this.threshold) {
			builder.append('\n').append(end);
			contents.add(builder.toString());
			builder = new StringBuilder(begin).append(language);
		}

		builder.append('\n').append(line);
	}

	public void addLine(String... line) {
		StringBuilder b = new StringBuilder();
		for (String s : line) {
			b.append(s);
		}
		addLine(b.toString());
	}

	public List<String> getList() {
		if (builder != null && !builder.isEmpty()) {
			builder.append('\n').append(end);
			contents.add(builder.toString());
			builder = null;
		}


		return contents;
	}

	private int getBlockLength() {
		return language.length() + begin.length() + end.length();
	}


}
