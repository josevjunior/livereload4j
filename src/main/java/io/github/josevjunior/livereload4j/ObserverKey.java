/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.josevjunior.livereload4j;

import io.github.josevjunior.livereload4j.utils.Logger;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ObserverKey implements Runnable {
    
    private final static Logger LOGGER = Logger.getLogger(ObserverKey.class);
    
    private final Path resourcePath;
    private Map<WatchKey, Path> watchKeys = new HashMap<>();
    private WatchService watchService;
    private Consumer<FileChangeEvent> onChange;
    private boolean canceled;

    public ObserverKey(Path resourcePath, Consumer<FileChangeEvent> onChange) {
        this.resourcePath = resourcePath;
        this.onChange = onChange;
    }

    public void register() throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        Files.walkFileTree(resourcePath, new SimpleFileVisitor<Path>() {
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                watchKeys.put(key, dir);
                return FileVisitResult.CONTINUE;
            }            
        });
    }

    public void unregister() {
        if (!canceled) {
            watchKeys.forEach((k, v) -> k.cancel());
            canceled = true;
        }
    }

    @Override
    public void run() {
        //tickWaiter = new TickWaiter();
        while(true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException x) {                
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path folderPath = watchKeys.get(key);
                if (folderPath == null) {
                    continue;
                }

                
                if (ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    try {
                        Path parentPath = watchKeys.get(key);
                        Path completePath = parentPath.resolve(ev.context());
                        WatchKey anotherKey = completePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                        watchKeys.put(anotherKey, completePath);
                    } catch (IOException e) {
                        LOGGER.debug(e.getMessage(), e);
                    }
                }

                if (ev.kind() == StandardWatchEventKinds.ENTRY_DELETE) {

                    Path parentPath = watchKeys.get(key);
                    Path completePath = parentPath.resolve(ev.context());

                    WatchKey toDelete = null;
                    for (WatchKey watchKey : watchKeys.keySet()) {
                        if (completePath.equals(watchKey)) {
                            toDelete = watchKey;
                            break;
                        }
                    }
                    
                    if(toDelete != null) {
                        toDelete.cancel();
                        watchKeys.remove(toDelete);
                    }
                    
                }

                Path filePath = folderPath.resolve(ev.context());
                onChange.accept(new FileChangeEvent(filePath, kind));
                //tickWaiter.notifyOrWait();
            }
            
            boolean valid = key.reset();
            if (!valid) {
                key.cancel();
                watchKeys.remove(key);
            }
        }
    }    

}
