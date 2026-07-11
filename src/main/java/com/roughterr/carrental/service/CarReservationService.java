package com.roughterr.carrental.service;

import com.roughterr.carrental.domain.CarType;
import com.roughterr.carrental.domain.ReservationResult;

import java.time.OffsetDateTime;

public class CarReservationService {
    /**
     * Reserves a car.
     *
     * @param pickupDate pickup/start date
     * @param returnDate vehicle return date
     * @param carType    car type
     * @return
     */
    public ReservationResult reserveCar(OffsetDateTime pickupDate, OffsetDateTime returnDate, CarType carType) {
        //TODO
        return new ReservationResult.CarNotAvaiable();
    }

}
