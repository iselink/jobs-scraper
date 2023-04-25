package net.iselink.jobsscraper.script;

import net.iselink.jobsscraper.Scraper;
import net.iselink.jobsscraper.utils.Configuration;
import net.iselink.jobsscraper.utils.DiscordWebHook;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class Script {

	private String source = "<?>";
	private String content = "";

	public Script(String source, String content) {
		this.source = source;
		this.content = content;
	}

	public static Script loadFromFile(String filename) throws IOException {
		String content = Files.readString(new File(filename).toPath(), StandardCharsets.UTF_8);
		return new Script(filename, content);
	}

	public void execute() {
		Context cx = Context.enter();
		try {
			cx.setLanguageVersion(Context.VERSION_ES6);
			ScriptableObject scope = cx.initSafeStandardObjects();

//			Object webHook = Context.javaToJS(DiscordWebHook.class, scope);
//			ScriptableObject.putProperty(scope, "WebHook", webHook);
//
//			Object configuration = Context.javaToJS(Configuration.class, scope);
//			ScriptableObject.putProperty(scope, "Configuration", configuration);
//
//			Object scraper = Context.javaToJS(Scraper.class, scope);
//			ScriptableObject.putProperty(scope, "Scraper", scraper);

			Object out = Context.javaToJS(System.out, scope);
			ScriptableObject.putProperty(scope, "out", out);

			cx.evaluateString(scope, content, source, 1, null);
		} finally {
			cx.close();
		}
	}
}
