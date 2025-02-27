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

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;


    public static String encode(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }


        byte[] bytes = input.getBytes();
        StringBuilder encoded = new StringBuilder();


        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            String bin = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            binary.append(bin);
        }


        java.math.BigInteger decimal = new java.math.BigInteger(binary.toString(), 2);


        while (decimal.compareTo(java.math.BigInteger.ZERO) > 0) {
            int remainder = decimal.mod(java.math.BigInteger.valueOf(BASE)).intValue();
            encoded.insert(0, BASE62_CHARS.charAt(remainder));
            decimal = decimal.divide(java.math.BigInteger.valueOf(BASE));
        }

        return encoded.toString();
    }


    public static String decode(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }


        java.math.BigInteger decimal = java.math.BigInteger.ZERO;
        for (int i = 0; i < input.length(); i++) {
            int index = BASE62_CHARS.indexOf(input.charAt(i));
            if (index == -1) {
                throw new IllegalArgumentException("Invalid Base62 character: " + input.charAt(i));
            }
            decimal = decimal.multiply(java.math.BigInteger.valueOf(BASE))
                    .add(java.math.BigInteger.valueOf(index));
        }


        String binary = decimal.toString(2);

        int padding = (8 - (binary.length() % 8)) % 8;
        binary = "0".repeat(padding) + binary;


        byte[] bytes = new byte[binary.length() / 8];
        for (int i = 0; i < bytes.length; i++) {
            String byteStr = binary.substring(i * 8, (i + 1) * 8);
            bytes[i] = (byte) Integer.parseInt(byteStr, 2);
        }

        return new String(bytes);
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
        String encodedString = encode(savedFlikUrl.getId().toString());
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
        Integer id = Integer.parseInt(decode(base62Code));
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
        Integer id = Integer.parseInt(decode(encoded_string));
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
