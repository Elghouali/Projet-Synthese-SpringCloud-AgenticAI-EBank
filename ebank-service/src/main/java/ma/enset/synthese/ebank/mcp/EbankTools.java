package ma.enset.synthese.ebank.mcp;
import ma.enset.synthese.ebank.domain.*;
import ma.enset.synthese.ebank.service.AccountService;
import org.springframework.ai.tool.annotation.*;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class EbankTools {
 private final AccountService accounts;
 public EbankTools(AccountService accounts){this.accounts=accounts;}
 @Tool(description="List existing bank accounts including IDs, customer IDs, types and balances. Read-only.")
 public List<BankAccount> listBankAccounts(){return accounts.list();}
 @Tool(description="Get exact current balance and type for a bank account by its UUID. Read-only.")
 public BankAccount getBankAccount(@ToolParam(description="Bank account UUID") String accountId){return accounts.get(accountId);}
 @Tool(description="List all bank accounts owned by a customer, by numeric customer ID. Read-only.")
 public List<BankAccount> accountsForCustomer(@ToolParam(description="Numeric customer ID") Long customerId){return accounts.forCustomer(customerId);}
 @Tool(description="List bank transactions/operations for one account UUID. Read-only.")
 public List<AccountOperation> getAccountOperations(@ToolParam(description="Bank account UUID") String accountId){return accounts.history(accountId);}
 @Configuration static class Registration {
  @Bean ToolCallbackProvider bankToolCallbacks(EbankTools tools){return MethodToolCallbackProvider.builder().toolObjects(tools).build();}
 }
}
