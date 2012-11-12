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
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FileOptimizer implements FileVisitor<Path> {
	private static final String	ENCODING	= "UTF-8";
	private String				basedir;
	private String				outputdir;
	private Map<String, String>	rulesToReplace;
	private List<String>		mimeTypes;
	private String				classAttribute;

	public FileOptimizer(String basedir, String outputdir, List<String> mimeTypes, String classAttribute, Map<String, String> rulesToReplace) {
		this.basedir = basedir;
		this.outputdir = outputdir;
		this.rulesToReplace = rulesToReplace;
		this.mimeTypes = mimeTypes;
		this.classAttribute = classAttribute;
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
					Document doc = Jsoup.parse(file.toFile(), ENCODING);
					Elements elements = doc.getElementsByAttribute(classAttribute);
					for (Element element : elements) {
						List<String> newClassList = new ArrayList<String>();
						String[] classes = element.attr(classAttribute).split(" ");

						for (String cls : classes) {
							boolean found = false;
							for (String cssName : rulesToReplace.keySet()) {
								if (cssName.equalsIgnoreCase(cls)) {
									newClassList.add(rulesToReplace.get(cssName));
									found = true;
									break;
								}
							}
							if (!found) {
								newClassList.add(cls);
							}
						}
						String styleClassValue = "";
						for (String clazz : newClassList) {
							styleClassValue += clazz;
							styleClassValue += " ";
						}
						styleClassValue = styleClassValue.substring(0, styleClassValue.length() - 1);
						element.attr(classAttribute, styleClassValue);
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
				}
			}
		}
		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return null;
	}

	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

}
