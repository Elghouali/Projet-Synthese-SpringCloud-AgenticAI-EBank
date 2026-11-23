import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';

type Customer = {id:number;name:string;email:string};
type Account = {id:string;customerId:number;type:'CURRENT'|'SAVINGS';balance:number;createdAt:string};
type Operation = {id:string;operationType:string;sourceId:string;destinationId:string|null;amount:number;occurredAt:string};
type Tab = 'overview'|'customers'|'accounts'|'chat';
@Component({selector:'app-root',standalone:true,imports:[CommonModule,FormsModule],templateUrl:'./app.component.html'})
export class AppComponent implements OnInit {
 tab:Tab='overview'; customers:Customer[]=[]; accounts:Account[]=[];
 operations:Operation[]=[]; currentAccount:Account|null=null;
 customerForm={name:'',email:''};accountForm:{customerId:number|null;type:'CURRENT'|'SAVINGS';initialBalance:number}={customerId:null,type:'CURRENT',initialBalance:1000};
 operationForm:{type:'deposit'|'withdraw'|'transfer';amount:number;destination:string}={type:'deposit',amount:100,destination:''};
 userMessage='';chatMessages:{role:'user'|'assistant';text:string}[]=[];
 processing=false;notice='';error='';chatLoading=false;aiReady=false;
 constructor(private http:HttpClient){}
 ngOnInit(){void this.reload();}
 get totalBalance(){return this.accounts.reduce((a,b)=>a+Number(b.balance),0);}
 get currentCount(){return this.accounts.filter(a=>a.type==='CURRENT').length;}
 customerName(id:number){return this.customers.find(c=>c.id===id)?.name??`Client #${id}`;}
 money(x:number){return new Intl.NumberFormat('fr-MA',{style:'currency',currency:'MAD'}).format(x||0);}
 async reload(){
  this.error='';
  try{
   const [cs,as]=await Promise.all([
    firstValueFrom(this.http.get<Customer[]>('/api/customers')),
    firstValueFrom(this.http.get<Account[]>('/api/accounts'))
   ]);
   this.customers=cs;this.accounts=as;
   if(this.currentAccount){this.currentAccount=this.accounts.find(a=>a.id===this.currentAccount?.id)??null;}
   if(this.accountForm.customerId===null && cs.length)this.accountForm.customerId=cs[0].id;
  }catch(e){this.error='Connexion au backend impossible. Démarrez Eureka, les services et Gateway.';}
 }
 private fail(e:unknown){
  const x=e as {error?:{detail?:string;message?:string;error?:string}};
  this.error=x?.error?.detail||x?.error?.message||x?.error?.error||'Opération échouée. Consultez les logs du service.';
 }
 async addCustomer(){if(this.processing)return;this.processing=true;this.error='';this.notice='';
  try{await firstValueFrom(this.http.post('/api/customers',this.customerForm));this.customerForm={name:'',email:''};this.notice='Client créé.';await this.reload();}
  catch(e){this.fail(e);}finally{this.processing=false;}
 }
 async addAccount(){if(this.processing||this.accountForm.customerId===null)return;this.processing=true;this.error='';this.notice='';
  try{await firstValueFrom(this.http.post('/api/accounts',this.accountForm));this.notice='Compte créé.';await this.reload();}
  catch(e){this.fail(e);}finally{this.processing=false;}
 }
 async selectAccount(account:Account){this.currentAccount=account;this.tab='accounts';
  try{this.operations=await firstValueFrom(this.http.get<Operation[]>(`/api/accounts/${encodeURIComponent(account.id)}/operations`));}
  catch(e){this.fail(e);}
 }
 async operate(){if(!this.currentAccount||this.processing)return;this.processing=true;this.error='';this.notice='';
  const id=encodeURIComponent(this.currentAccount.id);
  const suffix=this.operationForm.type==='transfer'?'transfer':this.operationForm.type;
  const body=this.operationForm.type==='transfer'?{amount:this.operationForm.amount,destinationAccountId:this.operationForm.destination}:{amount:this.operationForm.amount};
  try{await firstValueFrom(this.http.post(`/api/accounts/${id}/${suffix}`,body));this.notice='Opération validée.';await this.reload();if(this.currentAccount)await this.selectAccount(this.currentAccount);}
  catch(e){this.fail(e);}finally{this.processing=false;}
 }
 async sendMessage(){const message=this.userMessage.trim();if(!message||this.chatLoading)return;this.userMessage='';this.chatLoading=true;this.error='';
  this.chatMessages.push({role:'user',text:message});
  try{const resp=await firstValueFrom(this.http.post<{reply:string}>('/api/chat/ask',{message}));this.chatMessages.push({role:'assistant',text:resp.reply});}
  catch(e){this.chatMessages.push({role:'assistant',text:'Chatbot indisponible. Démarrez le profil AI et configurez une nouvelle clé OpenAI.'});}
  finally{this.chatLoading=false;}
 }
 formatId(id:string){return `${id.slice(0,8)}…${id.slice(-4)}`;}
}
