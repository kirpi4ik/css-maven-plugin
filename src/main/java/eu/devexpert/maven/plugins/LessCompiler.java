package eu.devexpert.maven.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import eu.devexpert.maven.plugins.less.LessUtils;

@Mojo(name = "less", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
@Execute(goal = "less", phase = LifecyclePhase.COMPILE)
public class LessCompiler extends CssMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			String[] s = { sourcedir };
			getLog().info("SEARCH IN " + sourcedir);
			LessUtils.compile(s, outputdir);
			for (Object key : getPluginContext().keySet()) {
				System.out.println("KEY=" + key + ", VALUE=" + getPluginContext().get(key));
			}
		} catch (Exception e) {
			getLog().error(e);
			throw new MojoExecutionException(null, "Error occured during application install", e.getMessage());
		}

	}

}
