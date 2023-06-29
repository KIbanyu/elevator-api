package elavatorapi.repositories;

import elavatorapi.entities.SqlLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Itotia Kibanyu on 29 Jun, 2023
 */
public interface SqlLogsRepo extends JpaRepository<SqlLogsEntity, Long> {
}
