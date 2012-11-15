package eu.devexpert.maven.plugins.less;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public class JsHelper {
	private static JsHelper	instance;
	private Context			ctx;
	private Scriptable		scope;

	private JsHelper() {
		this.ctx = ContextFactory.getGlobal().enterContext();
		this.ctx.setOptimizationLevel(-1);
		this.ctx.setLanguageVersion(170);

		this.scope = this.ctx.initStandardObjects();

		loadInternalJsResource("env.rhino.js", "env.rhino.js");
		loadInternalJsResource("less.js", "less.js");
	}

	public String compileLess(String less) {
		if (null == less) {
			throw new IllegalArgumentException("less must not be null.");
		}

		if (StringUtils.isBlank(less)) {
			return "";
		}

		this.scope.put("lessSourceCode", this.scope, less);
		this.scope.put("result", this.scope, "");

		String js = "var result; var p = new less.Parser(); p.parse(lessSourceCode, function(e, tree){ result=tree.toCSS(); });";
		this.ctx.evaluateString(this.scope, js, "compileLess.js", 1, null);

		Object result = this.scope.get("result", this.scope);
		String css = null;
		if ((result instanceof Undefined))
			System.err.println("result is undefined");
		else {
			css = result.toString();
		}

		this.scope.put("result", this.scope, "");
		return css;
	}

	private void loadInternalJsResource(String name, String relativePath) {
		System.out.println("loading internal js resource: " + relativePath);
		try {
			InputStream is = this.getClass().getResourceAsStream(relativePath);
			Reader reader = new InputStreamReader(is);
			this.ctx.evaluateReader(this.scope, reader, name, 1, null);
		} catch (Exception e) {
			System.err.println("failed to load internal resource: " + name + "(" + this.getClass().getResource(relativePath) + ")");
		}
	}

	private void loadJsResource(String name, String path) {
		File f = new File(path);
		if (!f.exists()) {
			System.err.println("file not found: " + f);
			return;
		}
		try {
			Reader reader = new InputStreamReader(new FileInputStream(f));
			this.ctx.evaluateReader(this.scope, reader, name, 1, null);
		} catch (Exception e) {
			System.err.println("failed to load resource: " + name + "(" + path + ")");
		}
	}

	public static JsHelper getInstance() {
		if (instance == null) {
			instance = new JsHelper();
		}

		return instance;
	}
}