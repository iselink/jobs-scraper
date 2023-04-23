package net.iselink.jobsscraper.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ArgumentParser {

	private final Map<String, String> flagsWithValue = new HashMap<>();
	private final Set<String> flags = new HashSet<>();

	public void parse(String[] args) {

		for (String argument : args) {
			if (argument.startsWith("--")) {
				int divider = argument.indexOf('=');
				if (divider == -1) {
					//flag without value
					flags.add(argument.substring(2));
				} else {
					//flag with value
					flagsWithValue.put(argument.substring(2, divider), argument.substring(divider + 1));
				}
			}
		}
	}

	public String getValue(String name) {
		return flagsWithValue.get(name);
	}

	public String getValue(String name, String defValue) {
		return flagsWithValue.getOrDefault(name, defValue);
	}

	public boolean hasFlag(String name) {
		return flagsWithValue.containsKey(name) || flags.contains(name);
	}


	public Map<String, String> getFlagsWithValue() {
		return flagsWithValue;
	}

	public Set<String> getFlags() {
		return flags;
	}
}
