/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service.fsm.utils;

import com.online.casino.service.fsm.event.FoldEvent;
import com.online.casino.service.fsm.utils.TimerHelper;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Bjorn Harvold
 * Date: 1/19/11
 * Time: 10:55 PM
 * Responsibility:
 */
public class TimerHelperTest extends TestCase {

    private static final String pokergameId = "1";

    @Test
    public void testTimer() {
        TimerHelper.startTime(pokergameId);

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Timer is wrong", 2L, TimerHelper.checkTime(pokergameId), 0);

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Timer is wrong", 4L, TimerHelper.checkTime(pokergameId), 0);
    }

    @Test
    public void testTimerTask() {
        final Timer timeoutTimer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("I ran");
            }
        };


        timeoutTimer.schedule(timerTask, 1000L);
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
