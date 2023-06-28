package elavatorapi.configs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
@Component
@Data
public class AppProps {

    private String status = "status";
    private String message = "message";

    @Value("${building.floors}")
    private int floorCount;
}

