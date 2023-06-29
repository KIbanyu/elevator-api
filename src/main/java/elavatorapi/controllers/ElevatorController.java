package elavatorapi.controllers;

import elavatorapi.configs.AppProps;
import elavatorapi.models.requests.CallElevator;
import elavatorapi.repositories.ElevatorLogRepo;
import elavatorapi.repositories.ElevatorRepo;
import elavatorapi.services.ElevatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Itotia Kibanyu on 26 Jun, 2023
 */
@RestController
@RequestMapping("api/v1")
public class ElevatorController {

    @Autowired
    private ElevatorService elevatorService;

    @Autowired
    private ElevatorLogRepo elevatorLogRepo;

    @Autowired
    private ElevatorRepo elevatorRepo;

    @Autowired
    private AppProps appProps;
    HashMap<String, Object> response;


    @PostMapping("/call-elevator")
    public ResponseEntity<HashMap<String, Object>> callElevator(@RequestBody CallElevator request) throws Exception {

        response = new HashMap<>();


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

        elevatorService.getElevator(request);
        response.put(appProps.getStatus(), HttpStatus.OK);
        response.put(appProps.getMessage(), "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);


    }

    @GetMapping("/get-elevators-info")
    public ResponseEntity<?> getElevators() {
        return elevatorService.getElevatorsInfo();

    }
}
