package main.islab1back.coordinates.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.islab1back.flats.model.Flat;
import main.islab1back.user.model.User;

import java.util.List;


@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Coordinates {
    public Coordinates(int x, long y, User user, List<Flat> flatList) {
        this.x = x;
        this.y = y;
        this.user = user;
        this.flats = flatList;

    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private int x;

    @NotNull
    @Max(965)
    private long y;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "coordinates", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Flat> flats;
}
