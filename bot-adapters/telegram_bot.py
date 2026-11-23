"""Telegram long polling, stdlib-only: python bot-adapters/telegram_bot.py.
Requires TELEGRAM_BOT_TOKEN and TELEGRAM_ALLOWED_CHAT_IDS.
Never run this bot with an unrestricted allowlist for real customer information.
"""
from __future__ import annotations
import json, os, time, urllib.request
from common import allowed_ids, ask_chatbot

TOKEN = os.environ.get('TELEGRAM_BOT_TOKEN', '').strip()
ALLOWED = allowed_ids('TELEGRAM_ALLOWED_CHAT_IDS')


def telegram(method: str, payload: dict) -> dict:
    data = json.dumps(payload).encode('utf-8')
    url = f'https://api.telegram.org/bot{TOKEN}/{method}'
    req = urllib.request.Request(url, data=data, headers={'Content-Type': 'application/json'},method='POST')
    with urllib.request.urlopen(req,timeout=55) as response:
        result = json.load(response)
        if not result.get('ok'):
            raise RuntimeError('Telegram API error: '+str(result.get('description')))
        return result


def main() -> None:
    if not TOKEN or not ALLOWED:
        raise SystemExit('Set TELEGRAM_BOT_TOKEN and TELEGRAM_ALLOWED_CHAT_IDS before starting.')
    offset = 0
    print('Telegram adapter running for allowlisted chats.')
    while True:
        try:
            updates = telegram('getUpdates', {'offset': offset, 'timeout': 30, 'allowed_updates': ['message']})
            for update in updates.get('result', []):
                offset = max(offset, int(update['update_id'])+1)
                msg = update.get('message', {})
                chat = msg.get('chat', {})
                chat_id = str(chat.get('id',''))
                text = str(msg.get('text','')).strip()
                if not chat_id or not text:
                    continue
                if chat_id not in ALLOWED:
                    telegram('sendMessage', {'chat_id': chat_id, 'text': 'Accès non autorisé.'})
                    continue
                answer = ('Bonjour ! Demandez-moi des informations sur les comptes.'
                          if text.startswith('/start') else ask_chatbot(text))
                telegram('sendMessage', {'chat_id': chat_id, 'text': answer})
        except Exception as exc:
            print('Telegram error:', type(exc).__name__, str(exc)[:120]); time.sleep(5)

if __name__ == '__main__':
    main()
