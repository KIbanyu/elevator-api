package elavatorapi.models.requests;

import lombok.Data;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
@Data
public class CallElevator {
    private Integer fromFloor;
    private Integer toFloor;
    private String elevatorIdentifier;

}
