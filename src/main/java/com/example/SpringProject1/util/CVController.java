package com.example.SpringProject1.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/cv")
public class CVController {
    private IndexHandler indexHandler;
    @Autowired
    public CVController(IndexHandler indexHandler) {
        this.indexHandler = indexHandler;
    }


    @PostMapping("/upload")
    public String upload(@RequestParam("file")MultipartFile file) throws IOException {
        indexHandler.handleUpload(file);
        return "File " + file.getOriginalFilename() + " uploaded successfully" ;
    }

    @GetMapping("/search/{skill}")
    public List<String> getFileName(@PathVariable(name = "skill") String skill) throws IOException {
        return indexHandler.search(skill);
    }
}
