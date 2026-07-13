package com.roughterr.carrental.service;

import com.roughterr.carrental.domain.CarReservationRequest;
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
     * @param request car reservation request data
     * @return minimum number of cars available during the interval
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if {@code from} is not before {@code to}
     */
    public int getNumberOfCars(CarReservationRequest request) {
        Objects.requireNonNull(request.carType(), "carType must not be null");
        Objects.requireNonNull(request.pickupDate(), "from must not be null");
        Objects.requireNonNull(request.returnDate(), "to must not be null");
        if (!request.pickupDate().isBefore(request.returnDate())) {
            throw new IllegalArgumentException("'from' must be before 'to'");
        }
        // TODO Consider changes to the fleet over time.
        return fleetSize.get(request.carType());
    }
}
