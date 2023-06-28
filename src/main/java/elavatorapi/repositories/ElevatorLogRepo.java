package elavatorapi.repositories;

import elavatorapi.entities.ElevatorLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
public interface ElevatorLogRepo extends JpaRepository<ElevatorLogsEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT Min(current_floor - :toFloor) as distance, elvator_id as elevatorId from elevator_logs where current_floor >=:toFloor and (elevator_direction='UP' || elevator_logs.elevator_state ='IDLE')")
     List<Integer> getGoingUpElevator(int toFloor);


}
