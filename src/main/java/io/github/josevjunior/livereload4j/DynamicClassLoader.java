
package io.github.josevjunior.livereload4j;

import io.github.josevjunior.livereload4j.utils.FileUtils;
import io.github.josevjunior.livereload4j.utils.IOUtils;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class DynamicClassLoader extends AggressiveClassLoader{

    LinkedList<Function<String, byte[]>> loaders = new LinkedList<>();

    public DynamicClassLoader(ClassLoader parent, String... paths) {
        super(parent);
		for (String path : paths) {
			File file = new File(path);

			Function<String, byte[]> loader = loader(file);
			if (loader == null) {
				throw new RuntimeException("Path not exists " + path);
			}
			loaders.add(loader);
		}
	}

	@SuppressWarnings("UnusedDeclaration")
	public DynamicClassLoader(Collection<File> paths) {
		for (File file : paths) {
			Function<String, byte[]> loader = loader(file);
			if (loader == null) {
				throw new RuntimeException("Path not exists " + file.getPath());
			}
			loaders.add(loader);
		}
	}
	

	public static Function<String, byte[]> loader(File file) {
		if (!file.exists()) {
			return null;
		} else if (file.isDirectory()) {
			return dirLoader(file);
		} else {
			try {
				final JarFile jarFile = new JarFile(file);

				return jarLoader(jarFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static File findFile(String filePath, File classPath) {
		File file = new File(classPath, filePath);
		return file.exists() ? file : null;
	}

	public static Function<String, byte[]> dirLoader(final File dir) {
		return filePath -> {
			File file = findFile(filePath, dir);
			if (file == null) {
				return null;
			}

			return FileUtils.readFileToBytes(file);
		};
	}

	private static Function<String, byte[]> jarLoader(final JarFile jarFile) {
		return new Function<String, byte[]>() {
			public byte[] apply(String filePath) {
				ZipEntry entry = jarFile.getJarEntry(filePath);
				if (entry == null) {
					return null;
				}
				try {
					return IOUtils.readData(jarFile.getInputStream(entry));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			protected void finalize() throws Throwable {
				IOUtils.close(jarFile);
				super.finalize();
			}
		};
	}
	
	@Override
	protected byte[] loadNewClass(String name) {
		for (Function<String, byte[]> loader : loaders) {
			byte[] data = loader.apply(AggressiveClassLoader.toFilePath(name));
			if (data!= null) {
				return data;
			}
		}
		return null;
	}
    
}
