package org.neutron.microedition;

import java.util.Map;
public interface ImplementationInitialization {
public static final String PARAM_EMULATOR_ID = "emulatorID";
public void registerImplementation(Map parameters);
public void notifyMIDletStart();
public void notifyMIDletDestroyed();
}
