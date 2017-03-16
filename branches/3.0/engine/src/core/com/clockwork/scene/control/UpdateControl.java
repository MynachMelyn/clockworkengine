
package com.clockwork.scene.control;

import com.clockwork.app.AppTask;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Spatial;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

/**
 * Allows for enqueueing tasks onto the update loop / rendering thread.
 * 
 * Usage:
 * mySpatial.addControl(new UpdateControl()); // add it once
 * mySpatial.getControl(UpdateControl.class).enqueue(new Callable() {
 *        public Object call() throws Exception {
 *            // do stuff here
 *            return null;
 *        }
 *    });
 * 
 */
public class UpdateControl extends AbstractControl {

    private final ConcurrentLinkedQueue<AppTask<?>> taskQueue = new ConcurrentLinkedQueue<AppTask<?>>();

    /**
     * Enqueues a task/callable object to execute in the CW
     * rendering thread.
     */
    public <V> Future<V> enqueue(Callable<V> callable) {
        AppTask<V> task = new AppTask<V>(callable);
        taskQueue.add(task);
        return task;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        AppTask<?> task = taskQueue.poll();
        toploop: do {
            if (task == null) break;
            while (task.isCancelled()) {
                task = taskQueue.poll();
                if (task == null) break toploop;
            }
            task.invoke();
        } while (((task = taskQueue.poll()) != null));
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    public Control cloneForSpatial(Spatial newSpatial) {
        UpdateControl control = new UpdateControl(); 
        control.setSpatial(newSpatial);
        control.setEnabled(isEnabled());
        control.taskQueue.addAll(taskQueue);
        return control;
    }
    
}
