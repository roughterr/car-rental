package com.roughterr.carrental.domain;

import java.time.Instant;

/**
 * @param carType    type of car
 * @param pickupDate inclusive start of the interval
 * @param returnDate exclusive end of the interval
 */
public record CarReservationRequest(
        CarType carType,
        Instant pickupDate,
        Instant returnDate
) {
}
