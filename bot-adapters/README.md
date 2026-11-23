# Telegram / Discord — optional clients

The professor requests testing via **Telegram and Discord**. These adapters call the **same Spring AI REST chatbot** as the Angular client, so the MCP tools remain in the Java backend.

## Telegram

1. Create a bot with Telegram BotFather; save the token in a **local environment variable**, never in a source file.
2. Set `TELEGRAM_BOT_TOKEN` and `TELEGRAM_ALLOWED_CHAT_IDS` (comma-separated chat IDs). **An allowlist is mandatory**.
3. Run `python bot-adapters/telegram_bot.py` from the project root (or from within the folder).
4. Send `/start`, followed by `Liste les clients`.

## Discord

1. Create a Discord application/bot in the Developer Portal, enable **Message Content Intent**, and use a fresh private bot token.
2. Set `DISCORD_BOT_TOKEN` and `DISCORD_ALLOWED_USER_IDS` (comma-separated user IDs). **An allowlist is mandatory**.
3. `pip install -r bot-adapters/requirements.txt` and run `python bot-adapters/discord_bot.py`.
4. DM the bot or mention it in a server where it has access, then ask about accounts.

## Chatbot prerequisites

Start the `chatbot-service` with a freshly issued `OPENAI_API_KEY` and ensure both `/mcp` servers are running. Both adapters use `http://localhost:8083/api/chat/ask` unless `CHATBOT_ENDPOINT` is overridden.

**Privacy:** Tools are intentionally **read-only**. Do not connect real banking data or expose these examples publicly without authentication, fine-grained authorization, encryption, audit logs, and rate limits.
