package com.roughterr.carrental.service;

import com.roughterr.carrental.domain.CarReservationRequest;
import com.roughterr.carrental.domain.CarType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarInventoryServiceTest {
    private static final Instant FROM = Instant.parse("2026-07-15T10:00:00Z");
    private static final Instant TO = Instant.parse("2026-07-16T10:00:00Z");

    private CarInventoryService service;

    @BeforeEach
    void setUp() {
        service = new CarInventoryService();
    }

    @Test
    void shouldReturnNumberOfSedans() {
        assertEquals(10, service.getNumberOfCars(new CarReservationRequest(CarType.SEDAN, FROM, TO)));
    }

    @Test
    void shouldReturnNumberOfSuvs() {
        assertEquals(5, service.getNumberOfCars(new CarReservationRequest(CarType.SUV, FROM, TO)));
    }

    @Test
    void shouldReturnNumberOfVans() {
        assertEquals(3, service.getNumberOfCars(new CarReservationRequest(CarType.VAN, FROM, TO)));
    }

    @Test
    void shouldRejectNullCarType() {
        assertThrows(NullPointerException.class, () -> service.getNumberOfCars(new CarReservationRequest(null, FROM, TO)));
    }

    @Test
    void shouldRejectNullFromDate() {
        assertThrows(NullPointerException.class, () -> service.getNumberOfCars(new CarReservationRequest(CarType.SEDAN, null, TO)));
    }

    @Test
    void shouldRejectNullToDate() {
        assertThrows(NullPointerException.class, () -> service.getNumberOfCars(new CarReservationRequest(CarType.SEDAN, FROM, null)));
    }

    @Test
    void shouldRejectEqualDates() {
        assertThrows(IllegalArgumentException.class, () -> service.getNumberOfCars(new CarReservationRequest(CarType.SEDAN, FROM, FROM)));
    }

    @Test
    void shouldRejectFromDateAfterToDate() {
        assertThrows(IllegalArgumentException.class, () -> service.getNumberOfCars(new CarReservationRequest(CarType.SEDAN, TO, FROM)));
    }
}