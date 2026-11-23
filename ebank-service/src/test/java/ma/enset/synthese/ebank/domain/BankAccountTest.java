package ma.enset.synthese.ebank.domain;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
class BankAccountTest {
 @Test void depositAndWithdraw(){
  BankAccount a=new BankAccount(1L,AccountType.CURRENT,new BigDecimal("1000.00"));
  a.deposit(new BigDecimal("200.00"));a.withdraw(new BigDecimal("50.00"));
  assertEquals(0,new BigDecimal("1150.00").compareTo(a.getBalance()));
 }
 @Test void overdraftRejected(){
  BankAccount a=new BankAccount(2L,AccountType.SAVINGS,new BigDecimal("10.00"));
  assertThrows(IllegalArgumentException.class,()->a.withdraw(new BigDecimal("11.00")));
  assertEquals(0,new BigDecimal("10.00").compareTo(a.getBalance()));
 }
 @Test void nonpositiveAmountsRejected(){
  BankAccount a=new BankAccount(1L,AccountType.CURRENT,BigDecimal.ZERO);
  assertThrows(IllegalArgumentException.class,()->a.deposit(BigDecimal.ZERO));
  assertThrows(IllegalArgumentException.class,()->a.withdraw(new BigDecimal("-1")));
 }
}
