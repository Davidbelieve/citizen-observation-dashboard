package com.waterQualityMonitoring.crowdsourced.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.Column;
import java.util.UUID;
import jakarta.persistence.Table;

/**
 * JPA entity storing laboratory-style measurements associated with an
 * observation. Shares its primary key with the parent {@link Observation}.
 */
@Entity
@Table(name = "measurements")
public class Measurement {
    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observation_id", nullable = false, unique = true)
    @MapsId
    private Observation observation;

    @Column(name = "temperature_c")
    private Double temperatureC;

    private Double pH;

    @Column(name = "alkalinity_mg_per_l")
    private Double alkalinityMgPerL;

    @Column(name = "turbidity_ntu")
    private Double turbidityNtu;

    // getters and setters
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public Observation getObservation() {
        return observation;
    }
    public void setObservation(Observation observation) {
        this.observation = observation;
    }
    public Double getTemperatureC() {
        return temperatureC;
    }
    public void setTemperatureC(Double temperatureC) {
        this.temperatureC = temperatureC;
    }
    public Double getpH() {
        return pH;
    }
    public void setpH(Double pH) {
        this.pH = pH;
    }
    public Double getAlkalinityMgPerL() {
        return alkalinityMgPerL;
    }
    public void setAlkalinityMgPerL(Double alkalinityMgPerL) {
        this.alkalinityMgPerL = alkalinityMgPerL;
    }
    public Double getTurbidityNtu() {
        return turbidityNtu;
    }
    public void setTurbidityNtu(Double turbidityNtu) {
        this.turbidityNtu = turbidityNtu;
    }
}
