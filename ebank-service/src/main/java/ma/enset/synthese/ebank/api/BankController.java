package ma.enset.synthese.ebank.api;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import ma.enset.synthese.ebank.domain.*;
import ma.enset.synthese.ebank.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
@RestController @RequestMapping("/api/accounts")
public class BankController {
 private final AccountService service;
 public BankController(AccountService service){this.service=service;}
 public record NewAccount(@NotNull Long customerId,@NotNull AccountType type,@NotNull @DecimalMin("0") BigDecimal initialBalance){}
 public record Amount(@NotNull @DecimalMin(value="0",inclusive=false) BigDecimal amount){}
 public record Transfer(@NotBlank String destinationAccountId,@NotNull @DecimalMin(value="0",inclusive=false) BigDecimal amount){}
 @GetMapping public List<BankAccount> all(@RequestParam(required=false) Long customerId){return customerId==null?service.list():service.forCustomer(customerId);}
 @GetMapping("/{id}") public BankAccount one(@PathVariable String id){return service.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public BankAccount create(@Valid @RequestBody NewAccount body){return service.create(body.customerId(),body.type(),body.initialBalance());}
 @PostMapping("/{id}/deposit") public BankAccount deposit(@PathVariable String id,@Valid @RequestBody Amount body){return service.deposit(id,body.amount());}
 @PostMapping("/{id}/withdraw") public BankAccount withdraw(@PathVariable String id,@Valid @RequestBody Amount body){return service.withdraw(id,body.amount());}
 @PostMapping("/{id}/transfer") public BankAccount transfer(@PathVariable String id,@Valid @RequestBody Transfer body){return service.transfer(id,body.destinationAccountId(),body.amount());}
 @GetMapping("/{id}/operations") public List<AccountOperation> operations(@PathVariable String id){return service.history(id);}
}
