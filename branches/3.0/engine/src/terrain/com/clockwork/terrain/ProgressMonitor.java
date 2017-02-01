
package com.clockwork.terrain;

/**
 * Monitor the progress of an expensive terrain operation.
 *
 * Monitors are passed into the expensive operations, and those operations
 * call the incrementProgress method whenever they determine that progress
 * has changed. It is up to the monitor to determine if the increment is
 * percentage or a unit of another measure, but anything calling it should
 * use the setMonitorMax() method and make sure incrementProgress() match up
 * in terms of units.
 *
 */
public interface ProgressMonitor {

    /**
     * Increment the progress by a unit.
     */
    public void incrementProgress(float increment);

    /**
     * The max value that when reached, the progress is at 100%.
     */
    public void setMonitorMax(float max);

    /**
     * The max value of the progress. When incrementProgress()
     * reaches this value, progress is complete
     */
    public float getMonitorMax();

    /**
     * The progress has completed
     */
    public void progressComplete();
}
