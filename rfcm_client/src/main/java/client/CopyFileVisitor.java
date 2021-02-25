package client;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

import static java.nio.file.FileVisitResult.TERMINATE;

public class CopyFileVisitor extends SimpleFileVisitor<Path> {
    private final Path source;
    private final Path target;
    public CopyFileVisitor(@NotNull Path source, @NotNull Path target) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
    }
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        File a = new File(String.valueOf(target.resolve(source.relativize(dir))));
        a.mkdir();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {
        Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
    }
}

