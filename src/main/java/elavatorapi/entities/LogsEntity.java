package elavatorapi.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import org.springframework.boot.autoconfigure.web.WebProperties;

/**
 * Created by Itotia Kibanyu on 26 Jun, 2023
 */
@Entity
@Data
@Table(name = "AUDIT_LOGS")
public class LogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
