package net.iselink.jobsscraper.script.classes;

import net.iselink.jobsscraper.scrapers.JobsCzScraper;
import net.iselink.jobsscraper.utils.Configuration;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Class implementing program-wide functions and general helpful functions which makes easy to write scripts.
 * Also, useful if rhino has been initiated with safe objects (i.e. unable to use Packets like this: new Packages.net.iselink.jobsscraper.Scraper('nachod', [''], 'high', 40);)
 */
public class ProgramJs extends ScriptableObject {

	@JSStaticFunction
	public static Configuration loadConfig(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IOException {
		if (args.length != 1) {
			Context.reportError("Invalid arg count.");
			return null;
		}

		return Configuration.loadConfig((String) args[0]);
	}

	@JSStaticFunction
	public static JobsCzScraper newScraper(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IOException {
		if (args.length != 4) {
			Context.reportError("Invalid arg count.");
			return null;
		}
		return new JobsCzScraper(
				(String) Context.jsToJava(args[0], String.class),
				(String[]) Context.jsToJava(args[1], String[].class),
				(String) Context.jsToJava(args[2], String.class),
				(Integer) Context.jsToJava(args[3], Integer.class));
	}

	@JSStaticFunction
	public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		String builder = Arrays
				.stream(args)
				.map(o -> Context.jsToJava(o, Object.class).toString()).collect(Collectors.joining());
		System.out.println(builder);
	}

	@Override
	public String getClassName() {
		return "Program";
	}
}
