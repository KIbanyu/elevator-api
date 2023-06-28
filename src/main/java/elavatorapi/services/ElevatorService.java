package elavatorapi.services;

import com.google.gson.Gson;
import elavatorapi.configs.AppProps;
import elavatorapi.configs.ElevatorConfigs;
import elavatorapi.entities.ElevatorLogsEntity;
import elavatorapi.entities.ElevatorsEntity;
import elavatorapi.models.Elevator;
import elavatorapi.models.requests.CallElevator;
import elavatorapi.repositories.ElevatorLogRepo;
import elavatorapi.repositories.ElevatorRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
            log.info("--------------------------");
            log.info("Elevator request " + new Gson().toJson(request));
            log.info("--------------------------");

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


            if (request.getFromFloor() == request.getToFloor()) {
                response.put(appProps.getStatus(), HttpStatus.BAD_REQUEST);
                response.put(appProps.getMessage(), "You cannot move to same floor");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            if (request.getFromFloor() > request.getToFloor()) {
                log.info("--------------------------");
                log.info("REQUEST IS TO FO DOWN");
                log.info("--------------------------");
                //Get the closet lift which is idle or direction is down



                List<Integer> items = elevatorLogRepo.getGoingUpElevator(request.getToFloor());

                log.info("Items " + new Gson().toJson(items));

                if (items.get(0) == null){
                    ElevatorsEntity elevatorsEntity = elevatorRepo.findFirstByElevatorIdentifier("A");
                    ElevatorLogsEntity logRepo = new ElevatorLogsEntity();
                    logRepo.setElevatorId(elevatorsEntity.getId());
                    logRepo.setElevatorDirection("DOWN");
                    logRepo.setElevatorState("MOVING");
                    logRepo.setToFloor(request.getToFloor());
                    logRepo.setCurrentFloor(request.getFromFloor());
                    logRepo.setUpdatedOn(new Date());
                    elevatorLogRepo.save(logRepo);

                    int floorsToMove = request.getFromFloor()  - request.getToFloor()  + 2;

                    Thread.sleep(floorsToMove * 1000);
                    updateElevator();


                }






            } else {

                log.info("--------------------------");
                log.info("REQUEST IS TO FO UP");
                log.info("--------------------------");

                //Get the closet lift which is idle or direction is up

                updateElevator();
            }


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
            response.put("elevators", elevatorConfigs.getElevatorList());

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "An exception occurred while getting elevators info");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }


    }


    @Async
    public void updateElevator() {
        log.info("Lift reached and opened anc closed the door");

    }


}
