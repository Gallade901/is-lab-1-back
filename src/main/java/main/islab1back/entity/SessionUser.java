package main.islab1back.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "sessions")
public class SessionUser {

    public SessionUser() {
    }

    public SessionUser (String id, User user, LocalDateTime expiresAt) {
        this.id = id;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    @Id
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime expiresAt;

}
