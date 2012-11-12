package eu.devexpert.maven.plugins;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.phloc.css.ECSSVersion;
import com.phloc.css.decl.CSSDeclaration;
import com.phloc.css.decl.CSSSelector;
import com.phloc.css.decl.CSSStyleRule;
import com.phloc.css.decl.CascadingStyleSheet;
import com.phloc.css.reader.CSSReader;
import com.phloc.css.writer.CSSWriter;
import com.phloc.css.writer.CSSWriterSettings;

import eu.devexpert.maven.plugins.api.Optimizer;

@Optimizer(id = "optimizer")
@Mojo(name = "optimizer", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
@Execute(goal = "optimizer", phase = LifecyclePhase.COMPILE)
public class CssOptimizer extends CssMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		System.out.println("OPTIMIZE");
		CSSWriterSettings settings = new CSSWriterSettings(ECSSVersion.CSS30);
		CSSWriter writer = new CSSWriter(settings);
		writer.setHeaderText("Generated by PARSER-CSS(www.devexpert.eu)");
		CascadingStyleSheet css = CSSReader.readFromFile(new File(sourcecss), "UTF-8", ECSSVersion.CSS30);
		CascadingStyleSheet newCss = new CascadingStyleSheet();

		List<CSSStyleRule> rules = css.getAllStyleRules();

		for (CSSStyleRule cssStyleRule : rules) {
			CSSStyleRule existingRule = null;
			if ((existingRule = getExistingRule(newCss.getAllStyleRules(), cssStyleRule)) != null) {
				newCss.removeRule(existingRule);
				for (CSSSelector selector : cssStyleRule.getAllSelectors()) {
					existingRule.addSelector(selector);
				}
				newCss.addRule(existingRule);
			} else {
				newCss.addRule(cssStyleRule);
			}
		}

		try {
			writer.writeCSS(newCss, new FileWriter(new File(outputcss)));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private CSSStyleRule getExistingRule(List<CSSStyleRule> optimizedRules, CSSStyleRule oldRule) {
		for (CSSStyleRule cssStyleRule : optimizedRules) {
			boolean areEquals = true;
			for (CSSDeclaration optimizedDeclaration : cssStyleRule.getAllDeclarations()) {
				for (CSSDeclaration declaration : oldRule.getAllDeclarations()) {
					if (!optimizedDeclaration.equals(declaration)) {
						areEquals = false;
						break;
					}
				}
				if (!areEquals) {
					break;
				}
			}
			if (areEquals) {
				return cssStyleRule;
			}
		}
		return null;
	}

}
