package main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import repairer.FixCandidate;
import repairer.JMLAnnotatedClass;
import tools.JavaCompilerAPI;
import tools.TacoAPI;
import ar.edu.jdynalloy.JDynAlloySemanticException;
import ar.edu.taco.TacoNotImplementedYetException;
import config.StrykerConfig;

public class Main {

	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
		
		String sourceFolder = "src/test/resources/java/";
		String className = "roops.core.objects.SinglyLinkedListContainsBug7";
		String[] dependencies = new String[]{"roops.core.objects.SinglyLinkedListNode", "roops.core.objects.SinglyLinkedListContainsBug7"};
		String subjectMethod = "contains";
		
		JMLAnnotatedClass subjectClass = new JMLAnnotatedClass(sourceFolder, className);
		
		if (subjectClass==null || subjectMethod==null) throw new IllegalStateException("program or method is null");
		if (!subjectClass.isValid()) throw new IllegalStateException("program does not compile");
		
		FixCandidate fixCandidate = new FixCandidate(subjectClass, subjectMethod);
		
		Properties overridingProperties = TacoAPI.getInstance().getOverridingProperties();
		overridingProperties.put("classToCheck",fixCandidate.getProgram().getClassName());
		overridingProperties.put("methodToCheck",subjectMethod+"_0");
		overridingProperties.put("jmlParser.sourcePathStr", StrykerConfig.getInstance().getCompilingSandbox());
		overridingProperties.put("relevantClasses",mergedRelevantClasses(dependencies));
		overridingProperties.put("relevancyAnalysis", true);
		overridingProperties.put("useJavaArithmetic", true);
		overridingProperties.put("checkArithmeticException", false);
		
		
		// +++++++++++++++++++++++++++++++++++++++++++++++
		// create compilation sandbox
		String sandboxDir = StrykerConfig.getInstance().getCompilingSandbox();
		if (!createSandboxDir(sandboxDir)) {
			System.err.println("couldn't create compilation sandbox directory: " + sandboxDir);
			return;
		}
		if (!move(fixCandidate.getProgram().getSourceFolder(), sandboxDir, fixCandidate.getProgram().getFilePath())) {
			System.err.println("couldn't move compilation ambient to compilation sandbox directory");
			return;
		}
		// ------------------------------------------------
		
		if (fixCandidate.getProgram()==null) throw new IllegalArgumentException("null program in fix candidate");
		
		if (!copy(fixCandidate.getProgram().getFilePath(), StrykerConfig.getInstance().getCompilingSandbox() + fixCandidate.getProgram().getClassName().replaceAll("\\.", StrykerConfig.getInstance().getFileSeparator()) + ".java")) {
			System.err.println("couldn't copy " + fixCandidate.getProgram().getFilePath() + " to " + StrykerConfig.getInstance().getCompilingSandbox());
			return ;
		}
		
		String sourceFolderBackup = fixCandidate.getProgram().getSourceFolder();
		fixCandidate.getProgram().moveLocation(StrykerConfig.getInstance().getCompilingSandbox());
		
		String[] classpathToCompile = new String[]{StrykerConfig.getInstance().getCompilingSandbox()};
		if (!JavaCompilerAPI.getInstance().compile(StrykerConfig.getInstance().getCompilingSandbox() + fixCandidate.getProgram().getClassNameAsPath()+".java", classpathToCompile)) {
			System.err.println("error while compiling FixCandidate!");
			return;
		}
		
		URL[] urls = new URL[classpathToCompile.length];
		int i = 0;
		for (String path : classpathToCompile) {
			urls[i++] = (new File(path)).toURI().toURL();
		}
		URLClassLoader loader = new URLClassLoader(urls);
		loader.loadClass(fixCandidate.getProgram().getClassName());
//		JavaCompilerAPI.getInstance().updateReloaderClassPath(classpathToCompile);
//		JavaCompilerAPI.getInstance().reloadClass(fixCandidate.getProgram().getClassName());
		ClassLoader classLoaderBackup = Thread.currentThread().getContextClassLoader();
//		Thread.currentThread().setContextClassLoader(JavaCompilerAPI.getInstance().getReloader());
		Thread.currentThread().setContextClassLoader(loader);
		
		if (!fixCandidate.getProgram().isValid()) return;
		boolean error = false;
		boolean sat = false;
		try {
			sat = TacoAPI.getInstance().isSAT(fixCandidate);
		} catch (TacoNotImplementedYetException e) {
			error = true;
		} catch (JDynAlloySemanticException e) {
			error = true;
		}
		fixCandidate.getProgram().moveLocation(sourceFolderBackup);
		Thread.currentThread().setContextClassLoader(classLoaderBackup);
		deleteDir(sandboxDir);
	}
	
	
	private static boolean move(String sourceFolder, String sandboxDir, String ignoreFile) {
		final Path source = FileSystems.getDefault().getPath(sourceFolder);
		final Path target = FileSystems.getDefault().getPath(sandboxDir);
		final Path ignore = FileSystems.getDefault().getPath(ignoreFile);
		try {
			Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
					Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					Path targetdir = target.resolve(source.relativize(dir));
					try {
						Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
					    FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(perms);
						Files.createDirectory(targetdir, fileAttributes);
						//Files.copy(dir, targetdir);
					} catch (FileAlreadyExistsException e) {
						if (!Files.isDirectory(targetdir))
							throw e;
					} catch (AccessDeniedException e) {
						System.err.println("AccessDeniedException: " + e.getMessage());
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException {
					try {
						if (!file.toAbsolutePath().equals(ignore)) {
							Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-rw-rw-");
						    FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(perms);
							Path resolvedPath = target.resolve(source.relativize(file));
						    Files.createFile(resolvedPath, fileAttributes);
							Files.copy(file,target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
						}
								
					} catch (AccessDeniedException e) {
						System.err.println("AccessDeniedException: " + e.getMessage());
					}
					return FileVisitResult.CONTINUE;
				}
				
				
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static boolean deleteDir(String dir) {
		Path start = FileSystems.getDefault().getPath(dir);
		try {
			Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException e) throws IOException {
					if (e == null) {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					} else {
						// directory iteration failed
						throw e;
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean createSandboxDir(String sandboxDir) {
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
	    FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(perms);
		Path sandboxPath = FileSystems.getDefault().getPath(sandboxDir);
	    try {
			Files.createDirectory(sandboxPath, fileAttributes);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String mergedRelevantClasses(String[] relevantClasses) {
		String mrc = "";
		for (int d = 0; d < relevantClasses.length; d++) {
			mrc += relevantClasses[d];
			if (d + 1 < relevantClasses.length) {
				mrc += ",";
			}
		}
		return mrc;
	}
	
	private static boolean copy(String srcPath, String destPath) {
		Path source = FileSystems.getDefault().getPath(srcPath);
		Path target = FileSystems.getDefault().getPath(destPath);
		try {
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
