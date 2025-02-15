package main.islab1back.flats.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.Getter;
import lombok.Setter;
import main.islab1back.flats.model.Furnish;
import main.islab1back.flats.model.Transport;
import main.islab1back.flats.model.View;


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

    private String houseName;


    private Integer houseYear;

    private Integer houseNumberOfFloors;

    @NotNull
    @Max(11)
    @Min(1)
    private Long numberOfRooms;

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
