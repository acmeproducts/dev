# 05_openclaw_v1.ps1 - OpenClaw with credential placeholders
param([string]$DistroName = "oc-dev")
$ErrorActionPreference = "Stop"

if (-not $env:OPENCLAW_TOKEN) { Write-Error "Run setup.ps1 first"; exit 1 }

wsl -d $DistroName -u support -- bash -c "npm install -g openclaw@2026.5.6"
$ver = wsl -d $DistroName -u support -- bash -c "openclaw --version"
Write-Host "OpenClaw: $ver" -ForegroundColor Green

# Config with env vars
$config = @"
{"env":{"VENICE_API_KEY":"$env:VENICE_API_KEY","OPENROUTER_API_KEY":"$env:OPENROUTER_API_KEY"},"gateway":{"mode":"local","port":18789,"auth":{"mode":"token","token":"$env:OPENCLAW_TOKEN"}},"channels":{"whatsapp":{"enabled":true,"allowFrom":["$env:WHATSAPP_NUMBER"]}}}
"@
wsl -d $DistroName -u support -- bash -c "echo '$config' > /home/support/.openclaw/openclaw.json"

wsl -d $DistroName -u root -- bash -c "loginctl enable-linger support"
wsl -d $DistroName -u support -- bash -c "openclaw gateway install --force"
wsl -d $DistroName -u support -- bash -c "systemctl --user enable openclaw-gateway --now"
wsl -d $DistroName -u support -- bash -c "openclaw plugins install @openclaw/whatsapp@2026.5.6"
wsl -d $DistroName -u support -- bash -c "systemctl --user restart openclaw-gateway"

Write-Host "=== Step 5 Complete ===" -ForegroundColor Green
