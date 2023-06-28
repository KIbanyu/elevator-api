package elavatorapi.models;

import lombok.Data;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
@Data
public class Elevator {
    private String elevatorIdentifier;
    private String elevatorState;
    private String elevatorDirection;
    private Integer currentFloor;
    private Integer toFloor;





}
