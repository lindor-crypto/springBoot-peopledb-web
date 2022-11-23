package com.neutrinosys.peopledbweb.data;

import com.neutrinosys.peopledbweb.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FileStorageRepository {

    @Value("${STORAGE_FOLDER}")
    private String storageFolder;
    public void save(String originalFilename, InputStream inputStream) {
        try {
            Path path = getNormalize(originalFilename);
            Files.copy(inputStream,path);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public Resource findByName(String filename){
        try {
            Path path = getNormalize(filename);
            return   new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
                throw new StorageException(e);
        }
    }

    private Path getNormalize(String filename) {
        return Path.of(storageFolder).resolve(filename).normalize();
    }

    public void deleteAllByName(Collection<String> filenames) {
        try {
            Set<String> collect = filenames.stream().filter(f -> f != null).collect(Collectors.toSet());
            for (String filename : collect) {
                Path path = getNormalize(filename);
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {

            throw new StorageException(e);
        }
    }
}
