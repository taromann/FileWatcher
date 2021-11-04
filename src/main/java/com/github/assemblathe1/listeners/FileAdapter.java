package com.github.assemblathe1.listeners;

import com.github.assemblathe1.utils.FileEvent;

import java.nio.file.WatchEvent;

public class FileAdapter implements FileListener {

    @Override
    public void onCreated(WatchEvent.Kind<?> kind, FileEvent event) {
//        map.add("file.created " + event.getFile().getFileName());
        System.out.println(kind + " " + event.getFile() + "  " + event);
    }
    @Override
    public void onModified(WatchEvent.Kind<?> kind, FileEvent event) {
//        map.add("file.modified " + event.getFile());
//        System.out.println(kind + " " + event.getFile());
    }

    @Override
    public void onDeleted(WatchEvent.Kind<?> kind, FileEvent event) {
//        map.add("file.deleted" + event.getFile().getFileName());
        System.out.println(kind + " " + event.getFile());
    }


}