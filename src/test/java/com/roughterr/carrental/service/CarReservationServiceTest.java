package com.roughterr.carrental.service;

import com.roughterr.carrental.domain.CarReservationRequest;
import com.roughterr.carrental.domain.CarType;
import com.roughterr.carrental.domain.ReservationResult;
import com.roughterr.carrental.domain.ReservationTreeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CarReservationServiceTest {
    private static final Instant JULY_10 =
            Instant.parse("2026-07-10T08:00:00Z");
    private static final Instant JULY_15 =
            Instant.parse("2026-07-15T08:00:00Z");
    private static final Instant JULY_20 =
            Instant.parse("2026-07-20T08:00:00Z");
    private static final Instant JULY_25 =
            Instant.parse("2026-07-25T08:00:00Z");
    private static final Instant JULY_30 =
            Instant.parse("2026-07-30T08:00:00Z");

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

    @Test
    void shouldFullyCoverNodeWhenRequestStartsBeforeNodeAndEndsAtNodeEnd() {
        ReservationTreeNode node =
                new ReservationTreeNode(JULY_15, JULY_25);

        CarReservationRequest request =
                new CarReservationRequest(CarType.SEDAN, JULY_10, JULY_25);

        assertTrue(service.shouldNodeBeFullyCovered(node, request));
    }

    @Test
    void shouldFullyCoverNodeWhenRequestStartsAtNodeStartAndEndsAfterNode() {
        ReservationTreeNode node =
                new ReservationTreeNode(JULY_15, JULY_25);

        CarReservationRequest request =
                new CarReservationRequest(CarType.SEDAN, JULY_15, JULY_30);

        assertTrue(service.shouldNodeBeFullyCovered(node, request));
    }

    @Test
    void shouldFullyCoverNodeWhenRequestRangeMatchesNodeRange() {
        ReservationTreeNode node =
                new ReservationTreeNode(JULY_15, JULY_25);

        CarReservationRequest request =
                new CarReservationRequest(CarType.SEDAN, JULY_15, JULY_25);

        assertTrue(service.shouldNodeBeFullyCovered(node, request));
    }

    @Test
    void shouldNotFullyCoverNodeWhenRequestStartsAfterNodeStart() {
        ReservationTreeNode node =
                new ReservationTreeNode(JULY_15, JULY_25);

        CarReservationRequest request =
                new CarReservationRequest(CarType.SEDAN, JULY_20, JULY_30);

        assertFalse(service.shouldNodeBeFullyCovered(node, request));
    }

    @Test
    void shouldNotFullyCoverNodeWhenRequestEndsBeforeNodeEnd() {
        ReservationTreeNode node =
                new ReservationTreeNode(JULY_15, JULY_25);

        CarReservationRequest request =
                new CarReservationRequest(CarType.SEDAN, JULY_10, JULY_20);

        assertFalse(service.shouldNodeBeFullyCovered(node, request));
    }

    @Test
    void shouldAcceptDateAtFullHour() {
        assertDoesNotThrow(() -> service.validateReservationDateGranularity(Instant.parse("2026-07-15T08:00:00Z")));
    }

    @Test
    void shouldAcceptDateAtHalfHour() {
        assertDoesNotThrow(() -> service.validateReservationDateGranularity(Instant.parse("2026-07-15T08:30:00Z")));
    }

    @Test
    void shouldRejectDateWithUnsupportedMinute() {
        assertThrows(IllegalArgumentException.class, () -> service.validateReservationDateGranularity(Instant.parse("2026-07-15T08:15:00Z")));
    }

    @Test
    void shouldRejectDateWithNonZeroSeconds() {
        assertThrows(IllegalArgumentException.class, () -> service.validateReservationDateGranularity(Instant.parse("2026-07-15T08:30:01Z")));
    }

    @Test
    void shouldRejectDateWithNonZeroNanoseconds() {
        assertThrows(IllegalArgumentException.class, () -> service.validateReservationDateGranularity(Instant.parse("2026-07-15T08:30:00.001Z"))
        );
    }

    @Test
    void shouldRejectOverlappingReservationWhenAllVansAreBooked() {
        Instant pickupDate = Instant.parse("2026-07-15T08:00:00Z");
        Instant returnDate = Instant.parse("2026-07-15T12:00:00Z");
        for (int reservationNumber = 0; reservationNumber < 3; reservationNumber++) {
            ReservationResult result = service.reserveCar(
                    CarType.VAN,
                    pickupDate,
                    returnDate
            );
            assertInstanceOf(ReservationResult.Reserved.class, result);
        }
        ReservationResult result = service.reserveCar(
                CarType.VAN,
                Instant.parse("2026-07-15T09:00:00Z"),
                Instant.parse("2026-07-15T10:00:00Z")
        );
        assertInstanceOf(ReservationResult.CarNotAvaiable.class, result);
    }

    @Test
    void shouldAcceptOverlappingReservationWhenAllVansAreBooked() {
        Instant pickupDate = Instant.parse("2026-07-15T08:00:00Z");
        Instant returnDate = Instant.parse("2026-07-15T12:00:00Z");
        for (int reservationNumber = 0; reservationNumber < 2; reservationNumber++) {
            ReservationResult result = service.reserveCar(
                    CarType.VAN,
                    pickupDate,
                    returnDate
            );
            assertInstanceOf(ReservationResult.Reserved.class, result);
        }
        ReservationResult result = service.reserveCar(
                CarType.VAN,
                Instant.parse("2026-07-15T09:00:00Z"),
                Instant.parse("2026-07-15T10:00:00Z")
        );
        assertInstanceOf(ReservationResult.Reserved.class, result);
    }

    private ReservationResult reserveSuv(String pickupDate, String returnDate) {
        return service.reserveCar(
                CarType.SUV,
                Instant.parse(pickupDate),
                Instant.parse(returnDate)
        );
    }

    /**
     * Tests that a reservation is rejected when any part of the requested interval
     * overlaps a fully booked time period.
     *
     * <p>The test creates the following existing SUV reservations:
     * <ul>
     *   <li>2026-07-15 15:00 – 2026-07-16 15:00</li>
     *   <li>2026-07-15 11:00 – 2026-07-16 17:00</li>
     *   <li>2026-07-15 13:30 – 2026-07-16 17:00</li>
     *   <li>2026-07-14 19:00 – 2026-07-16 20:00</li>
     * </ul>
     *
     * <p>After these reservations, one additional SUV can still be reserved for
     * the period 2026-07-15 16:00 – 2026-07-16 19:00. However, a subsequent
     * reservation for 2026-07-15 17:00 – 2026-07-16 20:00 must be rejected because
     * it overlaps a time interval where all SUVs are already reserved.
     */
    @Test
    void shouldRejectReservationWhenAnyPartOfRequestedIntervalIsFullyBooked() {
        reserveSuv(
                "2026-07-15T15:00:00Z",
                "2026-07-16T15:00:00Z"
        );
        reserveSuv(
                "2026-07-15T11:00:00Z",
                "2026-07-16T17:00:00Z"
        );
        reserveSuv(
                "2026-07-15T13:30:00Z",
                "2026-07-16T17:00:00Z"
        );
        reserveSuv(
                "2026-07-14T19:00:00Z",
                "2026-07-16T20:00:00Z"
        );
        ReservationResult fifthReservation = reserveSuv(
                "2026-07-15T16:00:00Z",
                "2026-07-16T19:00:00Z"
        );
        ReservationResult sixthReservation = reserveSuv(
                "2026-07-15T17:00:00Z",
                "2026-07-16T20:00:00Z"
        );
        assertInstanceOf(
                ReservationResult.Reserved.class,
                fifthReservation
        );
        assertInstanceOf(
                ReservationResult.CarNotAvaiable.class,
                sixthReservation
        );
    }
}