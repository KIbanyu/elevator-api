package elavatorapi.configs;

import com.google.gson.Gson;
import elavatorapi.entities.ElevatorLogsEntity;
import elavatorapi.entities.ElevatorsEntity;
import elavatorapi.models.Elevator;
import elavatorapi.repositories.ElevatorLogRepo;
import elavatorapi.repositories.ElevatorRepo;
import elavatorapi.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
@Component
@Slf4j
public class ElevatorConfigs implements ApplicationRunner {

    @Autowired
    private AppProps appProps;

    @Autowired
    private ElevatorRepo elevatorRepo;

    @Autowired
    private ElevatorLogRepo elevatorLogRepo;

    @Override
    public void run(ApplicationArguments args) {

        List<ElevatorsEntity> elevatorsEntities = elevatorRepo.findAllByStatus("Active");

        for (ElevatorsEntity singleElevator : elevatorsEntities) {

            List<ElevatorLogsEntity> elevatorLogsEntity = elevatorLogRepo.findFirstByElevatorId(singleElevator.getId());

            log.info(appProps.getLine());
            log.info("Elevator " + singleElevator.getElevatorIdentifier() + " log is " + new Gson().toJson(elevatorLogsEntity));
            log.info(appProps.getLine());

            if (elevatorLogsEntity.isEmpty()) {
                ElevatorLogsEntity createLog = new ElevatorLogsEntity();
                createLog.setElevatorId(singleElevator.getId());
                createLog.setToFloor(0);
                createLog.setCurrentFloor(0);
                createLog.setElevatorDirection("IDLE");
                createLog.setElevatorState("IDLE");
                createLog.setUpdatedOn(new Date());
                elevatorLogRepo.save(createLog);

            }

        }

    }


}
