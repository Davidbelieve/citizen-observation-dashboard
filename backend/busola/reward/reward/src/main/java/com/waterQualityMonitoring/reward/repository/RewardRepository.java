package com.waterQualityMonitoring.reward.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.waterQualityMonitoring.reward.model.RewardModel;

/**
 * In-memory repository used to store reward snapshots during runtime.
 */
@Repository
public class RewardRepository {

    private final Map<String, RewardModel> rewardStore = new ConcurrentHashMap<>();

    public void clear() {
        rewardStore.clear();
    }

    public void save(RewardModel reward) {
        rewardStore.put(reward.getCitizenId(), reward);
    }

    public void saveAll(Collection<RewardModel> rewards) {
        rewards.forEach(this::save);
    }

    public Optional<RewardModel> findByCitizenId(String citizenId) {
        return Optional.ofNullable(rewardStore.get(citizenId));
    }

    public List<RewardModel> findAll() {
        return new ArrayList<>(rewardStore.values());
    }
}
