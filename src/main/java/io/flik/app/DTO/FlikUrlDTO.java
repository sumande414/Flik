package io.flik.app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlikUrlDTO {
    private String originalUrl;

    private String flikkedUrl;

    private String createdAt;

    private Long clickCount = 0L;
}
