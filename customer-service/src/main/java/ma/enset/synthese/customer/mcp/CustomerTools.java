package ma.enset.synthese.customer.mcp;
import ma.enset.synthese.customer.domain.Customer;
import ma.enset.synthese.customer.repo.CustomerRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class CustomerTools {
 private final CustomerRepository repo;
 public CustomerTools(CustomerRepository repo){this.repo=repo;}
 @Tool(description="List all bank customers (id, name, email), read-only")
 public List<Customer> listCustomers(){return repo.findAll();}
 @Tool(description="Find one bank customer by numeric ID, read-only")
 public Customer getCustomer(@ToolParam(description="Numeric customer ID") Long customerId){return repo.findById(customerId).orElse(null);}
 @Configuration static class Registration {
  @Bean ToolCallbackProvider customerToolCallbacks(CustomerTools tools){return MethodToolCallbackProvider.builder().toolObjects(tools).build();}
 }
}
