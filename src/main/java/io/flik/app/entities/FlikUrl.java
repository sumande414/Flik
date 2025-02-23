package io.flik.app.entities;

import io.flik.app.auth.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FlikUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "fliked_url")
    private String flikkedUrl;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    @ManyToOne
    @JoinColumn(name="user_userId", nullable = false)
    private User user;

}
