package com.roughterr.carrental.service;

import com.roughterr.carrental.domain.CarReservationRequest;
import com.roughterr.carrental.domain.CarType;
import com.roughterr.carrental.domain.ReservationResult;
import com.roughterr.carrental.domain.ReservationTreeNode;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

public class CarReservationService {
    /**
     * Beginning of the supported reservation horizon.
     */
    protected static final Instant RESERVATION_START = Instant.parse("2026-01-01T00:00:00Z");

    /**
     * End of the supported reservation horizon.
     */
    protected static final Instant RESERVATION_END = Instant.parse("2034-01-01T00:00:00Z");
    /**
     * Contains data about total number of cars in our system.
     */
    private CarInventoryService carInventoryService;

    /**
     * Root of the reservation tree.
     */
    private final Map<CarType, ReservationTreeNode> reservationTrees = new EnumMap<>(CarType.class);

    public CarReservationService(CarInventoryService carInventoryService) {
        this.carInventoryService = carInventoryService;
        for (CarType carType : CarType.values()) {
            reservationTrees.put(carType, new ReservationTreeNode(RESERVATION_START, RESERVATION_END));
        }
    }

    /**
     * Reserves a car.
     *
     * @param carType    car type
     * @param pickupDate pickup/start date
     * @param returnDate vehicle return date
     * @return
     */
    public ReservationResult reserveCar(CarType carType, Instant pickupDate, Instant returnDate) {
        ReservationTreeNode root = reservationTrees.get(carType);
        int totalCarNumber = carInventoryService.getNumberOfCars(new CarReservationRequest(carType, pickupDate, returnDate));
        //TODO

        return new ReservationResult.CarNotAvaiable();
    }
}
