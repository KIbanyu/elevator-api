package elavatorapi.repositories;

import elavatorapi.entities.ElevatorLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
public interface ElevatorLogRepo extends JpaRepository<ElevatorLogsEntity, Long> {


    @Query(nativeQuery = true, value = "select * from elevator_logs where elvator_id =:elevatorId order by updated_on desc limit 1")
    ElevatorLogsEntity filerByElevatorId(long elevatorId);
    List<ElevatorLogsEntity> findFirstByElevatorId(long elevatorId);


    @Query(nativeQuery = true, value = "SELECT tbl.* FROM (SELECT * FROM elevator_logs ORDER BY updated_on desc ) as tbl GROUP BY tbl.elvator_id")
    List<ElevatorLogsEntity> filterAllElevatorLatestLog();




}
