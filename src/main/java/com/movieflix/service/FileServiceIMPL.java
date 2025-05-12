package com.movieflix.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceIMPL implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // get name of the file
        String fileName = file.getOriginalFilename();

        //get the file path
        String filePath= path + File.separator + fileName;

        //Create a File
        File newFile = new File(path);
        if(!newFile.exists()){
            newFile.mkdir();
        }

        //copy file or upload the to the path
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {

        String filePath= path + File.separator + fileName;
        return new FileInputStream(filePath);
    }
}
