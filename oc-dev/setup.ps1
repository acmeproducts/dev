# setup.ps1 - Prompt for credentials and validate before running setup
param([string]$SecretsFile = "$PSScriptRoot\secrets.ps1")

$ErrorActionPreference = "Stop"

function Write-Header { param([string]$msg) Write-Host "`n=== $msg ===" -ForegroundColor Cyan }
function Write-Success { param([string]$msg) Write-Host "[OK] $msg" -ForegroundColor Green }
function Write-Error { param([string]$msg) Write-Host "[ERR] $msg" -ForegroundColor Red }

Write-Header "OpenClaw Environment Setup - Credential Configuration"

$credentials = @(
    @{ Name = "TS_AUTH_KEY"; Prompt = "Tailscale Auth Key"; Pattern = '^tskey-auth-[A-Za-z0-9]+$' },
    @{ Name = "VENICE_API_KEY"; Prompt = "Venice AI API Key"; Pattern = '^VENICE_INFERENCE_KEY_[A-Za-z0-9_-]+$' },
    @{ Name = "OPENROUTER_API_KEY"; Prompt = "OpenRouter API Key"; Pattern = '^sk-or-v1-[a-f0-9]+$' },
    @{ Name = "OPENCLAW_TOKEN"; Prompt = "OpenClaw Gateway Token"; Pattern = '^[a-f0-9]{64}$' },
    @{ Name = "WHATSAPP_NUMBER"; Prompt = "WhatsApp Number (e.g., +19497776846)"; Pattern = '^\+\d{10,15}$' }
)

$secrets = @{}

foreach ($cred in $credentials) {
    $value = Read-Host -Prompt "Enter $($cred.Prompt)"
    
    if ($value -match $cred.Pattern) {
        $secrets[$cred.Name] = $value
        Write-Success "$($cred.Name) configured"
    } else {
        Write-Error "Invalid format for $($cred.Name)"
        $retry = Read-Host "Continue anyway? (y/N)"
        if ($retry -ne 'y') { exit 1 }
        $secrets[$cred.Name] = $value
    }
}

$secretsContent = @"
# OpenClaw Environment Secrets
$env:TS_AUTH_KEY = "$($secrets.TS_AUTH_KEY)"
$env:VENICE_API_KEY = "$($secrets.VENICE_API_KEY)"
$env:OPENROUTER_API_KEY = "$($secrets.OPENROUTER_API_KEY)"
$env:OPENCLAW_TOKEN = "$($secrets.OPENCLAW_TOKEN)"
$env:WHATSAPP_NUMBER = "$($secrets.WHATSAPP_NUMBER)"
"@

Set-Content -Path $SecretsFile -Value $secretsContent
Write-Success "Secrets saved to: $SecretsFile"
Write-Host "`nNext: Run 'wrapper.ps1' to begin setup" -ForegroundColor Cyan
