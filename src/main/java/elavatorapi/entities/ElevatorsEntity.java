package elavatorapi.entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Created by Itotia Kibanyu on 26 Jun, 2023
 */
@Entity
@Data
@Table(name = "Elevators")
public class ElevatorsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ELEVATOR_IDENTIFIER")
    private String elevatorIdentifier;

    @Column(name = "STATUS")
    private String status;


}
