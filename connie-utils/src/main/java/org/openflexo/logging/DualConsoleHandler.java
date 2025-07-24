package org.openflexo.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.StreamHandler;

public class DualConsoleHandler extends StreamHandler {

	private final ConsoleHandler stderrHandler = new ConsoleHandler();

	public DualConsoleHandler() {
		super(System.out, new FlexoLoggingFormatter());
	}

	@Override
	public void publish(java.util.logging.LogRecord record) {
		if (record.getLevel().intValue() <= Level.INFO.intValue()) {
			super.publish(record);
			super.flush();
		}
		else {
			stderrHandler.publish(record);
			stderrHandler.flush();
		}
	}
}
