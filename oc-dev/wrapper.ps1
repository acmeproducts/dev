# wrapper.ps1 - Master wrapper to run all setup steps
param(
    [string]$StartStep = "01",
    [switch]$SkipValidation = $false
)

$ErrorActionPreference = "Stop"
$scriptDir = Split-Path $MyInvocation.MyCommand.Path -Parent
$logDir = Join-Path $scriptDir "logs"
if (-not (Test-Path $logDir)) { New-Item -ItemType Directory -Path $logDir | Out-Null }

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  OpenClaw Environment Setup" -ForegroundColor Cyan
Write-Host "  (GitHub Version - Credentials Required)" -ForegroundColor DarkGray
Write-Host "========================================`n" -ForegroundColor Cyan

# Load secrets
$secretsFile = Join-Path $scriptDir "secrets.ps1"
if (Test-Path $secretsFile) {
    Write-Host "[i] Loading credentials from secrets.ps1..." -ForegroundColor DarkGray
    . $secretsFile
} else {
    Write-Host "[ERR] secrets.ps1 not found! Run setup.ps1 first." -ForegroundColor Red
    exit 1
}

# Validate
if (-not $SkipValidation) {
    $required = @("TS_AUTH_KEY", "VENICE_API_KEY", "OPENROUTER_API_KEY", "OPENCLAW_TOKEN", "WHATSAPP_NUMBER")
    $missing = @()
    foreach ($var in $required) {
        if (-not $env:$var) { $missing += $var }
    }
    if ($missing.Count -gt 0) {
        Write-Host "[ERR] Missing credentials:" -ForegroundColor Red
        $missing | ForEach-Object { Write-Host "    - $_" -ForegroundColor Red }
        exit 1
    }
    Write-Host "[OK] All credentials validated" -ForegroundColor Green
}

$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")

function Run-Step($num, $file, $desc) {
    Write-Host "=== Step $num: $desc ===" -ForegroundColor Cyan
    $log = Join-Path $logDir "step$num.log"
    $start = Get-Date
    $output = & powershell -ExecutionPolicy Bypass -File (Join-Path $scriptDir $file) 2>&1
    $output | Tee-Object -FilePath $log
    $exit = $LASTEXITCODE
    $dur = (Get-Date) - $start
    if ($exit -ne 0) {
        Write-Host "[FAILED] Step $num" -ForegroundColor Red
        return $false
    }
    Write-Host "[PASSED] Step $num in $($dur.ToString('mm\:ss'))" -ForegroundColor Green
    return $true
}

$steps = @(
    @{ Num = "01"; File = "01_create-wsl2_v1.ps1"; Desc = "Create WSL2 (Admin required)"; Admin = $true },
    @{ Num = "02"; File = "02_baseline_v1.ps1"; Desc = "Baseline packages"; Admin = $false },
    @{ Num = "03"; File = "03_ssh_tailscale_v1.ps1"; Desc = "SSH & Tailscale"; Admin = $false },
    @{ Num = "04"; File = "04_report_server_v1.ps1"; Desc = "Report Server"; Admin = $false },
    @{ Num = "05"; File = "05_openclaw_v1.ps1"; Desc = "OpenClaw"; Admin = $false }
)

Write-Host "Execution Plan:" -ForegroundColor White
foreach ($s in $steps) {
    $marker = if ($s.Num -lt $StartStep) { "SKIPPED" } elseif ($s.Admin -and -not $isAdmin) { "NEEDS ADMIN" } else { "READY" }
    $color = if ($marker -eq "READY") { "Green" } elseif ($marker -eq "SKIPPED") { "Gray" } else { "Yellow" }
    Write-Host "  $($s.Num). $($s.Desc) [$marker]" -ForegroundColor $color
}

$go = Read-Host "`nExecute from step $StartStep? (Y/n)"
if ($go -eq 'n') { exit 0 }

foreach ($s in ($steps | Where-Object { $_.Num -ge $StartStep })) {
    if ($s.Admin -and -not $isAdmin) {
        Write-Host "ERROR: Step $($s.Num) requires Administrator" -ForegroundColor Red
        exit 1
    }
    if (-not (Run-Step $s.Num $s.File $s.Desc)) {
        Write-Host "`nFAILED at step $($s.Num). Use -StartStep $($s.Num) to retry." -ForegroundColor Red
        exit 1
    }
}

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "  ALL STEPS COMPLETED" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "`nAccess:" -ForegroundColor White
Write-Host "  SSH:  ssh support@oc-dev.fell-dojo.ts.net" -ForegroundColor Cyan
Write-Host "  Web:  https://oc-dev.fell-dojo.ts.net/report/" -ForegroundColor Cyan
