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

    @Test
    void shouldSplitNodeIntoTwoEqualHalves() {
        Instant start = Instant.parse("2026-07-15T08:00:00Z");
        Instant middle = Instant.parse("2026-07-16T08:00:00Z");
        Instant end = Instant.parse("2026-07-17T08:00:00Z");
        ReservationTreeNode node = new ReservationTreeNode(start, end);
        node.incrementCoveringReservationCount();
        node.incrementCoveringReservationCount();
        node.split();
        ReservationTreeNode firstHalf = node.getFirstHalf();
        ReservationTreeNode secondHalf = node.getSecondHalf();
        assertNotNull(firstHalf);
        assertNotNull(secondHalf);
        assertEquals(start, firstHalf.getStart());
        assertEquals(middle, firstHalf.getEnd());
        assertEquals(middle, secondHalf.getStart());
        assertEquals(end, secondHalf.getEnd());
        assertEquals(2, firstHalf.getCoveringReservationCount());
        assertEquals(2, secondHalf.getCoveringReservationCount());
    }

    @Test
    void shouldNotReplaceChildrenWhenNodeIsSplitAgain() {
        ReservationTreeNode node = new ReservationTreeNode(
                Instant.parse("2026-07-15T08:00:00Z"),
                Instant.parse("2026-07-17T08:00:00Z")
        );
        node.split();
        ReservationTreeNode originalFirstHalf = node.getFirstHalf();
        ReservationTreeNode originalSecondHalf = node.getSecondHalf();
        node.split();
        assertSame(originalFirstHalf, node.getFirstHalf());
        assertSame(originalSecondHalf, node.getSecondHalf());
    }
}