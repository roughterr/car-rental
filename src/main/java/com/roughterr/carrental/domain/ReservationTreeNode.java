package com.roughterr.carrental.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a node in a dynamic reservation segment tree.
 */
public class ReservationTreeNode {

    /**
     * Inclusive start of the interval represented by this node.
     */
    private final Instant start;

    /**
     * Exclusive end of the interval represented by this node.
     */
    private final Instant end;

    /**
     * Number of reservations covering this node's complete interval.
     *
     * <p>A reservation counted here covers every instant in
     * {@code [start, end)}.</p>
     */
    private int coveringReservationCount = 0;

    /**
     * Child representing the first half of this node's interval.
     */
    private ReservationTreeNode firstHalf;

    /**
     * Child representing the second half of this node's interval.
     */
    private ReservationTreeNode secondHalf;

    public ReservationTreeNode(Instant start, Instant end) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start must be before end");
        }
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public int getCoveringReservationCount() {
        return coveringReservationCount;
    }

    public void incrementCoveringReservationCount() {
        coveringReservationCount++;
    }

    public void decrementCoveringReservationCount() {
        if (coveringReservationCount == 0) {
            throw new IllegalStateException("coveringReservationCount cannot become negative");
        }
        coveringReservationCount--;
    }

    public ReservationTreeNode getFirstHalf() {
        return firstHalf;
    }

    public void setFirstHalf(ReservationTreeNode firstHalf) {
        this.firstHalf = Objects.requireNonNull(firstHalf);
    }

    public ReservationTreeNode getSecondHalf() {
        return secondHalf;
    }

    public void setSecondHalf(ReservationTreeNode secondHalf) {
        this.secondHalf = Objects.requireNonNull(secondHalf);
    }

    public boolean hasFirstHalf() {
        return firstHalf != null;
    }

    public boolean hasSecondHalf() {
        return secondHalf != null;
    }
}
