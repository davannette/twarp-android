package com.shawft.twarp;

import java.util.Date;

/**
 * Created by david on 2/10/2015.
 */
public class TimerController {

    public enum Skip { BIG, SMALL };
    public enum Direction { BACK, FORWARD };

    // skipping constants:
    private static final long BIG_SKIP = 5 * 60 * 1000;
    private static final long SMALL_SKIP = 30 * 1000;

    // timer properties:
    private Date created = null;
    private Date origin;
    private long offset = 0;

    // pause functionality:
    private boolean paused = false;
    private Date timePaused = null;

    public TimerController(Date time) {
        created = new Date();
        origin = time;
    }

    public Date getTime() {
        long timediff = 0;
        if (paused)
            timediff = timePaused.getTime() - created.getTime();
        else
            timediff = new Date().getTime() - created.getTime();
        return new Date(origin.getTime() + timediff + offset);
    }

    public void skip(Skip skip, Direction dir) {
        long amt = 0;
        switch (skip) {
            case SMALL :
                amt = SMALL_SKIP;
                break;
            case BIG :
                amt = BIG_SKIP;
                break;
        }
        if (dir == Direction.BACK)
            offset -= amt;
        else
            offset += amt;
    }

    public boolean togglePause() {
        paused = !paused;
        if (paused)
            timePaused = new Date();
        else
            offset -= new Date().getTime() - timePaused.getTime();
        return paused;
    }

}
