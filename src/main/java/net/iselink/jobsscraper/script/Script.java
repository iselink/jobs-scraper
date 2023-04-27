package net.iselink.jobsscraper.script;

import net.iselink.jobsscraper.script.classes.ProgramJs;
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
		try (Context cx = Context.enter()) {
			cx.setLanguageVersion(Context.VERSION_ES6);
			ScriptableObject scope = cx.initStandardObjects();

			ScriptableObject.putProperty(scope, "out", Context.javaToJS(System.out, scope));
			ScriptableObject.putProperty(scope, "err", Context.javaToJS(System.err, scope));

			ScriptableObject.defineClass(scope, ProgramJs.class);

			cx.evaluateString(scope, content, source, 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
