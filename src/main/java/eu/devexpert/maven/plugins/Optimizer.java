package eu.devexpert.maven.plugins;

import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.reflections.Reflections;

@Mojo(name = "optimize", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
@Execute(goal = "optimize", phase = LifecyclePhase.COMPILE)
public class Optimizer extends CssMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		Reflections reflections = new Reflections("org.grossbook.sandbox", this.getClass().getClassLoader());
		Set<Class<?>> annotatedOptimimizers = reflections.getTypesAnnotatedWith(eu.devexpert.maven.plugins.api.Optimizer.class);
		for (String optimizerId : optimizers) {
			for (Class<?> clazz : annotatedOptimimizers) {
				String id = clazz.getAnnotation(eu.devexpert.maven.plugins.api.Optimizer.class).id();
				if (optimizerId.equalsIgnoreCase(id)) {
					try {
						CssMojo mojo = (CssMojo) clazz.newInstance();
						mojo.initialize(sourcedir, outputdir, sourcecss, outputcss, classAttribute, cssRulePrefix, mimeTypes, cssRulePrefixUseFileName);
						mojo.execute();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
