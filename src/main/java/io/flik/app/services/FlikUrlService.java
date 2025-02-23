package io.flik.app.services;

import io.flik.app.DTO.FlikUrlDTO;
import io.flik.app.auth.entities.User;
import io.flik.app.entities.FlikUrl;
import io.flik.app.exceptions.FlikUrlNotFoundException;
import io.flik.app.repositories.FlikUrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.ArrayList;

@Service
public class FlikUrlService {
    @Autowired
    private FlikUrlRepository flikUrlRepository;

    @Autowired
    private UserService userService;

    private String encodeBase62(Integer num){
        String ASCII_BASE_62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String output = "";
        while(num!=0){
            output = ASCII_BASE_62.charAt(num%62) + output;
            num /= 62;
        }
        return output;
    }

    private Integer decodeBase62(String encodedStr) {
        String ASCII_BASE_62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int num = 0;

        for (char c : encodedStr.toCharArray()) {
            num = num * 62 + ASCII_BASE_62.indexOf(c);
        }

        return num;
    }


    public String generateFlikUrl(String originalUrl, HttpServletRequest request) {
        FlikUrl flikUrl = new FlikUrl(
                null,
                originalUrl,
                null,
                Instant.now().toString(),
                0L,
                userService.getCurrentUser()
        );
        FlikUrl savedFlikUrl = flikUrlRepository.save(flikUrl);
        String encodedString = encodeBase62(savedFlikUrl.getId());
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        String generatedUrl = baseUrl + "/r/" + encodedString;
        savedFlikUrl.setFlikkedUrl(generatedUrl);
        flikUrlRepository.save(savedFlikUrl);
        return generatedUrl;
    }

    public String resolve(String base62Code) {
        Integer id = decodeBase62(base62Code);
        FlikUrl flikUrl = flikUrlRepository.getReferenceById(id);
        String resolvedUrl = flikUrl.getOriginalUrl();
        flikUrl.setClickCount(flikUrl.getClickCount()+1);
        flikUrlRepository.save(flikUrl);
        if (!resolvedUrl.startsWith("http://") && !resolvedUrl.startsWith("https://")) {
            resolvedUrl = "https://" + resolvedUrl; // Default to HTTPS
        }
        return resolvedUrl;
    }

    public void deleteFlikUrl(String encoded_string){
        Integer id = decodeBase62(encoded_string);
        FlikUrl flikUrl = flikUrlRepository.findById(id).orElseThrow(()->new FlikUrlNotFoundException("Wrong Url code :" + encoded_string));
        flikUrlRepository.delete(flikUrl);
    }

    public ArrayList<FlikUrlDTO> getAllFlikUrl(){
        User user = userService.getCurrentUser();
        ArrayList<FlikUrl> flikUrls = flikUrlRepository.findAllByUser(user);
        ArrayList <FlikUrlDTO> response = new ArrayList<>();
        for(FlikUrl flikUrl: flikUrls){
            response.add(new FlikUrlDTO(
                  flikUrl.getOriginalUrl(),
                  flikUrl.getFlikkedUrl(),
                  flikUrl.getCreatedAt(),
                  flikUrl.getClickCount()
            ));

        }
        return response;
    }
}
