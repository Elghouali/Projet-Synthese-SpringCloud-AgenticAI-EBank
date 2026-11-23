#!/usr/bin/env python3
"""Simple MCP handshake + tools/list test against Spring AI's Streamable HTTP."""
import json,sys,urllib.request

def exchange(port,method,params=None,session=None,notification=False):
    payload={'jsonrpc':'2.0','method':method}
    if not notification:
        payload['id']=1
    if params is not None:
        payload['params']=params
    req=urllib.request.Request(f'http://localhost:{port}/mcp',data=json.dumps(payload).encode(),headers={
      'Content-Type':'application/json','Accept':'application/json, text/event-stream',
      'MCP-Protocol-Version':'2025-03-26', **({'Mcp-Session-Id':session} if session else {})},method='POST')
    with urllib.request.urlopen(req,timeout=12) as response:
        body=response.read().decode(); sid=response.headers.get('Mcp-Session-Id')
        return body,sid
for port in (8081,8082):
    try:
        body,sid=exchange(port,'initialize',{'protocolVersion':'2025-03-26','capabilities':{},'clientInfo':{'name':'smoke-test','version':'1.0'}})
        assert 'result' in body
        exchange(port,'notifications/initialized',session=sid,notification=True)
        data,_=exchange(port,'tools/list',{},sid)
        assert 'tools' in data
        print('PASS: MCP tools/list on',port)
    except Exception as ex:print('FAIL: MCP server',port,str(ex),file=sys.stderr);sys.exit(1)
