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
}
