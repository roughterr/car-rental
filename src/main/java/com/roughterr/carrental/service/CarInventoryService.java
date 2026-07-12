package com.roughterr.carrental.service;

import com.roughterr.carrental.domain.CarType;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Provides information about the number of cars available in the fleet.
 */
public class CarInventoryService {
    private static final int NUMBER_OF_SEDANS = 10;
    private static final int NUMBER_OF_SUVS = 5;
    private static final int NUMBER_OF_VANS = 3;

    private final Map<CarType, Integer> fleetSize =
            new EnumMap<>(CarType.class);

    public CarInventoryService() {
        fleetSize.put(CarType.SEDAN, NUMBER_OF_SEDANS);
        fleetSize.put(CarType.SUV, NUMBER_OF_SUVS);
        fleetSize.put(CarType.VAN, NUMBER_OF_VANS);
    }

    /**
     * Returns the minimum number of cars of the specified type available in the
     * fleet during the supplied time interval.
     *
     * <p>The current implementation assumes a constant fleet size. Future
     * implementations may support changes to the fleet over time, such as adding
     * or removing vehicles.</p>
     *
     * @param carType type of car
     * @param from    inclusive start of the interval
     * @param to      exclusive end of the interval
     * @return minimum number of cars available during the interval
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if {@code from} is not before {@code to}
     */
    public int getNumberOfCars(CarType carType, Instant from, Instant to) {
        Objects.requireNonNull(carType, "carType must not be null");
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("'from' must be before 'to'");
        }
        // TODO Consider changes to the fleet over time.
        return fleetSize.get(carType);
    }
}
