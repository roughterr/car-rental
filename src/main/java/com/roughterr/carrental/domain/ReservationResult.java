package com.roughterr.carrental.domain;

/**
 * Result of an operation of reserving a car.
 */
public sealed interface ReservationResult permits ReservationResult.CarNotAvaiable, ReservationResult.Reserved {
    record Reserved(String reservationId) implements ReservationResult {
    }

    record CarNotAvaiable() implements ReservationResult {
    }
}



