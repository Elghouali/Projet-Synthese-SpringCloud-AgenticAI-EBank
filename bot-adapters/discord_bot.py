"""Optional Discord client; install `pip install -r bot-adapters/requirements.txt`.
Requires DISCORD_BOT_TOKEN, DISCORD_ALLOWED_USER_IDS, plus Message Content Intent
in the Discord Developer Portal. Processes direct messages and @mentions only.
"""
from __future__ import annotations
import asyncio, os, discord
from common import allowed_ids, ask_chatbot

TOKEN = os.getenv('DISCORD_BOT_TOKEN', '').strip()
ALLOWED = allowed_ids('DISCORD_ALLOWED_USER_IDS')

class BankBot(discord.Client):
    async def on_ready(self):
        print(f'Connected as {self.user} — allowlisted users only.')

    async def on_message(self, message: discord.Message):
        if message.author.bot or not self.user:
            return
        is_dm = isinstance(message.channel, discord.DMChannel)
        mention = self.user in message.mentions
        if not is_dm and not mention:
            return
        if str(message.author.id) not in ALLOWED:
            await message.channel.send('Accès non autorisé.'); return
        prompt = message.content.replace(f'<@{self.user.id}>', '').replace(f'<@!{self.user.id}>', '').strip()
        if not prompt:
            await message.channel.send('Posez-moi une question sur les clients ou comptes bancaires.'); return
        answer = await asyncio.to_thread(ask_chatbot, prompt)
        for index in range(0, len(answer), 1950):
            await message.channel.send(answer[index:index+1950])

if __name__ == '__main__':
    if not TOKEN or not ALLOWED:
        raise SystemExit('Set DISCORD_BOT_TOKEN and DISCORD_ALLOWED_USER_IDS before starting.')
    intents = discord.Intents.default(); intents.message_content = True
    BankBot(intents=intents).run(TOKEN)
