package main.islab1back.flatsDescription;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coordinates {
    @Getter @Setter
    private Float x; //Поле не может быть null
    @Getter @Setter
    private Integer y; //Максимальное значение поля: 965, Поле не может быть null
}