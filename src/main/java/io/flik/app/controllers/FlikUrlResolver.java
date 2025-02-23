package io.flik.app.controllers;

import io.flik.app.services.FlikUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/r")
public class FlikUrlResolver {
    @Autowired
    FlikUrlService flikUrlService;
    @GetMapping("/{encoded_string}")
    public ResponseEntity<Void> flikUrlResolveHandler(@PathVariable  String encoded_string){
        String resolvedUrl = flikUrlService.resolve(encoded_string);
        System.out.println(resolvedUrl);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(resolvedUrl)).build();
    }
}
