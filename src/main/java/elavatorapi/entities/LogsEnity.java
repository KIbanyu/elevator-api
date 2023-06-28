package elavatorapi.entities;

import jakarta.persistence.*;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */

@Entity
@Table(name = "LOGS")
public class LogsEnity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;



}
