"""Small authenticated-operator client for the EBank chatbot's REST endpoint."""
from __future__ import annotations
import json, os, urllib.request, urllib.error

CHATBOT_ENDPOINT = os.getenv('CHATBOT_ENDPOINT', 'http://localhost:8083/api/chat/ask')

def allowed_ids(name: str) -> set[str]:
    return {s.strip() for s in os.getenv(name, '').split(',') if s.strip()}

def ask_chatbot(message: str) -> str:
    if not message.strip():
        return 'Veuillez écrire une question.'
    data = json.dumps({'message': message[:3000]}).encode('utf-8')
    request = urllib.request.Request(CHATBOT_ENDPOINT, data=data,
                                     headers={'Content-Type': 'application/json'}, method='POST')
    try:
        with urllib.request.urlopen(request, timeout=35) as response:
            result = json.load(response)
            return str(result.get('reply', 'Aucune réponse'))[:3900]
    except (urllib.error.HTTPError, urllib.error.URLError, TimeoutError, ValueError):
        return 'Service AI momentanément indisponible. Vérifiez le chatbot et les serveurs MCP.'
