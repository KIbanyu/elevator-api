package elavatorapi.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


/**
 * Created by Itotia Kibanyu on 29 Jun, 2023
 */
@Entity
@Data
@Table(name = "SQL_LOGS")
public class SqlLogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SQL_QUERY")
    private String executedSqlQuery;

    @Column(name = "EXECUTED_BY")
    private String executedBy;

    @Column(name = "EXECUTED_ON")
    private Date extecutedOn;

}
