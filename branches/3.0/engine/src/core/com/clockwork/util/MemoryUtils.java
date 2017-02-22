
package com.clockwork.util;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * See thread http://jmonkeyengine.org/forum/topic/monitor-direct-memory-usage-in-your-app/#post-205999
 * 
 */
public class MemoryUtils {
    private static MBeanServer mbeans = ManagementFactory.getPlatformMBeanServer();
    private static ObjectName directPool;
    static {
        try {
            // Create the name reference for the direct buffer pool’s MBean
            directPool = new ObjectName("java.nio:type=BufferPool,name=direct");
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(MemoryUtils.class.getName()).log(Level.SEVERE, "Error creating direct pool ObjectName", ex);
        }
    }

    /**
     * 
     * @return the direct memory used in byte.
     */
    public static long getDirectMemoryUsage() {
        try {
            Long value = (Long)mbeans.getAttribute(directPool, "MemoryUsed");
            return value == null ? -1 : value;
        } catch (JMException ex) {
            Logger.getLogger(MemoryUtils.class.getName()).log(Level.SEVERE, "Error retrieving ‘MemoryUsed’", ex);
            return -1;
        }
    }

    /**
     * 
     * @return the number of direct buffer used
     */
    public static long getDirectMemoryCount() {
        try {
            Long value = (Long)mbeans.getAttribute(directPool, "Count");
            return value == null ? -1 : value;
        } catch (JMException ex) {
            Logger.getLogger(MemoryUtils.class.getName()).log(Level.SEVERE, "Error retrieving ‘Count’", ex);
            return -1;
        }
    }

    /**
     * 
     * @return Should return the total direct memory available, result seem off
     * see post http://jmonkeyengine.org/forum/topic/monitor-direct-memory-usage-in-your-app/#post-205999
     */
    public static long getDirectMemoryTotalCapacity() {
        try {
            Long value = (Long)mbeans.getAttribute(directPool, "TotalCapacity");
            return value == null ? -1 : value;
        } catch (JMException ex) {
            Logger.getLogger(MemoryUtils.class.getName()).log(Level.SEVERE, "Error retrieving ‘TotalCapacity’", ex);
            return -1;
        }
    }
}