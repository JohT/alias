package org.alias.annotation.internal.processor;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Ignore;

/**
 * Collects all files in the given root directory and provides them and their contents for unit testing.
 * 
 * @author JohT
 */
@Ignore
class FileCollector extends SimpleFileVisitor<Path> {

	private final File root;
	private final Map<String, Path> files = new HashMap<>();

	public static final FileCollector collectFilesFromRoot(File root) throws IOException {
		FileCollector fileCollector = new FileCollector(root);
		Files.walkFileTree(fileCollector.getRootPath(), fileCollector);
		return fileCollector;
	}

	FileCollector(File root) {
		this.root = root;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		files.put(withoutRoot(file), file);
		return super.visitFile(file, attrs);
	}

	public Path getRootPath() {
		return Paths.get(root.toURI());
	}

	private String withoutRoot(Path file) {
		return file.toFile().getAbsolutePath().replaceFirst(root.getAbsolutePath() + File.separator, "");
	}

	public void failOnMissing(String... pathSegments) {
		String filename = toFilename(pathSegments);
		if (!contains(filename)) {
			throw fileNotFound(filename);
		}
	}

	public InputStream inputStreamOf(String... pathSegments) {
		String filename = toFilename(pathSegments);
		try {
			return new FileInputStream(new File(root, filename));
		} catch (FileNotFoundException e) {
			throw fileNotFound(filename);
		}
	}

	private AssertionError fileNotFound(String filename) throws AssertionError {
		return new AssertionError(String.format("File %s not found in %s.", filename, files.keySet().toString()));
	}

	public String getAbsolutePathOf(String... pathSegments) {
		failOnMissing(pathSegments);
		return root.getAbsolutePath() + File.separator + toFilename(pathSegments);
	}

	private boolean contains(String filename) {
		return files.containsKey(filename);
	}

	public void assertContains(String expectedContents, String... pathSegments) throws IOException {
		String filecontents = contentsOf(pathSegments);
		String message = String.format("Expected content %s not found in:\n---\n%s\n---", expectedContents, filecontents);
		assertTrue(message, filecontents.contains(expectedContents));
	}

	public void assertHasUncommentedPropertyLineCountOf(long expectedLines, String... pathSegments) throws IOException {
		String content = contentsOf(pathSegments);
		long linecount = uncommentedLinesOf(content);
		String message = String.format("Expected line count of %d, but found %d in:\n---\n%s\n---", expectedLines, linecount, content);
		assertEquals(message, expectedLines, linecount);
	}

	public void assertFilesWithExtension(long expectedFiles, String extension) throws IOException {
		long filecount = countFilesWithExtension(extension);
		String text = "Expected %d file(s) with extension '%s', but found %d in:\n%s";
		String message = String.format(text, expectedFiles, extension, filecount, files.keySet().toString());
		assertEquals(message, expectedFiles, filecount);
	}

	private String contentsOf(String... pathSegments) throws IOException {
		failOnMissing(pathSegments);
		return new String(Files.readAllBytes(files.get(toFilename(pathSegments))));
	}

	public long uncommentedLinesOf(String filecontents) throws IOException {
		return countLinesThatFulfill(s -> !s.startsWith("#"), filecontents);
	}

	private long countLinesThatFulfill(Predicate<String> lineCondition, String filecontents) throws IOException {
		return asList(filecontents.split("\n")).stream()//
				.filter(s -> !s.startsWith("#"))
				.count();
	}

	public long countFilesWithExtension(String extension) {
		if (extension.startsWith(".")) {
			throw new IllegalArgumentException("Please provide the extension without the leading point character");
		}
		return files.keySet().stream().filter(name -> name.endsWith("." + extension)).count();
	}

	private String toFilename(String... pathSegments) {
		String filename = "";
		for (String path : pathSegments) {
			filename += filename.isEmpty() ? "" : File.separator;
			filename += path;
		}
		return filename;
	}

	@Override
	public String toString() {
		return "FileCollector [root=" + root + ", files=" + files + "]";
	}
}