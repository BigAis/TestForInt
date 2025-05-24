package com.dterz;

import com.dterz.model.Account;
import com.dterz.model.Info;
import com.dterz.model.Transaction;
import com.dterz.model.TransactionType; // Assuming you have this enum
import com.dterz.model.User;
import com.dterz.repositories.AccountRepository;
import com.dterz.repositories.InfoRepository; // Assuming this repository exists
import com.dterz.repositories.TransactionsRepository;
import com.dterz.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseTests {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected AccountRepository accountRepository;

    @MockBean
    protected TransactionsRepository transactionsRepository;

    @MockBean
    protected InfoRepository infoRepository;

    protected User user;
    protected Account account;
    protected Info info;
    protected List<Transaction> income;
    protected List<Transaction> expences;
    protected List<Account> accounts;

    // Declare t1 and t2 as protected instance variables
    protected Transaction t1;
    protected Transaction t2;

    public BaseTests() {
        // Setup test data
        user = new User();
        user.setId(1L);
        user.setUserName("spring");
        user.setFistName("Spring");
        user.setSurName("Framework");
        user.setAge(30);

        account = new Account();
        account.setId(1L);
        account.setDescription("Test Account");

        info = new Info();
        info.setId(1L);
        info.setComponent("Backend");
        info.setVersion("1.0.0");

        // Initialize t1 here (now it's the instance variable)
        t1 = new Transaction();
        t1.setId(1L);
        t1.setAmount(BigDecimal.valueOf(100)); // Use BigDecimal directly
        t1.setType(TransactionType.INCOME);
        t1.setDescription("Test Income");
        t1.setDate(new Date());
        t1.setAccount(account); // Assign the account
        t1.setUser(user); // Assign the user

        // Initialize t2 here (now it's the instance variable)
        t2 = new Transaction();
        t2.setId(2L);
        t2.setAmount(BigDecimal.valueOf(75)); // Use BigDecimal directly
        t2.setType(TransactionType.EXPENSE);
        t2.setDescription("Test Expense");
        t2.setDate(new Date());
        t2.setAccount(account); // Assign the account
        t2.setUser(user); // Assign the user


        income = new ArrayList<>();
        income.add(t1);

        expences = new ArrayList<>();
        expences.add(t2);

        accounts = new ArrayList<>();
        accounts.add(account);
    }

    // Helper method to convert objects to JSON string
    protected String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}