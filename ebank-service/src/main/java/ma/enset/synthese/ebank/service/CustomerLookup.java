package ma.enset.synthese.ebank.service;
import ma.enset.synthese.ebank.client.CustomerClient;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
public class CustomerLookup {
 private final CustomerClient client;
 private final CircuitBreakerFactory<?,?> breakers;
 public CustomerLookup(CustomerClient client,CircuitBreakerFactory<?,?> breakers){this.client=client;this.breakers=breakers;}
 public CustomerClient.CustomerView find(Long customerId){
  return breakers.create("customerLookup").run(
    ()->client.find(customerId),
    ex->{throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Customer service unavailable or customer unknown",ex);}
  );
 }
}
