package main.islab1back.house.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
public class House {
    public House (String name, Integer year, Integer numberOfFloors, User user, List<Flat> flatList) {
        this.name = name;
        this.year = year;
        this.numberOfFloors = numberOfFloors;
        this.user = user;
        this.flats = flatList;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @NotNull
    @Max(681)
    @Min(0)
    private Integer year;

    @NotNull
    @Max(80)
    @Min(1)
    private Integer numberOfFloors;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "house", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Flat> flats;
}
