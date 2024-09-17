package com.dws.challenge.exception;

public class InvalidAccountIdException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidAccountIdException(String message) {
	    super("Input Invalid, " + message + " not available.");
	  }

}
