package main.islab1back.coordinates.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordinatesDtoRequest {
    @NotNull
    private int x;
    @Max(965)
    private long y;
    @NotNull
    private String login;
}
