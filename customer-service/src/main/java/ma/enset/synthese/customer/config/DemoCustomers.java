package ma.enset.synthese.customer.config;
import ma.enset.synthese.customer.domain.Customer;
import ma.enset.synthese.customer.repo.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
@Configuration
public class DemoCustomers {
 @Bean CommandLineRunner seedCustomers(CustomerRepository repo){return args->{if(repo.count()==0){repo.save(new Customer("Amina Berrada","amina@example.test"));repo.save(new Customer("Youssef Alaoui","youssef@example.test"));}};}
}
