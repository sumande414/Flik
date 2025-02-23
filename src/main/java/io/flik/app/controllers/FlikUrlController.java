package io.flik.app.controllers;

import io.flik.app.DTO.FlikUrlDTO;
import io.flik.app.entities.FlikUrl;
import io.flik.app.services.FlikUrlService;
import io.flik.app.utils.FlikUrlRequest;
import io.flik.app.utils.FlikUrlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;


@RestController
@RequestMapping("/api/v1/")
public class FlikUrlController {
    @Autowired
    private FlikUrlService flikUrlService;

    @PostMapping("/generate-url")
    public ResponseEntity<FlikUrlResponse> generateUrlHandler(@RequestBody FlikUrlRequest flikUrlRequest, HttpServletRequest request){
        String generatedUrl = flikUrlService.generateFlikUrl(flikUrlRequest.getOriginalUrl(), request);
        return new ResponseEntity<>(new FlikUrlResponse(generatedUrl), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-url/{encoded_string}")
    public ResponseEntity<String> deleteUrlHandler(@PathVariable String encoded_string){
        flikUrlService.deleteFlikUrl(encoded_string);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/get-all-flikurl")
    public ResponseEntity<ArrayList<FlikUrlDTO>> getFlikUrlsHandler(){
        return new ResponseEntity<>(flikUrlService.getAllFlikUrl(), HttpStatus.OK);
    }
}
