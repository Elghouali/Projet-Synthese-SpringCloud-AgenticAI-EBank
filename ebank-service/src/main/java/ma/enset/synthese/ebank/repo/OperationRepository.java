package ma.enset.synthese.ebank.repo;
import ma.enset.synthese.ebank.domain.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface OperationRepository extends JpaRepository<AccountOperation,String>{
 List<AccountOperation> findBySourceIdOrDestinationIdOrderByOccurredAtDesc(String sourceId,String destinationId);
}
