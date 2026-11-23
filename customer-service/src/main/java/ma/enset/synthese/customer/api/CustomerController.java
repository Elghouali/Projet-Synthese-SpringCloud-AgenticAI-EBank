package ma.enset.synthese.customer.api;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import ma.enset.synthese.customer.domain.Customer;
import ma.enset.synthese.customer.repo.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/customers")
public class CustomerController {
 private final CustomerRepository repository;
 public CustomerController(CustomerRepository repository){this.repository=repository;}
 public record CustomerInput(@NotBlank String name,@NotBlank @Email String email){}
 @GetMapping public List<Customer> list(){return repository.findAll();}
 @GetMapping("/{id}") public Customer get(@PathVariable Long id){return repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer not found"));}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Customer create(@Valid @RequestBody CustomerInput input){
  String email=input.email().trim();
  if(repository.findByEmailIgnoreCase(email).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Email exists");
  return repository.save(new Customer(input.name().trim(),email));
 }
 @PutMapping("/{id}") public Customer update(@PathVariable Long id,@Valid @RequestBody CustomerInput input){
  Customer c=get(id);
  repository.findByEmailIgnoreCase(input.email().trim()).filter(existing->!existing.getId().equals(id)).ifPresent(x->{throw new ResponseStatusException(HttpStatus.CONFLICT,"Email exists");});
  c.setName(input.name().trim());c.setEmail(input.email().trim());return repository.save(c);
 }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){
  if(!repository.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer not found");repository.deleteById(id);
 }
}
