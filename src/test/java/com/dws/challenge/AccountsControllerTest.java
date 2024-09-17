package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.dws.challenge.domain.Account;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.EmailNotificationService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
@TestMethodOrder(OrderAnnotation.class)
class AccountsControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;
  
  @Mock
  private EmailNotificationService notificationService;

  @BeforeEach
  void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  @Order(1)
  void createAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    Account account = accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
  }
  
  @Test
  @Order(2)
  void createSecondAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());

    Account account = accountsService.getAccount("Id-456");
    assertThat(account.getAccountId()).isEqualTo("Id-456");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
  }

  @Test
  @Order(3)
  void createDuplicateAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  @Order(4)
  void createAccountNoAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  @Order(5)
  void createAccountNoBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\"}")).andExpect(status().isBadRequest());
  }

  @Test
  @Order(6)
  void createAccountNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  @Order(7)
  void createAccountNegativeBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
  }

  @Test
  @Order(8)
  void createAccountEmptyAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  @Order(9)
  void getAccount() throws Exception {
    String uniqueAccountId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
    this.accountsService.createAccount(account);
    this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId))
      .andExpect(status().isOk())
      .andExpect(
        content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
  }
  
  @Test
  @Order(10)
  void performTranfer() throws Exception {
	  this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
		      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
	  
	  this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
		      .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
	  
    this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
      .content("{ \"accountFromId\": \"Id-123\", \"accountToId\": \"Id-456\", \"amount\": 1000 }")).andExpect(status().is2xxSuccessful());
  }
  
  @Test
  @Order(11)
  void performTranferNegative() throws Exception {
	  
    this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
      .content("{ \"accountFromId\": \"Id-123\", \"accountToId\": \"Id-456\", \"amount\": 1000 }")).andExpect(status().isNotFound());
  }
  
  @Test
  @Order(12)
  void performTranferNoBody() throws Exception {
	  
    this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
      .content("")).andExpect(status().isBadRequest());
  }
  
  @Test
  @Order(12)
  void performTranferNegativeAmount() throws Exception {
	  
    this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
      .content("{ \"accountFromId\": \"Id-123\", \"accountToId\": \"Id-456\", \"amount\": -1000 }")).andExpect(status().isBadRequest());
  }
  
  @Test
  @Order(13)
  void performTranferNoFromAccount() throws Exception {
	  
    this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
      .content("{  \"accountToId\": \"Id-456\", \"amount\": -1000 }")).andExpect(status().isBadRequest());
  }
  
  @Test
  @Order(14)
  void performTranferNoToAccount() throws Exception {
	  
    this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
      .content("{ \"accountFromId\": \"Id-123\", \"amount\": -1000 }")).andExpect(status().isBadRequest());
  }
  
  @Test
  @Order(15)
  void performTranferNoAmount() throws Exception {
	  
    this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
      .content("{ \"accountFromId\": \"Id-123\", \"accountToId\": \"Id-456\" }")).andExpect(status().isBadRequest());
  }
  
}
