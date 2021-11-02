package com.github.assemblathe1.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.EventObject;

public class FileEvent extends EventObject {

    public FileEvent(Path path) {
        super(path);
    }

    public Path getFile() {
        return (Path) getSource();
    }



}
