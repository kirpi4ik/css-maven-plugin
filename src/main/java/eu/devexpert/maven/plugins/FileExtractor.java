package eu.devexpert.maven.plugins;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FileExtractor implements FileVisitor<Path> {
	private static final String				ENCODING	= "UTF-8";
	private static final String				STYLE		= "style";
	private Map<Path, Map<String, String>>	paths		= new HashMap<Path, Map<String, String>>();
	private List<String>					mimeTypes;
	private String							classAttribute;
	private String							classPrefix;
	private boolean							cssRulePrefixUseFileName;
	private Map<String, List<String>>		classesMap	= new HashMap<String, List<String>>();
	private String							outputdir;
	private String							basedir;

	public FileExtractor(List<String> mimeTypes, String classPrefix, String classAttribute, boolean cssRulePrefixUseFileName, String basedir, String outputdir) {
		super();
		this.mimeTypes = mimeTypes;
		this.classAttribute = classAttribute;
		this.classPrefix = classPrefix;
		this.cssRulePrefixUseFileName = cssRulePrefixUseFileName;
		this.basedir = basedir;
		this.outputdir = outputdir;
	}

	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		String mimeType = Files.probeContentType(file);
		if (mimeTypes != null) {
			for (String acceptedMimeType : mimeTypes) {
				if (mimeType != null && mimeType.equalsIgnoreCase(acceptedMimeType)) {
					System.out.println("Processing file: " + file);
					Map<String, String> styles = new HashMap<String, String>();
					String nameTemp = file.getFileName().toString();
					String fileName = nameTemp.substring(0, nameTemp.lastIndexOf('.')).replaceAll("\\.", "-").replaceAll("_", "-");
					Document doc = Jsoup.parse(file.toFile(), ENCODING);
					Elements elements = doc.getElementsByAttribute(STYLE);
					for (Element element : elements) {
						String style = element.attr(STYLE);
						if (style.indexOf("#{") == -1) {
							String id = element.attr("id");
							String cssName = classPrefix + (cssRulePrefixUseFileName ? fileName + "-" : "")
									+ ((id != null && id.length() > 0) ? id : RandomStringUtils.randomAlphabetic(5).toLowerCase());
							styles.put(cssName, style);
							String[] clss = element.attr(classAttribute).split(" ");
							for (String cls : clss) {
								if (classesMap.get(cls) == null) {
									classesMap.put(cls, new ArrayList<String>());
								}
								classesMap.get(cls).add(cssName);
							}
							String existingClass = element.attr(classAttribute);
							if (existingClass.length() > 0) {
								existingClass += " ";
							}
							element.attr(classAttribute, element.attr(classAttribute) + cssName);
							element.removeAttr(STYLE);
						}

					}

					String path = file.toString().substring(basedir.length());
					File f = new File(outputdir + path);
					if (!f.exists()) {
						if (!f.getParentFile().exists()) {
							f.getParentFile().mkdirs();
						}
						f.createNewFile();
					}
					FileWriter fw = new FileWriter(f);
					fw.write(doc.toString());
					fw.flush();

					paths.put(file, styles);
				}
			}
		}
		return FileVisitResult.CONTINUE;
	}

	public Map<String, List<String>> getClassesMap() {
		return classesMap;
	}

	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return null;
	}

	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	public Map<Path, Map<String, String>> getPaths() {
		return paths;
	}

}
