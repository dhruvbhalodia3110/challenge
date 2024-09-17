package com.dws.challenge.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class TransferResponse {
	
	private String status;
	
	private  BigDecimal amount;

	private  String fromAccount;

	
	private  String toAccount;
	
	private LocalDateTime timestamp;

	public TransferResponse(String status, BigDecimal amount, String fromAccount, String toAccount,
			LocalDateTime timestamp) {
		super();
		this.status = status;
		this.amount = amount;
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "TransferResponse [status=" + status + ", amount=" + amount + ", fromAccount=" + fromAccount
				+ ", toAccount=" + toAccount + ", timestamp=" + timestamp + "]";
	}
	

	
	

}
