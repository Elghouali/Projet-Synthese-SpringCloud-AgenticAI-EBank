package ma.enset.synthese.chatbot.api;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/chat")
public class ChatController {
 private final ChatClient chat;
 public ChatController(ChatClient.Builder builder,SyncMcpToolCallbackProvider tools,
       @Value("${app.chatbot.system-instructions:You are a banking assistant. Query MCP tools for account data; never invent account information. Read-only operations.}") String system){
  this.chat=builder.defaultSystem(system).defaultToolCallbacks(tools).build();
 }
 public record ChatRequest(@NotBlank @Size(max=3000) String message){}
 public record ChatReply(String reply){}
 @PostMapping("/ask") public ChatReply ask(@Valid @RequestBody ChatRequest request){
  try{
   String answer=chat.prompt().user(request.message()).call().content();
   return new ChatReply(answer==null?"No response from model":answer);
  }catch(Exception ex){
   throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"AI/MCP unavailable; check OPENAI_API_KEY and running MCP servers",ex);
  }
 }
 @GetMapping("/health") public java.util.Map<String,String> status(){return java.util.Map.of("status","configured");}
}
