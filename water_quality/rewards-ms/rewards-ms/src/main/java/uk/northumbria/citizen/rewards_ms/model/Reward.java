package uk.northumbria.citizen.rewards_ms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a reward calculation result.
 * Used internally for reward processing logic.
 */
@Entity
@Table(name = "rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reward {
    @Id
    private String citizenId;
    private Integer points;
    private String badge;
}

