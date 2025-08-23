package org.neutron.log;

public interface LoggerAppender {

	public void append(LoggingEvent event);
}
