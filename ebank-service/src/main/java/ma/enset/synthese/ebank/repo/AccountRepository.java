package ma.enset.synthese.ebank.repo;
import ma.enset.synthese.ebank.domain.BankAccount;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface AccountRepository extends JpaRepository<BankAccount,String>{
 List<BankAccount> findByCustomerId(Long customerId);
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select a from BankAccount a where a.id = :id")
 Optional<BankAccount> lockedById(String id);
}
