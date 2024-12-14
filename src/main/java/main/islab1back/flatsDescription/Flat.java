package main.islab1back.flatsDescription;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Flat")
public class Flat {
    @Id
    @Getter @Setter
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    @Getter @Setter
    private String name; //Поле не может быть null, Строка не может быть пустой
    @Getter @Setter
    private Coordinates coordinates; //Поле не может быть null
    @Getter @Setter
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @Getter @Setter
    private double area; //Максимальное значение поля: 521, Значение поля должно быть больше 0
    @Getter @Setter
    private Double price; //Максимальное значение поля: 648728965, Значение поля должно быть больше 0
    @Getter @Setter
    private Boolean balcony; //Поле может быть null
    @Getter @Setter
    private float timeToMetroOnFoot; //Значение поля должно быть больше 0
    @Getter @Setter
    private Long numberOfRooms; //Максимальное значение поля: 11, Значение поля должно быть больше 0
    @Getter @Setter
    private Furnish furnish; //Поле может быть null
    @Getter @Setter
    private View view; //Поле может быть null
    @Getter @Setter
    private Transport transport; //Поле не может быть null
    @Getter @Setter
    private House house; //Поле может быть null

}