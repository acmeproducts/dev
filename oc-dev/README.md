# OpenClaw Environment Setup (GitHub Version)

**⚠️ IMPORTANT: This version requires credential configuration before use. No API keys are embedded.**

## Quick Start

1. **Clone and configure:**
   ```powershell
   git clone https://github.com/acmeproducts/dev.git
   cd dev/oc-dev
   powershell -ExecutionPolicy Bypass -File "setup.ps1"
   ```

2. **Run:**
   ```powershell
   powershell -ExecutionPolicy Bypass -File "wrapper.ps1"
   ```

## Required Credentials

| Variable | Description | Where to Get |
|----------|-------------|--------------|
| TS_AUTH_KEY | Tailscale auth key | https://login.tailscale.com/admin/settings/keys |
| VENICE_API_KEY | Venice AI API key | Venice Dashboard |
| OPENROUTER_API_KEY | OpenRouter API key | https://openrouter.ai/keys |
| OPENCLAW_TOKEN | OpenClaw token | OpenClaw configuration |
| WHATSAPP_NUMBER | WhatsApp number | Your number (+12345678901) |

## What Gets Installed

- WSL2 Ubuntu 24.04 with systemd
- Node.js 20, Python 3, SSH server
- Tailscale VPN
- Report server (port 18080)
- OpenClaw 2026.5.6 with WhatsApp plugin

## Access After Setup

- SSH: `ssh support@oc-dev.fell-dojo.ts.net`
- Report: `https://oc-dev.fell-dojo.ts.net/report/`

## Security

- `secrets.ps1` is in `.gitignore` and ignored by git
- Never commit credentials to the repository
- Rotate API keys periodically
