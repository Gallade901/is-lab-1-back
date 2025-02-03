package main.islab1back.flats.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.islab1back.flats.model.Furnish;
import main.islab1back.flats.model.Transport;
import main.islab1back.flats.model.View;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlatDtoResponse {
    @NotNull
    private int id;
    @NotNull
    private String name;

    @NotNull
    private int coordinateX;

    @NotNull
    @Max(965)
    private long coordinateY;

    @NotNull
    private String houseName;

    @NotNull
    @Max(681)
    @Min(0)
    private Integer houseYear;

    @NotNull
    @Max(80)
    @Min(1)
    private Integer houseNumberOfFloors;

    @NotNull
    @Max(11)
    @Min(1)
    private Long numberOfRooms;

    @NotNull
    @Max(521)
    @Min(1)
    private Double area;

    @NotNull
    private LocalDate creationDate;

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
    private String owner;
}
