package elavatorapi.configs;

import com.google.gson.Gson;
import elavatorapi.models.Elevator;
import elavatorapi.repositories.ElevatorLogRepo;
import elavatorapi.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
@Component
@Slf4j
public class ElevatorConfigs implements ApplicationRunner {
    private int floors;
    private int elevators = 5;
    private List<Elevator> elevatorList;
    private List<String> directions;
    private List<String> elevatorStates;

    @Autowired
    private AppProps appProps;

    @Autowired
    private ElevatorLogRepo elevatorLogRepo;

    @Override
    public void run(ApplicationArguments args) {
        elevatorList = new ArrayList<>();
        directions = new ArrayList<>();
        elevatorStates = new ArrayList<>();

        floors = appProps.getFloorCount();

        directions.add("UP");
        directions.add("DOWN");

        elevatorStates.add("IDLE");
        elevatorStates.add("MOVING");



        if (elevatorLogRepo.findAll().isEmpty())
        {
            for (int i = 0; i < elevators; i++) {
                Elevator elevator = new Elevator();
                elevator.setElevatorIdentifier(AppUtils.elevator(i+1));
                elevator.setElevatorState(getRandomState());
                int floor = getRandomFloors();
                elevator.setElevatorDirection(getRandomDirection(floor));
                elevator.setCurrentFloor(floor);
                elevatorList.add(elevator);
            }
        }else {

        }



        log.info("Elevators {} ", new Gson().toJson(elevatorList));

    }


    public int getRandomFloors() {
        Random r = new Random();
        int low = 0;
        int high = floors;
        return r.nextInt(high - low) + low;
    }


    public String getRandomDirection(int floor) {

        if (floor == 0){
            return "UP";
        }
        if (floor == appProps.getFloorCount()){
            return "DOWN";
        }
        return directions.get(new Random().nextInt(directions.size()));
    }

    public String getRandomState() {
        return elevatorStates.get(new Random().nextInt(elevatorStates.size()));
    }


    public List<Elevator> getElevatorList() {
        return elevatorList;
    }
}
