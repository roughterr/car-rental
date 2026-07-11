package com.roughterr.carrental.service;

import com.roughterr.carrental.domain.CarType;
import com.roughterr.carrental.domain.ReservationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class CarReservationServiceTest {
    private CarReservationService service;

    @BeforeEach
    void setUp() {
        service = new CarReservationService();
    }

    /**
     * Tests a situation when the selected return date is earlier than the selected pickup date.
     */
    @Test
    void shouldReturnInvalidRequestWhenReturnDateIsBeforePickupDate() {
        assertInstanceOf(ReservationResult.InvalidCarReservationRequest.class, service.reserveCar(
                OffsetDateTime.of(2026, 7, 16, 10, 0, 0, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(2026, 7, 15, 10, 0, 0, 0, ZoneOffset.ofHours(2)), CarType.SEDAN));
    }
}