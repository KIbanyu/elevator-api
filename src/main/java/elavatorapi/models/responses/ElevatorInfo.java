package elavatorapi.models.responses;

import lombok.Data;

/**
 * Created by Itotia Kibanyu on 29 Jun, 2023
 */
@Data
public class ElevatorInfo {
    private String elevatorIdentifier;
    private int currentFloor;
    private int toFloor;
    private String direction;
    private String state;
}
