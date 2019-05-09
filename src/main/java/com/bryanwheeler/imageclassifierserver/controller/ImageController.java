package com.bryanwheeler.imageclassifierserver.controller;

import com.bryanwheeler.imageclassifierserver.View;
import com.bryanwheeler.imageclassifierserver.classifier.ImageClassifier;
import com.bryanwheeler.imageclassifierserver.model.ImageModel;
import com.bryanwheeler.imageclassifierserver.repository.ImageRepo;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
public class ImageController {

    private final ImageRepo imageRepo;

    public ImageController(ImageRepo imageRepo) {
        this.imageRepo = imageRepo;
    }

    @JsonView(View.ImageInfo.class)
    @GetMapping("/image/all")
    public List<ImageModel> getAllImages() {
        return imageRepo.findAll();
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        Optional<ImageModel> imageModelOptional = imageRepo.findById(id);
        if(imageModelOptional.isPresent()) {
            ImageModel image = imageModelOptional.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION
                            , "attachment; filename=\"" + image.getFilename() + "\"")
                    .body(image.getImage());
        }
        return ResponseEntity.status(404).body(null);
    }

    @PostMapping("/image/upload")
    public String uploadMultipartFile(@RequestParam("file")MultipartFile file) {
        try{
            ImageClassifier image = new ImageClassifier(file.getBytes());
            image.classifyImage();
            String result = image.getResult();
            ImageModel imageModel = new ImageModel(file.getOriginalFilename()
                                                    ,file.getContentType()
                                                    ,file.getBytes()
                                                    ,result);
            imageRepo.save(imageModel);
            return result;
//            return "File uploaded: " + file.getOriginalFilename();
        } catch (Exception e) {
            return "Upload Faliled! File already uploaded or exceeds max size.";
        }
    }
}
