package uk.northumbria.citizen.crowdsourced_data_ms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.northumbria.citizen.crowdsourced_data_ms.model.Observation;

import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {
    List<Observation> findByCitizenId(String citizenId);
    List<Observation> findByPostcode(String postcode);
}

