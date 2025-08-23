package org.neutron.microedition.io;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.microedition.io.Connection;

import org.neutron.log.Logger;
public class ConnectionInvocationHandler implements InvocationHandler {

	private Connection originalConnection; 
private AccessControlContext acc;
    
	static {
		Logger.addLogOrigin(ConnectionInvocationHandler.class);
	}
	
	public ConnectionInvocationHandler(Connection con, boolean needPrivilegedCalls) {
		this.originalConnection = con;
		if (needPrivilegedCalls) {
			this.acc = AccessController.getContext();
		}
	}
public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		if (ConnectorImpl.debugConnectionInvocations) {
			Logger.debug("invoke", method);
		}
		try {
			if (this.acc != null) {
			return AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws InvocationTargetException, IllegalAccessException {
					return method.invoke(originalConnection, args);
				}
			}, acc);
			} else {
				return method.invoke(this.originalConnection, args);
			}
		} catch (PrivilegedActionException e) {
			if (e.getCause() instanceof InvocationTargetException) {
				if (ConnectorImpl.debugConnectionInvocations) {
	        		Logger.error("Connection." + method.getName(), e.getCause().getCause());
	        	}
				throw e.getCause().getCause();
			} else {
				if (ConnectorImpl.debugConnectionInvocations) {
	        		Logger.error("Connection." + method.getName(), e.getCause());
	        	}
				throw e.getCause();
			}
        } catch (InvocationTargetException e) {
        	if (ConnectorImpl.debugConnectionInvocations) {
        		Logger.error("Connection." + method.getName(), e.getCause());
        	}
            throw e.getCause();
        }
	}

}
