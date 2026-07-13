package com.roughterr.carrental.service;

import com.roughterr.carrental.domain.CarType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CarReservationServiceTest {
    private CarReservationService service;

    @BeforeEach
    void setUp() {
        service = new CarReservationService(new CarInventoryService());
    }

    /**
     * Tests a situation when the selected return date is earlier than the selected pickup date.
     */
    @Test
    void shouldReturnInvalidRequestWhenReturnDateIsBeforePickupDate() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.reserveCar(
                        CarType.SEDAN,
                        Instant.parse("2026-07-16T08:00:00Z"),
                        Instant.parse("2026-07-15T08:00:00Z")
                )
        );
    }
}