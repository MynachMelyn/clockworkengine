
package com.clockwork.system.ios;

import com.clockwork.app.Application;
import com.clockwork.system.CWSystem;

/**
 */
public abstract class IosHarness extends ObjcNativeObject {

    protected Application app;

    public IosHarness(long appDelegate) {
        super(appDelegate);
        CWSystem.setSystemDelegate(new CWIosSystem());
    }

    public abstract void appPaused();

    public abstract void appReactivated();

    public abstract void appClosed();

    public abstract void appUpdate();

    public abstract void appDraw();
    
    public abstract void appReshape(int width, int height);

}