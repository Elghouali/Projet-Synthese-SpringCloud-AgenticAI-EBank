package ma.enset.synthese.ebank.service;
import ma.enset.synthese.ebank.domain.*;
import ma.enset.synthese.ebank.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.*;
@Service
public class AccountService {
 private final AccountRepository accounts;
 private final OperationRepository operations;
 private final CustomerLookup customers;
 public AccountService(AccountRepository accounts,OperationRepository operations,CustomerLookup customers){
  this.accounts=accounts;this.operations=operations;this.customers=customers;
 }
 public List<BankAccount> list(){return accounts.findAll();}
 public BankAccount get(String id){return accounts.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found"));}
 public List<BankAccount> forCustomer(Long customerId){return accounts.findByCustomerId(customerId);}
 @Transactional public BankAccount create(Long customerId,AccountType type,BigDecimal balance){
  customers.find(customerId);
  BankAccount account=accounts.save(new BankAccount(customerId,type,balance));
  operations.save(new AccountOperation("CREATED",account.getId(),null,balance));return account;
 }
 private BankAccount locked(String id){return accounts.lockedById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found"));}
 @Transactional public BankAccount deposit(String id,BigDecimal amount){
  BankAccount account=locked(id);try{account.deposit(amount);}catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,ex.getMessage());}
  operations.save(new AccountOperation("DEPOSIT",id,null,amount));return account;
 }
 @Transactional public BankAccount withdraw(String id,BigDecimal amount){
  BankAccount account=locked(id);try{account.withdraw(amount);}catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.CONFLICT,ex.getMessage());}
  operations.save(new AccountOperation("WITHDRAW",id,null,amount));return account;
 }
 @Transactional public BankAccount transfer(String source,String destination,BigDecimal amount){
  if(source.equals(destination))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Source and destination must differ");
  try{BankAccount.requirePositive(amount);}catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,ex.getMessage());}
  // Acquire locks in deterministic order to reduce deadlocks between concurrent transfers.
  BankAccount first=locked(source.compareTo(destination)<0?source:destination);
  BankAccount second=locked(source.compareTo(destination)<0?destination:source);
  BankAccount sender=first.getId().equals(source)?first:second;
  BankAccount receiver=first.getId().equals(destination)?first:second;
  try{sender.withdraw(amount);receiver.deposit(amount);}catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.CONFLICT,ex.getMessage());}
  operations.save(new AccountOperation("TRANSFER",source,destination,amount));return sender;
 }
 public List<AccountOperation> history(String id){get(id);return operations.findBySourceIdOrDestinationIdOrderByOccurredAtDesc(id,id);}
}
