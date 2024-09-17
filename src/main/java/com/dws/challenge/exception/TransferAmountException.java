package com.dws.challenge.exception;

public class TransferAmountException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public TransferAmountException(String message) {
	    super("Input Invalid, " + message);
	  }
}
