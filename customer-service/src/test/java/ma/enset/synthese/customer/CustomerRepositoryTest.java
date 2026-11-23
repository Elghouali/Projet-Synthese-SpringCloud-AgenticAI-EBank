package ma.enset.synthese.customer;
import ma.enset.synthese.customer.domain.Customer;
import ma.enset.synthese.customer.repo.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest(properties={"spring.cloud.config.enabled=false","eureka.client.enabled=false","spring.ai.mcp.server.enabled=false"})
class CustomerRepositoryTest {
 @Autowired CustomerRepository repo;
 @Test void storesAndFindsCustomers(){repo.save(new Customer("Tester","tester@example.test"));assertThat(repo.findByEmailIgnoreCase("TESTER@example.test")).isPresent();}
}
