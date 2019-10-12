package org.std.framework.exceptiom;

public class CustomException extends RuntimeException{
	private static final long serialVersionUID = 3206531128875711182L;

	public CustomException(String message) {
		super(message);
	}
}
