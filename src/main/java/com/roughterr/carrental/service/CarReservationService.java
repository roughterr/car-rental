package com.roughterr.carrental.service;

import com.roughterr.carrental.domain.CarReservationRequest;
import com.roughterr.carrental.domain.CarType;
import com.roughterr.carrental.domain.ReservationResult;
import com.roughterr.carrental.domain.ReservationTreeNode;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

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

    private int lastReservationId = 0;

    public CarReservationService(CarInventoryService carInventoryService) {
        this.carInventoryService = carInventoryService;
        for (CarType carType : CarType.values()) {
            reservationTrees.put(carType, new ReservationTreeNode(RESERVATION_START, RESERVATION_END));
        }
    }

    protected void validateReservationDateGranularity(Instant date) {
        ZonedDateTime dateTime = date.atZone(ZoneOffset.UTC);
        boolean validMinute = dateTime.getMinute() == 0
                || dateTime.getMinute() == 30;
        boolean validSecond = dateTime.getSecond() == 0;
        boolean validNano = dateTime.getNano() == 0;
        if (!validMinute || !validSecond || !validNano) {
            throw new IllegalArgumentException(
                    "Reservation dates must be aligned to 30-minute intervals"
            );
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
        validateReservationDateGranularity(pickupDate);
        validateReservationDateGranularity(returnDate);
        ReservationTreeNode rootNode = reservationTrees.get(carType);
        CarReservationRequest carReservationRequest = new CarReservationRequest(carType, pickupDate, returnDate);
        int totalCarNumber = carInventoryService.getNumberOfCars(carReservationRequest);
        // nodes where coveringReservationCount would need to be incremented
        List<ReservationTreeNode> nodesToFullyCover = new ArrayList<>();
        Deque<ReservationTreeNode> nodesToProbe = new ArrayDeque<>(List.of(rootNode));
        while (!nodesToProbe.isEmpty()) {
            ReservationTreeNode nodeToProbe = nodesToProbe.pop();
            if (shouldNodeBeFullyCovered(nodeToProbe, carReservationRequest)) {
                // if all the cars are fully booked
                if (nodeToProbe.getCoveringReservationCount() >= totalCarNumber) {
                    return new ReservationResult.CarNotAvaiable();
                } else {
                    nodesToFullyCover.add(nodeToProbe);
                }
            } else if (shouldNodeBeAtLeastPartiallyCovered(nodeToProbe, carReservationRequest)) {
                nodeToProbe.split();
                nodesToProbe.push(nodeToProbe.getFirstHalf());
                nodesToProbe.push(nodeToProbe.getSecondHalf());
            }
        }
        nodesToFullyCover.forEach(ReservationTreeNode::incrementCoveringReservationCount);
        return new ReservationResult.Reserved(String.valueOf(++lastReservationId));
    }

    /**
     * Returns true if node's range is within carReservationRequest range.
     * That means:
     * the reservation must start at or before the node starts;
     * the reservation must extend past the node so that the entire interior of the node is covered;
     * whether the reservation ends exactly at the node's end or after it doesn't matter.
     *
     * @param node
     * @param request
     * @return
     */
    protected boolean shouldNodeBeFullyCovered(ReservationTreeNode node, CarReservationRequest request) {
        return !node.getStart().isBefore(request.pickupDate())
                && !node.getEnd().isAfter(request.returnDate());
    }

    /**
     * Returns {@code true} when the reservation request overlaps at least part
     * of the node's time range.
     *
     * <p>Both ranges are treated as half-open intervals: {@code [start, end)}.
     */
    protected boolean shouldNodeBeAtLeastPartiallyCovered(ReservationTreeNode node, CarReservationRequest request) {
        return request.pickupDate().isBefore(node.getEnd()) && request.returnDate().isAfter(node.getStart());
    }
}
