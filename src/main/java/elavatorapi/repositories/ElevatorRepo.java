package elavatorapi.repositories;

import elavatorapi.entities.ElevatorsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
public interface ElevatorRepo extends JpaRepository<ElevatorsEntity, Long> {
    ElevatorsEntity findFirstByElevatorIdentifier(String elevatorIdentifier);
}
