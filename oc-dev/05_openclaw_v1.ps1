# 05_openclaw_v1.ps1 - OpenClaw installation with credential placeholders
param([string]$DistroName = "oc-dev")
$ErrorActionPreference = "Stop"

# Validate required environment variables
$required = @("VENICE_API_KEY", "OPENROUTER_API_KEY", "OPENCLAW_TOKEN", "WHATSAPP_NUMBER")
foreach ($var in $required) {
    if (-not $env:$var) {
        Write-Error "$var not set. Run setup.ps1 first."
        exit 1
    }
}

Write-Host "=== Installing OpenClaw ===" -ForegroundColor Cyan

# Install OpenClaw
wsl -d $DistroName -u support -- bash -c "npm install -g openclaw@2026.5.6"

$ver = wsl -d $DistroName -u support -- bash -c "openclaw --version"
Write-Host "OpenClaw version: $ver" -ForegroundColor Green

# Create config using environment variables
$venice = $env:VENICE_API_KEY
$openrouter = $env:OPENROUTER_API_KEY
$token = $env:OPENCLAW_TOKEN
$whatsapp = $env:WHATSAPP_NUMBER

$openclawConfig = @"
{
  "env": {
    "VENICE_API_KEY": "$venice",
    "OPENROUTER_API_KEY": "$openrouter",
    "shellEnv": { "enabled": true, "timeoutMs": 15000 }
  },
  "gateway": {
    "mode": "local",
    "port": 18789,
    "bind": "localhost",
    "auth": { "mode": "token", "token": "$token" }
  },
  "channels": {
    "whatsapp": {
      "enabled": true,
      "dmPolicy": "allowlist",
      "allowFrom": ["$whatsapp"],
      "groups": {"*": {"requirement": true}},
      "accounts": {"default": {"enabled": true}},
      "groupPolicy": "allowlist",
      "selfChatMode": true,
      "sendReadReceipts": true
    }
  }
}
"@

# Write config
wsl -d $DistroName -u support -- bash -c "cat > /home/support/.openclaw/openclaw.json << EOFCONFIG
$openclawConfig
EOFCONFIG"

# Validate
wsl -d $DistroName -u support -- bash -c "openclaw config validate"

# Enable linger and install gateway
wsl -d $DistroName -u root -- bash -c "loginctl enable-linger support"
wsl -d $DistroName -u support -- bash -c "openclaw gateway install --force"
wsl -d $DistroName -u support -- bash -c "systemctl --user daemon-reload; systemctl --user enable openclaw-gateway; systemctl --user start openclaw-gateway"

Start-Sleep -Seconds 5

# Install WhatsApp plugin
wsl -d $DistroName -u support -- bash -c "openclaw plugins install @openclaw/whatsapp@2026.5.6"
wsl -d $DistroName -u support -- bash -c "systemctl --user restart openclaw-gateway"

Write-Host "OpenClaw gateway running on port 18789" -ForegroundColor Green
Write-Host "=== Step 5 Complete ===" -ForegroundColor Green
Write-Host "Next: Run 'openclaw channels login --channel whatsapp' to scan QR code" -ForegroundColor Cyan
