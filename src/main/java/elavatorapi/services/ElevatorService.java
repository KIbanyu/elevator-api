package elavatorapi.services;

import com.google.gson.Gson;
import elavatorapi.configs.AppProps;
import elavatorapi.configs.ElevatorConfigs;
import elavatorapi.entities.ElevatorLogsEntity;
import elavatorapi.entities.ElevatorsEntity;

import elavatorapi.models.requests.CallElevator;
import elavatorapi.repositories.ElevatorLogRepo;
import elavatorapi.repositories.ElevatorRepo;
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
    private ElevatorConfigs elevatorConfigs;

    @Autowired
    private ElevatorLogRepo elevatorLogRepo;

    @Autowired
    private ElevatorRepo elevatorRepo;

    @Autowired
    private AppProps appProps;
    HashMap<String, Object> response;


    public ResponseEntity<HashMap<String, Object>> getElevator(CallElevator request) {
        response = new HashMap<>();
        try {
            log.info(appProps.getLine());
            log.info("Elevator request " + new Gson().toJson(request));
            log.info(appProps.getLine());


            String[] elevators = elevatorRepo.searchAllByStatus();

            //Validate elevator
            if (request.getElevatorIdentifier() == null
                    || request.getElevatorIdentifier().isEmpty()
                    || request.getElevatorIdentifier().isBlank()) {
                response.put(appProps.getStatus(), HttpStatus.BAD_REQUEST);
                response.put(appProps.getMessage(), "Enter the elevator you want to call " + Arrays.asList(elevators));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            if (!Arrays.asList(elevators).contains(request.getElevatorIdentifier().replace(" ", ""))) {
                response.put(appProps.getStatus(), HttpStatus.BAD_REQUEST);
                response.put(appProps.getMessage(), "Enter the elevator you want to call " + Arrays.asList(elevators));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            //Check if the floor entered is more than number of floors in the building
            if (request.getFromFloor() < 0 || request.getFromFloor() > appProps.getFloorCount()) {
                response.put(appProps.getStatus(), HttpStatus.BAD_REQUEST);
                response.put(appProps.getMessage(), "Invalid From floor, floors must be between 0 and " + appProps.getFloorCount());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

            }

            //Check if the floor entered is more than number of floors in the building
            if (request.getToFloor() < 0 || request.getToFloor() > appProps.getFloorCount()) {
                response.put(appProps.getStatus(), HttpStatus.BAD_REQUEST);
                response.put(appProps.getMessage(), "Invalid To floor, floors must be between 0 and " + appProps.getFloorCount());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

            }


            if (Objects.equals(request.getFromFloor(), request.getToFloor())) {
                response.put(appProps.getStatus(), HttpStatus.BAD_REQUEST);
                response.put(appProps.getMessage(), "You cannot move to same floor");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

            }

            startElevatorsActions(request);
            response.put(appProps.getStatus(), HttpStatus.OK);
            response.put(appProps.getMessage(), "Success");
            return new ResponseEntity<>(response, HttpStatus.OK);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<HashMap<String, Object>> getElevatorsInfo() {
        response = new HashMap<>();
        try {
            response.put("status", HttpStatus.OK);
            response.put("message", "success");
            response.put("elevators", elevatorLogRepo.filterAllElevatorLatestLog());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "An exception occurred while getting elevators info");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }


    }


    @Async
    public CompletableFuture<String> startElevatorsActions(CallElevator request) {


        try {
            ElevatorsEntity calledElevator = elevatorRepo.findFirstByElevatorIdentifier(request.getElevatorIdentifier());
            ElevatorLogsEntity elevatorLogsEntity = elevatorLogRepo.filerByElevatorId(calledElevator.getId());
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
                    updateElevator("OPENING", currentElevatorFloor, request.getToFloor(), "OPENING", calledElevator.getId());
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
                            updateElevator("STOPPING", currentElevatorFloor, request.getToFloor(), "ARRIVED", calledElevator.getId());

                        } else {
                            updateElevator(elevatorDirection, currentElevatorFloor, request.getToFloor(), "MOVING", calledElevator.getId());

                        }
                        Thread.sleep(5 * 1000L);
                    }
                    //On arrival, elevator sleeps for two seconds
                    Thread.sleep(2 * 1000L);
                    updateElevator("IDLE", currentElevatorFloor, currentElevatorFloor, "IDLE", calledElevator.getId());

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

                        updateElevator(elevatorDirection, currentElevatorFloor, request.getFromFloor(), "MOVING", calledElevator.getId());
                        Thread.sleep(5 * 1000L);
                    }

                    //Opening and closing the elevator on arrival
                    updateElevator("OPENING", currentElevatorFloor, elevatorToFloor, "ARRIVED", calledElevator.getId());
                    Thread.sleep(2 * 1000L);


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
                        updateElevator(elevatorDirection, currentElevatorFloor, request.getToFloor(), "MOVING", calledElevator.getId());
                        Thread.sleep(5 * 1000L);
                    }


                    updateElevator("ARRIVED", currentElevatorFloor, currentElevatorFloor, "ARRIVED", calledElevator.getId());
                    //On arrival, elevator sleeps for two seconds
                    Thread.sleep(2 * 1000L);

                    updateElevator("IDLE", currentElevatorFloor, currentElevatorFloor, "IDLE", calledElevator.getId());

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CompletableFuture<>();

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

    }


}
