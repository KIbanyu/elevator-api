package elavatorapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Itotia Kibanyu on 26 Jun, 2023
 */
@RestController
@RequestMapping("api/v1")
public class ElevatorController {


    @PostMapping("/call-elevator")
    public ResponseEntity<?> callElevator(){
        return new ResponseEntity<>("Success", HttpStatus.OK);

    }

    @GetMapping("/get-elevator-info/{elevatorId}")
    public ResponseEntity<?> getElevatorInfo(@PathVariable("elevatorId") long elevatorId){
        return new ResponseEntity<>(elevatorId, HttpStatus.OK);

    }


    @GetMapping("/get-elevators")
    public ResponseEntity<?> getElevators(){
        return new ResponseEntity<>("All", HttpStatus.OK);

    }
}
