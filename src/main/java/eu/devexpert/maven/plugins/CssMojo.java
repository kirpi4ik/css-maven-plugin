package eu.devexpert.maven.plugins;

import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

public abstract class CssMojo extends AbstractMojo {

	@Parameter(required = true, readonly = true, property = "sourcedir")
	protected String			sourcedir;
	@Parameter(required = true, readonly = true, property = "outputdir")
	protected String			outputdir;
	@Parameter(required = false, readonly = true, property = "sourcecss")
	protected String			sourcecss;
	@Parameter(required = false, readonly = true, property = "outputcss")
	protected String			outputcss;
	@Parameter(required = false, readonly = true, property = "classAttribute")
	protected String			classAttribute;
	@Parameter(required = false, readonly = true, property = "cssRulePrefix")
	protected String			cssRulePrefix;
	@Parameter(required = false, readonly = true, property = "mimeTypes")
	protected List<String>		mimeTypes;
	@Parameter(required = false, readonly = true, property = "cssRulePrefixUseFileName")
	protected boolean			cssRulePrefixUseFileName;
	@Parameter(required = false, readonly = true, property = "optimizers")
	protected List<String>		optimizers;
	@Component
	protected MavenSession		session;

	@Component
	protected MavenProject		project;

	@Component
	protected MojoExecution		mojo;

	@Component
	protected PluginDescriptor	plugin;

	@Component
	protected Settings			settings;
	@Component
	protected PluginManager		manager;

	public void initialize(String sourcedir, String outputdir, String sourcecss, String outputcss, String classAttribute, String cssRulePrefix, List<String> mimeTypes,
			boolean cssRulePrefixUseFileName) {
		this.sourcedir = sourcedir;
		this.outputdir = outputdir;
		this.sourcecss = sourcecss;
		this.outputcss = outputcss;
		this.classAttribute = classAttribute;
		this.cssRulePrefix = cssRulePrefix;
		this.cssRulePrefixUseFileName = cssRulePrefixUseFileName;
		this.mimeTypes = mimeTypes;

	}
}
