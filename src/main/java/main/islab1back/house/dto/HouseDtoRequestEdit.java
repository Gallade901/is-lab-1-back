package main.islab1back.house.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HouseDtoRequestEdit {

    @NotNull
    private Integer id;

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
    private String owner;
}
