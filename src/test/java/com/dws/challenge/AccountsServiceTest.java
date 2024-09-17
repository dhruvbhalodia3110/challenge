package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferMoney;
import com.dws.challenge.domain.TransferResponse;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InvalidAccountIdException;
import com.dws.challenge.exception.TransferAmountException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.EmailNotificationService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;
  
  @Mock
  private EmailNotificationService notificationService;

  @Test
  void addAccount() {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }
  
  @Test
  void addSecondAccount() {
    Account account = new Account("Id-456");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-456")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }
  
  @Test
  void testTransferAmountSuccess() {
      // Arrange
      TransferMoney transferMoney = new TransferMoney("Id-123", "Id-456", new BigDecimal("200"));
      

      // Act
      TransferResponse result = accountsService.transferAmount(transferMoney, notificationService);

      // Assert
      assertEquals("Success", result.getStatus());
     
  }
  
  
  @Test
  void testTransferAmountInsufficientBalance() {
      // Arrange
	  TransferMoney transferMoney = new TransferMoney("Id-123", "Id-456", new BigDecimal("1200"));
      


      // Act & Assert
      TransferAmountException exception = assertThrows(TransferAmountException.class, () -> {
    	  accountsService.transferAmount(transferMoney, notificationService);
      });
      assertEquals("Input Invalid, Insufficient balance to transfer the amount", exception.getMessage());

  }

  @Test
  void testTransferAmountInvalidFromAccount() {
      // Arrange
      TransferMoney transferMoney = new TransferMoney("123", "456", new BigDecimal("200"));

      // Act & Assert
      InvalidAccountIdException exception = assertThrows(InvalidAccountIdException.class, () -> {
    	  accountsService.transferAmount(transferMoney, notificationService);
      });
      assertEquals("Input Invalid, Account From ID not available.", exception.getMessage());
     
  }

  @Test
  void testTransferAmountInvalidToAccount() {
      // Arrange
      TransferMoney transferMoney = new TransferMoney("Id-123", "456", new BigDecimal("200"));

      // Act & Assert
      InvalidAccountIdException exception = assertThrows(InvalidAccountIdException.class, () -> {
    	  accountsService.transferAmount(transferMoney, notificationService);
      });
      assertEquals("Input Invalid, Account To ID not available.", exception.getMessage());

  }
  

}
