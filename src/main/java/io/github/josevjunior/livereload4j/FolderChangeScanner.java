package io.github.josevjunior.livereload4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FolderChangeScanner {

    private static Map<String, ObserverKey> keys = new HashMap<>();

    static {

        Thread thread = new Thread(() -> {
            keys.forEach((k, v) -> v.unregister());
        }, "WatchServiceCleaner");

        Runtime.getRuntime().addShutdownHook(thread);
    }

    public static ObserverKey registerObserver(Project project, Consumer<FileChangeEvent> onChange) throws IOException {
        
        Path rootPath = project.getRootPath();
        
        if (!Files.isDirectory(rootPath)) {
            throw new IOException("Path " + project.getRootPath() + " does not point to a valid folder");
        }

        if (!Files.exists(rootPath)) {
            throw new FileNotFoundException(project.getRootPath().toString());
        }
        
        if(onChange == null) {
            throw new NullPointerException("onChange must not be null");
        }

        if (keys.containsKey(rootPath.toString())) {
            throw new IllegalArgumentException("Path already registered in this runtime");
        }

        ObserverKey observerKey = new ObserverKey(project, onChange);
        observerKey.register();

        Thread thread = new Thread(observerKey);
        thread.setName("WatchService [folder=" + rootPath + "]");
        thread.start();

        keys.put(rootPath.toString(), observerKey);
        
        return observerKey;
    }

}
