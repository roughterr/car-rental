package com.roughterr.carrental.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTreeNodeTest {
    private static final Instant START = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant END = Instant.parse("2026-01-02T00:00:00Z");

    @Test
    void shouldCreateNodeWithGivenInterval() {
        ReservationTreeNode node = new ReservationTreeNode(START, END);
        assertEquals(START, node.getStart());
        assertEquals(END, node.getEnd());
        assertEquals(0, node.getCoveringReservationCount());
        assertFalse(node.hasFirstHalf());
        assertFalse(node.hasSecondHalf());
    }

    @Test
    void shouldRejectInvalidInterval() {
        assertThrows(IllegalArgumentException.class, () -> new ReservationTreeNode(END, START));
        assertThrows(IllegalArgumentException.class, () -> new ReservationTreeNode(START, START));
    }

    @Test
    void shouldIncrementAndDecrementCoveringReservationCount() {
        ReservationTreeNode node = new ReservationTreeNode(START, END);
        node.incrementCoveringReservationCount();
        node.incrementCoveringReservationCount();
        assertEquals(2, node.getCoveringReservationCount());
        node.decrementCoveringReservationCount();
        assertEquals(1, node.getCoveringReservationCount());
    }

    @Test
    void shouldNotAllowNegativeReservationCount() {
        ReservationTreeNode node = new ReservationTreeNode(START, END);
        assertThrows(IllegalStateException.class, node::decrementCoveringReservationCount);
    }

    @Test
    void shouldSetChildNodes() {
        ReservationTreeNode node = new ReservationTreeNode(START, END);
        Instant midpoint = Instant.parse("2026-01-01T12:00:00Z");
        ReservationTreeNode firstHalf = new ReservationTreeNode(START, midpoint);
        ReservationTreeNode secondHalf = new ReservationTreeNode(midpoint, END);
        node.setFirstHalf(firstHalf);
        node.setSecondHalf(secondHalf);
        assertTrue(node.hasFirstHalf());
        assertTrue(node.hasSecondHalf());
        assertSame(firstHalf, node.getFirstHalf());
        assertSame(secondHalf, node.getSecondHalf());
    }
}