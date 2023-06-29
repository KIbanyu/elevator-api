package elavatorapi.configs;

import com.google.gson.Gson;
import elavatorapi.entities.ElevatorLogsEntity;
import elavatorapi.entities.ElevatorsEntity;
import elavatorapi.entities.SqlLogsEntity;
import elavatorapi.models.Elevator;
import elavatorapi.repositories.ElevatorLogRepo;
import elavatorapi.repositories.ElevatorRepo;
import elavatorapi.repositories.SqlLogsRepo;
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

    @Autowired
    private SqlLogsRepo sqlLogsRepo;

    @Override
    public void run(ApplicationArguments args) {

        List<ElevatorsEntity> elevatorsEntities = elevatorRepo.findAllByStatus("Active");
        saveSqlLog("SELECT * from elevators where status = 'Active';");



        for (ElevatorsEntity singleElevator : elevatorsEntities) {

            List<ElevatorLogsEntity> elevatorLogsEntity = elevatorLogRepo.findFirstByElevatorId(singleElevator.getId());
            saveSqlLog("select * from elevator_logs where id=:"+singleElevator.getId()+" limit 1;");

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
                saveSqlLog("INSERT INTO ezra.elevator_logs (current_floor, elevator_direction, elvator_id, elevator_state, to_floor, updated_on) VALUES ( 0, 'IDLE', 1, 'IDLE', 0, "+new Date()+");");

            }

        }

    }

    public void saveSqlLog(String sqlStatement){
        SqlLogsEntity sqlLogsEntity = new SqlLogsEntity();
        sqlLogsEntity.setExecutedSqlQuery(sqlStatement);
        sqlLogsEntity.setExecutedBy("SYSTEM");
        sqlLogsEntity.setExtecutedOn(new Date());
        sqlLogsRepo.save(sqlLogsEntity);

    }


}
