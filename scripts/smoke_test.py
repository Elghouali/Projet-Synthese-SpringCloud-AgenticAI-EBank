#!/usr/bin/env python3
"""Integration smoke tests after Eureka, services and Gateway have started."""
from __future__ import annotations
import json, sys, time, urllib.error, urllib.request
BASE = 'http://localhost:8080'

def call(path: str, method: str='GET', payload:dict|None=None):
    data=json.dumps(payload).encode('utf-8') if payload is not None else None
    request=urllib.request.Request(BASE+path,data=data,headers={'Content-Type':'application/json'},method=method)
    with urllib.request.urlopen(request,timeout=12) as reply:
        raw=reply.read();return json.loads(raw) if raw else None

def wait(path):
    for _ in range(35):
        try:return call(path)
        except (urllib.error.URLError, TimeoutError, ValueError):time.sleep(3)
    raise RuntimeError('Gateway unavailable: '+path)

def main():
    users=wait('/api/customers');assert users,'No seeded customers'
    acc=call('/api/accounts','POST',{'customerId':users[0]['id'],'type':'CURRENT','initialBalance':1000})
    account_id=acc['id']
    acc=call(f'/api/accounts/{account_id}/deposit','POST',{'amount':200});assert float(acc['balance'])==1200
    acc=call(f'/api/accounts/{account_id}/withdraw','POST',{'amount':50});assert float(acc['balance'])==1150
    ops=call(f'/api/accounts/{account_id}/operations')
    assert len(ops)>=3 and ops[0]['operationType']=='WITHDRAW'
    print('PASS: customer→Feign→ebank→Gateway; create/deposit/withdraw/history',account_id)

if __name__ == '__main__':
    try:main()
    except Exception as ex:print('FAIL:',ex,file=sys.stderr);sys.exit(1)
