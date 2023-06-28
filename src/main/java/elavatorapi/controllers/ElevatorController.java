package elavatorapi.controllers;

import elavatorapi.models.requests.CallElevator;
import elavatorapi.services.ElevatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Created by Itotia Kibanyu on 26 Jun, 2023
 */
@RestController
@RequestMapping("api/v1")
public class ElevatorController {

    @Autowired
    private ElevatorService elevatorService;


    @PostMapping("/call-elevator")
    public ResponseEntity<HashMap<String, Object>> callElevator(@RequestBody CallElevator request){
        return elevatorService.getElevator(request);

    }

    @GetMapping("/get-elevators-info")
    public ResponseEntity<?> getElevators(){
        return elevatorService.getElevatorsInfo();

    }
}
