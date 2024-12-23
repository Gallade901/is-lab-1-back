package main.islab1back.house.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.islab1back.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class House {
    public House (String name, int year, int numberOfFloors, User user) {
        this.name = name;
        this.year = year;
        this.numberOfFloors = numberOfFloors;
        this.user = user;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String name;

    @NotNull
    @Max(681)
    @Min(0)
    private int year;

    @NotNull
    @Max(80)
    @Min(1)
    private int numberOfFloors;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
