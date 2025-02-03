package main.islab1back.flats.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.islab1back.coordinates.model.Coordinates;
import main.islab1back.house.model.House;
import main.islab1back.user.model.User;

import java.time.LocalDate;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Flat {
    public Flat(String name, Coordinates coordinates, House house, double area, double price,
                boolean balcony, float timeToMetroOnFoot, long numberOfRooms, Furnish furnish, View view, Transport transport, User user) {
        this.name = name;
        this.coordinates = coordinates;
        this.house = house;
        this.area = area;
        this.price = price;
        this.balcony = balcony;
        this.timeToMetroOnFoot = timeToMetroOnFoot;
        this.numberOfRooms = numberOfRooms;
        this.furnish = furnish;
        this.view = view;
        this.transport = transport;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coordinate_id", nullable = false)
    private Coordinates coordinates;

    @ManyToOne()
    @JoinColumn(name = "house_id")
    private House house;

    @NotNull
    private LocalDate creationDate;

    @NotNull
    @Max(521)
    @Min(1)
    private double area;

    @NotNull
    @Max(648728965)
    @Min(1)
    private Double price;

    private Boolean balcony;

    @NotNull
    private float timeToMetroOnFoot;

    @NotNull
    @Max(11)
    @Min(1)
    private Long numberOfRooms;

    @Enumerated(EnumType.STRING)
    private Furnish furnish;

    @Enumerated(EnumType.STRING)
    private View view;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Transport transport;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
