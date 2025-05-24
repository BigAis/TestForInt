package com.dterz.service;

import com.dterz.dtos.AccountDTO;
import com.dterz.mappers.AccountMapper;
import com.dterz.model.Account;
import com.dterz.model.Transaction;
import com.dterz.model.TransactionType;
import com.dterz.model.User;
import com.dterz.repositories.AccountRepository;
import com.dterz.repositories.TransactionsRepository;
import com.dterz.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper mapper;
    private final UserRepository userRepository;
    private final TransactionsRepository transactionsRepository;

    /**
     * Gets all Accounts currently in the System.
     * The parameter is optional and if provided the accounts get filtered by User
     *
     * @param pageRequest number of active page and number of items per page
     * @param username    the username
     * @return ResponseEntity<Map < String, Object>>
     */
    public Map<String, Object> getAll(PageRequest pageRequest, String username) {
        User user = userRepository.findByUserName(username);
        Page<Account> page;
        if (user != null) {
            page = accountRepository.findByUserId(user.getId(), pageRequest);
        } else {
            page = accountRepository.findAll(pageRequest);
        }
        List<Account> acountList = page.getContent();
        Map<String, Object> response = new HashMap<>();
        List<AccountDTO> accountDTOS = mapper.entityListToDTOList(acountList);
        for (AccountDTO accountDTO : accountDTOS) {
            calculateBalance(accountDTO);
        }
        response.put("accounts", accountDTOS);
        response.put("currentPage", page.getNumber());
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        return response;
    }

    /**
     * Calculates the current balance of an Account based on its transactions
     *
     * @param accountDTO the Account we need the Balance for
     */
    private void calculateBalance(AccountDTO accountDTO) {
        if (accountDTO == null) {
            log.warn("Cannot calculate balance for null accountDTO");
            return;
        }

        final long start = System.currentTimeMillis();
        
        try {
            // Get all transactions for the account in a single query
            List<Transaction> allTransactions = transactionsRepository.findByAccountId(accountDTO.getId(), PageRequest.of(0, Integer.MAX_VALUE)).getContent();
            
            // Use Java streams to calculate the balance efficiently
            BigDecimal balance = allTransactions.stream()
                .filter(transaction -> transaction.getAmount() != null)
                .map(transaction -> {
                    if (TransactionType.INCOME.equals(transaction.getType())) {
                        return transaction.getAmount();
                    } else {
                        return transaction.getAmount().negate();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN); // Set consistent scale and rounding
            
            accountDTO.setCalculatedBalance(balance);
            log.debug("calculateBalance took {} ms for Account {}", (System.currentTimeMillis() - start), accountDTO.getId());
        } catch (Exception e) {
            log.error("Error calculating balance for account {}: {}", accountDTO.getId(), e.getMessage());
            accountDTO.setCalculatedBalance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN));
        }
    }

    /**
     * Gets an Acount by its id
     *
     * @param accountId the id of the Account to get
     * @return AccountDTO
     */
    public AccountDTO getAccountById(long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        AccountDTO accountDTO = mapper.entityToDto(account);
        calculateBalance(accountDTO);
        return accountDTO;
    }

    /**
     * Creates and returns a new empty Account Object
     *
     * @return AccountDTO
     */
    public AccountDTO draftAccount() {
        Account account = new Account();
        return mapper.entityToDto(account);
    }

    /**
     * Updates the Account with the data from the Front end
     *
     * @param accountDTO the updated Account
     * @return AccountDTO
     */
    public AccountDTO updateAccount(AccountDTO accountDTO) {
        Optional<Account> accountOpt = accountRepository.findById(accountDTO.getId());
        Account account;
        if (accountOpt.isPresent()) {
            account = accountOpt.get();
            mapper.dtoToEntity(accountDTO, account);
        } else {
            account = mapper.dtoToEntity(accountDTO);
        }
        accountRepository.save(account);
        return mapper.entityToDto(account);
    }
}