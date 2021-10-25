package org.alias.annotation.internal.processor;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

class AnnotationProcessorExecutor {

	private final JavaCompiler compiler;
	private File outputDirectory;
	private final Collection<JavaFileObject> compilationUnits = new ArrayList<>();
	private final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();


	public static final AnnotationProcessorExecutor compileSourceFiles(JavaFileObject... sources) {
		return new AnnotationProcessorExecutor(ToolProvider.getSystemJavaCompiler()).addSourceFiles(asList(sources));
	}

	protected AnnotationProcessorExecutor(JavaCompiler compiler) {
		this.compiler = compiler;
	}

	private AnnotationProcessorExecutor addSourceFiles(Collection<? extends JavaFileObject> sources) {
		this.compilationUnits.addAll(sources);
		return this;
	}

	public AnnotationProcessorExecutor addSourceFile(JavaFileObject source) {
		this.compilationUnits.add(source);
		return this;
	}

	public AnnotationProcessorExecutor to(File outputDirectory) {
		this.outputDirectory = outputDirectory;
		return this;
	}

	public void executeProcessor(Processor processor) throws IOException {
		if (!isExecutionWithProcessorSucessful(processor)) {
			printDiagnosticsTo(System.err);
			throw new IllegalStateException("Compilation not successful");
		}
	}

	public void expectErrorFrom(Processor processor) throws IOException {
		if (isExecutionWithProcessorSucessful(processor)) {
			throw new IllegalStateException("Compilation error expected");
		}
		printDiagnosticsTo(System.out);
	}

	private boolean isExecutionWithProcessorSucessful(Processor processor) throws IOException {
		JavaFileManager fileManager = getJavaFileManager();
		CompilationTask task = getCompilationTask(fileManager);
		task.setProcessors(asList(processor));
		return task.call();
	}

	private JavaFileManager getJavaFileManager() throws IOException {
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, asList(outputDirectory));
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, asList(outputDirectory));
		return fileManager;
	}

	private CompilationTask getCompilationTask(JavaFileManager fileManager) {
		List<String> options = new ArrayList<String>();
		// options.addAll(Arrays.asList("-classpath", System.getProperty("java.class.path")));
		// options.add("-verbose");
		return compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
	}

	private void printDiagnosticsTo(PrintStream printStream) {
		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
			printStream.println(diagnostic);
		}
		printStream.flush();
	}
}