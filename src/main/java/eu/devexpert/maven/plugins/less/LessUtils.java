package eu.devexpert.maven.plugins.less;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public class LessUtils {
	public static String compileLess(String absolutePath, Map<String, String> lessFiles) {
		System.out.println("compiling " + absolutePath);
		String less = resolveImports(absolutePath, lessFiles);

		JsHelper jsHelper = JsHelper.getInstance();
		return jsHelper.compileLess(less);
	}

	public static Map<String, String> readFiles(Collection<File> files) {
		Map<String, String> result = new HashMap<String, String>();
		for (File file : files) {
			try {
				String content = FileUtils.readFileToString(file);
				String absolutePath = file.getAbsolutePath();
				result.put(absolutePath, content);
			} catch (IOException e) {
				System.err.println("failed to read file: " + file);
			}
		}
		return result;
	}

	public static String resolveImports(String filePath, Map<String, String> lessFiles) {
		String content = lessFiles.get(filePath);
		if (content == null) {
			System.out.println("file not found: " + filePath);
			return null;
		}

		if (StringUtils.isBlank(content)) {
			System.out.println(filePath + " is empty.");
			return "";
		}

		Pattern importStatementPattern = Pattern.compile("@import\\s*\".+\"\\s*;*");
		Pattern importedFilePattern = Pattern.compile("\".*\"");

		Matcher importStatementMatcher = importStatementPattern.matcher(content);
		while (importStatementMatcher.find()) {
			String importStatement = content.substring(importStatementMatcher.start(), importStatementMatcher.end());

			Matcher importedFileMatcher = importedFilePattern.matcher(importStatement);
			importedFileMatcher.find();
			String importedFile = importStatement.substring(importedFileMatcher.start() + 1, importedFileMatcher.end() - 1);
			if (!importedFile.endsWith(".less")) {
				importedFile = importedFile + ".less";
			}

			importedFile = FilenameUtils.getFullPath(filePath) + importedFile;
			importedFile = FilenameUtils.normalize(importedFile);

			content = content.substring(0, importStatementMatcher.start()) + " " + resolveImports(importedFile, lessFiles) + " " + content.substring(importStatementMatcher.end());
			importStatementMatcher = importStatementPattern.matcher(content);
		}

		return content;
	}

	public static void main(String[] args) {
		compile(args, null);
	}

	public static void compile(String[] args, String output) {
		String dirParameter = null;

		if (args.length < 1)
			dirParameter = ".";
		else {
			dirParameter = args[0];
		}

		File dir = new File(dirParameter);
		if (!dir.exists()) {
			System.err.println("specified directory not found.");
		}

		String dirNormalized = FilenameUtils.normalize(dir.getAbsolutePath());
		System.out.println("DIR: "+dirNormalized);

		Collection<File> files = FileUtils.listFiles(new File(dirNormalized), new String[] { "less" }, true);
		Map<String, String> lessFiles = readFiles(files);
		for (Map.Entry entry : lessFiles.entrySet()) {
			String lessFileWithPath = (String) entry.getKey();
			String css = compileLess(lessFileWithPath, lessFiles);
			if (StringUtils.isEmpty(css)) {
				continue;
			}
			String absolutePath = FilenameUtils.getFullPath(lessFileWithPath);
			String basename = FilenameUtils.getBaseName(lessFileWithPath);

			String cssFile = absolutePath + basename + ".css";
			if (output != null) {
				String source = output + basename + ".css";
				try {
					System.out.println("writing cssXXX to " + source);
					FileUtils.writeStringToFile(new File(source), css);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				System.out.println("writing css to " + cssFile);
				FileUtils.writeStringToFile(new File(cssFile), css);
			} catch (IOException e) {
				System.err.println("failed to write to file: " + cssFile);
			}
		}

	}
}