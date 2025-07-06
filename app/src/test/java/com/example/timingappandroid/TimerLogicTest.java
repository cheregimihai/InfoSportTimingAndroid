package com.example.timingappandroid;

import org.junit.Test;
import static org.junit.Assert.*;

public class TimerLogicTest {
    private static class TimerState {
        private final int[] stopTimes = {1800, 3600, 3900, 4200, 4500, 4800};
        private int nextStopIndex = 0;
        void updateNextStopIndex(long currentMillis) {
            int currentSecs = (int) (currentMillis / 1000);
            int index = stopTimes.length;
            for (int i = 0; i < stopTimes.length; i++) {
                if (currentSecs < stopTimes[i]) {
                    index = i;
                    break;
                }
            }
            nextStopIndex = index;
        }
        boolean checkForStop(long currentMillis) {
            int secs = (int) (currentMillis / 1000);
            if (nextStopIndex < stopTimes.length && secs >= stopTimes[nextStopIndex]) {
                nextStopIndex++;
                return true;
            }
            return false;
        }
        int getNextStopIndex() { return nextStopIndex; }

        long adjust(long current, long adjustment) {
            long newTime = current + adjustment;
            for (int stopTime : stopTimes) {
                long boundary = stopTime * 1000L;
                if (current < boundary && newTime >= boundary) {
                    newTime = boundary;
                } else if (current > boundary && newTime <= boundary) {
                    newTime = boundary;
                }
            }
            if (newTime < 0) {
                newTime = 0;
            }
            if (newTime / 1000 > 4800) {
                newTime = 4_800_000L;
            }
            return newTime;
        }
    }

    @Test
    public void testResumePastStopTimes() {
        TimerState state = new TimerState();
        assertTrue(state.checkForStop(1_800_000));
        assertEquals(1, state.getNextStopIndex());
        assertFalse(state.checkForStop(1_801_000));
        assertTrue(state.checkForStop(3_600_000));
        assertEquals(2, state.getNextStopIndex());
    }

    @Test
    public void testStartBeyondFirstStop() {
        TimerState state = new TimerState();
        state.updateNextStopIndex(1_860_000); // 31:00
        assertEquals(1, state.getNextStopIndex());
        assertFalse(state.checkForStop(1_860_000));
        assertTrue(state.checkForStop(3_600_000));
    }

    @Test
    public void testAdjustTimeClampsAroundStops() {
        TimerState state = new TimerState();
        int[] boundaries = {1800, 3600, 3900, 4200, 4500, 4800};
        for (int b : boundaries) {
            long boundaryMillis = b * 1000L;
            // +1 sec should not pass the boundary
            assertEquals(boundaryMillis,
                    state.adjust(boundaryMillis - 1000L, 1000L));
            // -1 sec should not go below the boundary
            assertEquals(boundaryMillis,
                    state.adjust(boundaryMillis + 1000L, -1000L));
        }
    }
}
