package ma.enset.synthese.ebank.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="account_operations",indexes={@Index(name="idx_op_source",columnList="sourceId")})
public class AccountOperation {
 @Id private String id;
 @Column(nullable=false) private String operationType;
 @Column(nullable=false) private String sourceId;
 private String destinationId;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
 @Column(nullable=false) private Instant occurredAt;
 protected AccountOperation(){}
 public AccountOperation(String type,String sourceId,String destinationId,BigDecimal amount){
  this.id=UUID.randomUUID().toString();this.operationType=type;this.sourceId=sourceId;this.destinationId=destinationId;this.amount=amount;this.occurredAt=Instant.now();
 }
 public String getId(){return id;}public String getOperationType(){return operationType;}
 public String getSourceId(){return sourceId;}public String getDestinationId(){return destinationId;}
 public BigDecimal getAmount(){return amount;}public Instant getOccurredAt(){return occurredAt;}
}
