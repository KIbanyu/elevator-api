package elavatorapi.services;

import com.google.gson.Gson;
import elavatorapi.configs.AppProps;
import elavatorapi.configs.ElevatorConfigs;
import elavatorapi.entities.ElevatorLogsEntity;
import elavatorapi.entities.ElevatorsEntity;

import elavatorapi.entities.SqlLogsEntity;
import elavatorapi.models.requests.CallElevator;
import elavatorapi.models.responses.ElevatorInfo;
import elavatorapi.repositories.ElevatorLogRepo;
import elavatorapi.repositories.ElevatorRepo;
import elavatorapi.repositories.SqlLogsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
@Service
@Slf4j
public class ElevatorService {


    @Autowired
    private ElevatorLogRepo elevatorLogRepo;

    @Autowired
    private ElevatorRepo elevatorRepo;

    @Autowired
    private AppProps appProps;
    HashMap<String, Object> response;

    @Autowired
    private SqlLogsRepo sqlLogsRepo;

    @Async
    public void getElevator(CallElevator request) throws Exception {

        log.info(appProps.getLine());
        log.info("Elevator request " + new Gson().toJson(request));
        log.info(appProps.getLine());

        ElevatorsEntity calledElevator = elevatorRepo.findFirstByElevatorIdentifier(request.getElevatorIdentifier());
        saveSqlLog("select * from elevators where elevator_identifier =:" + request.getElevatorIdentifier() + ";");
        ElevatorLogsEntity elevatorLogsEntity = elevatorLogRepo.filerByElevatorId(calledElevator.getId());
        saveSqlLog("select * from elevator_logs where elvator_id =:" + calledElevator.getId() + " order by updated_on desc limit 1");

        int directionCalled = request.getToFloor() - request.getFromFloor();
        int currentElevatorFloor = elevatorLogsEntity.getCurrentFloor();

        String elevatorDirection = "UP";

        if (directionCalled < 0) {
            elevatorDirection = "DOWN";
        }

        log.info(appProps.getLine());
        log.info("LAST ELEVATOR {} LOG {}", request.getElevatorIdentifier(), new Gson().toJson(elevatorLogsEntity));
        log.info(appProps.getLine());


        //Check if the current elevator status is idle, to determine the distance
        //When the elevator is idle
        if (elevatorLogsEntity.getElevatorState().equalsIgnoreCase("IDLE")) {
            int numOfFloors = 0;
            //check if the current elevator floor is the same as from caller floor
            if (currentElevatorFloor != request.getFromFloor()) {
                //Get number of floors between the current lift floor and the caller floor
                numOfFloors = currentElevatorFloor - request.getFromFloor();
            }

            //If number of floors is 0, then lift should just open and close
            if (numOfFloors == 0) {

                updateElevatorIfo("OPENING", currentElevatorFloor, request.getFromFloor(), "OPENING", calledElevator.getId(), 2);
                //Get number of floors from the pick up floor to the next floor
                numOfFloors = currentElevatorFloor - request.getToFloor();
                //Convert a negative integer to a positive integer
                if (numOfFloors < 0) {
                    numOfFloors = Math.abs(numOfFloors);
                }


                //Thread sleep 5 seconds per floor, as elevator takes 5 seconds to moe from one floor to the next
                for (int i = 0; i < numOfFloors; i++) {
                    if (elevatorDirection.equalsIgnoreCase("UP")) {
                        currentElevatorFloor = currentElevatorFloor + 1;
                    } else {
                        currentElevatorFloor = currentElevatorFloor - 1;
                    }
                    if (i == request.getToFloor() - 1) {
                        updateElevatorIfo("STOPPING", currentElevatorFloor, request.getToFloor(), "ARRIVED", calledElevator.getId(), 0);


                    } else {
                        updateElevatorIfo(elevatorDirection, currentElevatorFloor, request.getToFloor(), "MOVING", calledElevator.getId(), 0);
                    }
                    Thread.sleep(5 * 1000L);
                }
                //On arrival, elevator sleeps for two seconds
                Thread.sleep(2 * 1000L);
                updateElevatorIfo("IDLE", currentElevatorFloor, currentElevatorFloor, "IDLE", calledElevator.getId(), 0);


            } else {

                log.info("Current elevator floor " + currentElevatorFloor);


                //Elevator and the caller are not in the same floor
                //Elevator should go to the called floor
                //Get number of floors from elevator floor to caller floor
                int elevatorToFloor = request.getFromFloor() - currentElevatorFloor;

                //Update the current direction of elevator
                if (elevatorToFloor < 0) {
                    elevatorDirection = "DOWN";
                } else {
                    elevatorDirection = "UP";
                }

                //Convert negative  integer to a positive integer
                if (elevatorToFloor < 0) {
                    elevatorToFloor = Math.abs(elevatorToFloor);
                }

                //Start moving the elevator to direction of the caller
                for (int i = 0; i < elevatorToFloor; i++) {
                    if (elevatorDirection.equalsIgnoreCase("UP")) {
                        currentElevatorFloor = currentElevatorFloor + 1;
                    } else {
                        currentElevatorFloor = currentElevatorFloor - 1;
                    }

                    updateElevatorIfo(elevatorDirection, currentElevatorFloor, request.getFromFloor(), "MOVING", calledElevator.getId(), 5);

                }

                //Opening and closing the elevator on arrival
                updateElevatorIfo("OPENING", currentElevatorFloor, elevatorToFloor, "ARRIVED", calledElevator.getId(), 2);

                //The elevator has arrived the pick up floor
                //Get the number of floors between pick up and desitantion floor
                numOfFloors = request.getToFloor() - currentElevatorFloor;

//                    Update the current direction of elevator
                if (numOfFloors < 0) {
                    elevatorDirection = "DOWN";
                } else {
                    elevatorDirection = "UP";
                }

                //Convert a negative integer to a positive integer
                if (numOfFloors < 0) {
                    numOfFloors = Math.abs(numOfFloors);
                }

                //Thread sleep 5 seconds per floor, as elevator takes 5 seconds to moe from one floor to the next
                for (int i = 0; i < numOfFloors; i++) {
                    if (elevatorDirection.equalsIgnoreCase("UP")) {
                        currentElevatorFloor = currentElevatorFloor + 1;
                    } else {
                        currentElevatorFloor = currentElevatorFloor - 1;
                    }
                    updateElevatorIfo(elevatorDirection, currentElevatorFloor, request.getFromFloor(), "MOVING", calledElevator.getId(), 5);

                }


                updateElevator("ARRIVED", currentElevatorFloor, currentElevatorFloor, "ARRIVED", calledElevator.getId());
                //On arrival, elevator sleeps for two seconds
                Thread.sleep(2 * 1000L);

                updateElevator("IDLE", currentElevatorFloor, currentElevatorFloor, "IDLE", calledElevator.getId());

            }

        } else {

            log.info(appProps.getLine());
            log.info("ELEVATOR ALREADY ENGAGED");
            log.info(appProps.getLine());


        }


        new CompletableFuture<>();
    }


    public void updateElevatorIfo(String elevatorDirection, int currentElevatorFloor, int toFloor, String state, long elevatorId, int seconds) throws InterruptedException {
        updateElevator(elevatorDirection, currentElevatorFloor, toFloor, state, elevatorId);
        Thread.sleep(seconds * 1000L);
    }


    public ResponseEntity<HashMap<String, Object>> getElevatorsInfo() {
        response = new HashMap<>();
        try {

            List<ElevatorInfo> elevatorInfos = new ArrayList<>();
            List<ElevatorsEntity> elevatorsEntities = elevatorRepo.findAllByStatus("Active");

            for (ElevatorsEntity singleElevator : elevatorsEntities) {
                ElevatorLogsEntity elevatorLogsEntity = elevatorLogRepo.filerByElevatorId(singleElevator.getId());

                ElevatorInfo elevatorInfo = new ElevatorInfo();
                elevatorInfo.setElevatorIdentifier(singleElevator.getElevatorIdentifier());
                elevatorInfo.setCurrentFloor(elevatorLogsEntity.getCurrentFloor());
                elevatorInfo.setToFloor(elevatorLogsEntity.getToFloor());
                elevatorInfo.setDirection(elevatorLogsEntity.getElevatorDirection());
                elevatorInfo.setState(elevatorLogsEntity.getElevatorState());
                elevatorInfos.add(elevatorInfo);

            }


            response.put("status", HttpStatus.OK);
            response.put("message", "success");
            response.put("elevators", elevatorInfos);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "An exception occurred while getting elevators info");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }


    }


    public void updateElevator(String direction, int fromFloor, int toFloor, String state, long elevatorId) {

        ElevatorLogsEntity createLog = new ElevatorLogsEntity();
        createLog.setElevatorId(elevatorId);
        createLog.setToFloor(toFloor);
        createLog.setCurrentFloor(fromFloor);
        createLog.setElevatorDirection(direction);
        createLog.setElevatorState(state);
        createLog.setUpdatedOn(new Date());

        log.info(appProps.getLine());
        log.info("ELEVATOR LOG TO UPDATE {}", new Gson().toJson(createLog));
        log.info(appProps.getLine());
        elevatorLogRepo.save(createLog);
        saveSqlLog("INSERT INTO ezra.elevator_logs (current_floor, elevator_direction, elvator_id, elevator_state, to_floor, updated_on) VALUES ( " + fromFloor + ", " + direction + ", " + elevatorId + ", " + direction + ", " + toFloor + ", " + new Date() + ");");


    }

    public void saveSqlLog(String sqlStatement) {
        SqlLogsEntity sqlLogsEntity = new SqlLogsEntity();
        sqlLogsEntity.setExecutedSqlQuery(sqlStatement);
        sqlLogsEntity.setExecutedBy("SYSTEM");
        sqlLogsEntity.setExtecutedOn(new Date());
        sqlLogsRepo.save(sqlLogsEntity);

    }

}
