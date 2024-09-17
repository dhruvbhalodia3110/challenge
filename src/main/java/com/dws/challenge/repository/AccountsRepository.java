package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferMoney;
import com.dws.challenge.domain.TransferResponse;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.NotificationService;

public interface AccountsRepository {

	void createAccount(Account account) throws DuplicateAccountIdException;

	Account getAccount(String accountId);

	void clearAccounts();

	TransferResponse transferAmount(TransferMoney transferMoney, NotificationService notifyService);
}
