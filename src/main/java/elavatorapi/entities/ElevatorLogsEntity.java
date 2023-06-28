package elavatorapi.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * Created by Itotia Kibanyu on 26 Jun, 2023
 */
@Entity
@Data
@Table(name = "ELEVATOR_LOGS")
public class ElevatorLogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ELVATOR_ID")
    private Long elevatorId;

    @Column(name = "ELEVATOR_STATE")
    private String elevatorState;

    @Column(name = "ELEVATOR_DIRECTION")
    private String elevatorDirection;

    @Column(name = "CURRENT_FLOOR")
    private Integer currentFloor;

    @Column(name = "TO_FLOOR")
    private Integer toFloor;

    @Column(name = "UPDATED_ON")
    private Date updatedOn;



}
