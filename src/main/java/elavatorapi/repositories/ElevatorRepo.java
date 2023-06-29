package elavatorapi.repositories;

import elavatorapi.entities.ElevatorsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
public interface ElevatorRepo extends JpaRepository<ElevatorsEntity, Long> {
    ElevatorsEntity findFirstByElevatorIdentifier(String elevatorIdentifier);

    @Query(nativeQuery = true, value = "SELECT elevator_identifier FROM elevators WHERE status='Active'")
    String [] searchAllByStatus();
    List<ElevatorsEntity> findAllByStatus(String status);
}
