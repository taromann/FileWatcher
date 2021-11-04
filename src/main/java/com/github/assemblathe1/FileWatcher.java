package com.github.assemblathe1;

import com.github.assemblathe1.listeners.FileListener;
import com.github.assemblathe1.utils.FileEvent;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher extends SimpleFileVisitor<Path> {

    Set<Path> foldersToWatch = new HashSet<>();
    protected List<FileListener> listeners = new ArrayList<>();
    protected static final List<WatchService> watchServices = new ArrayList<>();

    public FileWatcher(Path folder, FileListener listener) {
        createFoldersTree(folder);
        listeners.add(listener);
    }

    public void start() {
        foldersToWatch.forEach(this::addDirectoryToWatching);
    }

    private void createFoldersTree(Path folder) {
        try {
            Files.walkFileTree(folder, this);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        foldersToWatch.add(dir);
        return FileVisitResult.CONTINUE;
    }

    public void addDirectoryToWatching(Path path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                    path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                    watchServices.add(watchService);
                    boolean poll = true;
                    while (poll) {
                        poll = pollEvents(watchService);
                    }
                } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    protected boolean pollEvents(WatchService watchService) throws InterruptedException, IOException {
//        System.out.println("FileWatcher 66 THREAD:::   " + Thread.currentThread().getName());
        WatchKey key = watchService.take();
        Path path = (Path) key.watchable();
        for (WatchEvent<?> event : key.pollEvents()) {
            notifyListeners(watchService, event.kind(), path.resolve((Path) event.context()));
        }
        return key.reset();
    }

    protected void notifyListeners(WatchService watchService, WatchEvent.Kind<?> kind, Path path) {

        FileEvent event = new FileEvent(path);
        if (kind == ENTRY_CREATE) {
            for (FileListener listener : listeners) {
                listener.onCreated(kind, event);
            }
            if (path.toFile().isDirectory()) {
                addDirectoryToWatching(path);
            }
            System.out.println(Thread.currentThread().getName());
        }
        else if (kind == ENTRY_MODIFY) {
            for (FileListener listener : listeners) {
                listener.onModified(kind, event);
            }
        } else if (kind == ENTRY_DELETE) {
            for (FileListener listener : listeners) {
                listener.onDeleted(kind, event);
            }
        }
    }

}
