package ma.enset.synthese.ebank.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="bank_accounts")
public class BankAccount {
 @Id private String id;
 @Column(nullable=false) private Long customerId;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountType type;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal balance;
 @Column(nullable=false) private Instant createdAt;
 @Version private Long version;
 protected BankAccount(){}
 public BankAccount(Long customerId,AccountType type,BigDecimal initialBalance){
  if(customerId==null || type==null || initialBalance==null || initialBalance.signum()<0)throw new IllegalArgumentException("Invalid account creation");
  this.id=UUID.randomUUID().toString();this.customerId=customerId;this.type=type;this.balance=initialBalance;this.createdAt=Instant.now();
 }
 public String getId(){return id;} public Long getCustomerId(){return customerId;}
 public AccountType getType(){return type;} public BigDecimal getBalance(){return balance;}
 public Instant getCreatedAt(){return createdAt;}
 public void deposit(BigDecimal amount){requirePositive(amount);balance=balance.add(amount);}
 public void withdraw(BigDecimal amount){requirePositive(amount);if(balance.compareTo(amount)<0)throw new IllegalArgumentException("Insufficient funds");balance=balance.subtract(amount);}
 public static void requirePositive(BigDecimal amount){if(amount==null || amount.signum()<=0)throw new IllegalArgumentException("Amount must be positive");}
}
