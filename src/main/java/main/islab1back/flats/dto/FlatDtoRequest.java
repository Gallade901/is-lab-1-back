package main.islab1back.flats.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import main.islab1back.coordinates.model.Coordinates;
import main.islab1back.flats.model.Furnish;
import main.islab1back.flats.model.Transport;
import main.islab1back.flats.model.View;
import main.islab1back.house.model.House;

import java.time.LocalDate;

@Getter
@Setter
public class FlatDtoRequest {
    @NotNull
    private String name;

    private Integer coordinatesId;

    @NotNull
    private int coordinateX;

    @NotNull
    @Max(965)
    private long coordinateY;

    private Integer houseId;

    @NotNull
    private String houseName;

    @NotNull
    @Max(681)
    @Min(0)
    private int houseYear;

    @NotNull
    @Max(80)
    @Min(1)
    private int houseNumberOfFloors;

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

    @Enumerated(EnumType.STRING)
    private Furnish furnish;

    @Enumerated(EnumType.STRING)
    private View view;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Transport transport;

    @NotNull
    private String login;
}
