
package io.github.josevjunior.livereload4j;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class FileChangeEvent {
    
    private final Path fileChanged;
    private final WatchEvent.Kind eventKind;

    public FileChangeEvent(Path fileChanged, WatchEvent.Kind eventKind) {
        this.fileChanged = fileChanged;
        this.eventKind = eventKind;
    }

    public Path getPathChanged() {
        return fileChanged;
    }

    public WatchEvent.Kind getEventKind() {
        return eventKind;
    }
    
}
