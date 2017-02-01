
package com.clockwork.system.ios;

/**
 * Java Object that represents a native iOS class. You can call methods and
 * retrieve objects from the native object via this class.
 *
 */
public class ObjcNativeObject {

    private final long nativeObject;

    /**
     * Creates a new native object representation
     *
     * @param nativeObject The id of the native object, created via cast of
     * object to jlong
     */
    public ObjcNativeObject(long nativeObject) {
        this.nativeObject = nativeObject;
    }

    /**
     * Performs the given selector on the native AppDelegate, equivalent to
     * calling a method in java.
     *
     * @param selector The selector (name of the method) to perform.
     * @return The object in form of a long id if any is returned.
     */
    public ObjcNativeObject performSelector(String selector) {
        return new ObjcNativeObject(performSelector(nativeObject, selector));
    }

    /**
     * Performs the given selector on the native AppDelegate, equivalent to
     * calling a method in java.
     *
     * @param selector The selector (name of the method) to perform.
     * @param object An object that was before returned from native code.
     * @return The object in form of a long id if any is returned.
     */
    public ObjcNativeObject performSelectorWithObject(String selector, ObjcNativeObject object) {
        return new ObjcNativeObject(performSelectorWithObject(nativeObject, selector, object.nativeObject));
    }

    /**
     * Performs the given selector on the native AppDelegate, run it on the main
     * thread.
     *
     * @param selector The selector (name of the method) to perform.
     */
    public void performSelectorOnMainThread(String selector) {
        performSelectorOnMainThread(nativeObject, selector);
    }

    /**
     * Performs the given selector on the native AppDelegate, run it on the main
     * thread.
     *
     * @param selector The selector (name of the method) to perform.
     * @param object An object that was before returned from native code.
     */
    public void performSelectorOnMainThreadWithObject(String selector, ObjcNativeObject object) {
        performSelectorOnMainThreadWithObject(nativeObject, selector, object.nativeObject);
    }

    /**
     * Performs the given selector on the native AppDelegate, run it in the
     * background.
     *
     * @param selector The selector (name of the method) to perform.
     */
    public void performSelectorInBackground(String selector) {
        performSelectorInBackground(nativeObject, selector);
    }

    /**
     * Performs the given selector on the native AppDelegate, run it in the
     * background
     *
     * @param selector The selector (name of the method) to perform.
     * @param object An object that was before returned from native code.
     */
    public void performSelectorInBackgroundWithObject(String selector, ObjcNativeObject object) {
        performSelectorInBackgroundWithObject(nativeObject, selector, object.nativeObject);
    }

    private static native long performSelector(long nativeObject, String selector);

    private static native long performSelectorWithObject(long nativeObject, String selector, long object);

    private static native void performSelectorOnMainThread(long nativeObject, String selector);

    private static native void performSelectorOnMainThreadWithObject(long nativeObject, String selector, long object);

    private static native void performSelectorInBackground(long nativeObject, String selector);

    private static native void performSelectorInBackgroundWithObject(long nativeObject, String selector, long object);
}
