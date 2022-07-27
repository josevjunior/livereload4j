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

    public static ObserverKey registerObserver(Path folderPath, Consumer<FileChangeEvent> onChange) throws IOException {
        
        if (!Files.isDirectory(folderPath)) {
            throw new IOException("Path " + folderPath + " does not point to a valid folder");
        }

        if (!Files.exists(folderPath)) {
            throw new FileNotFoundException(folderPath.toString());
        }
        
        if(onChange == null) {
            throw new NullPointerException("onChange must not be null");
        }

        if (keys.containsKey(folderPath.toString())) {
            throw new IllegalArgumentException("Path already registered in this runtime");
        }

        ObserverKey observerKey = new ObserverKey(folderPath, onChange);
        observerKey.register();

        Thread thread = new Thread(observerKey);
        thread.setName("WatchService [folder=" + folderPath + "]");
        thread.start();

        keys.put(folderPath.toString(), observerKey);
        
        return observerKey;
    }

}
