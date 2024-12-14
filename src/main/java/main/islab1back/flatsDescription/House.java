package main.islab1back.flatsDescription;

import lombok.Getter;
import lombok.Setter;

public class House {
    @Getter @Setter
    private String name; //Поле может быть null
    @Getter @Setter
    private long year; //Максимальное значение поля: 681, Значение поля должно быть больше 0
    @Getter @Setter
    private long numberOfFloors; //Максимальное значение поля: 80, Значение поля должно быть больше 0
}