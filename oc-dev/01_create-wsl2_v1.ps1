# 01_create-wsl2_v1.ps1 - Create WSL2 with systemd
param([string]$DistroName = "oc-dev")
$ErrorActionPreference = "Stop"

Write-Host "=== Creating WSL2: $DistroName ===" -ForegroundColor Cyan

# Check admin
if (-not ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    Write-Error "This script must run as Administrator"
    exit 1
}

# Unregister if exists
$existing = wsl --list --quiet 2>$null | Where-Object { $_ -match "^$DistroName$" }
if ($existing) {
    Write-Host "Unregistering existing $DistroName..." -ForegroundColor Yellow
    wsl --unregister $DistroName
    Start-Sleep -Seconds 3
}

# Create base tarball
$baseTar = "$env:TEMP\ubuntu-2404-base.tar"
$baseDistro = "Ubuntu-24.04"

if (-not (Test-Path $baseTar)) {
    $baseExists = wsl --list --quiet 2>$null | Where-Object { $_ -match "^$baseDistro$" }
    if (-not $baseExists) {
        Write-Host "Installing $baseDistro..."
        wsl --install -d $baseDistro --no-launch
        Start-Sleep -Seconds 30
    }
    Write-Host "Exporting base tarball to $baseTar..."
    wsl --export $baseDistro $baseTar
}

# Import distro
$destDir = "$env:LOCALAPPDATA\WSL\$DistroName"
New-Item -ItemType Directory -Force -Path $destDir | Out-Null

Write-Host "Importing $DistroName..."
wsl --import $DistroName $destDir $baseTar --version 2

# Configure wsl.conf
Write-Host "Configuring systemd..."
wsl -d $DistroName -u root -- bash -c "cat > /etc/wsl.conf << 'EOF'
[boot]
systemd=true
[network]
generateHosts=true
generateResolvConf=true
EOF"

# Restart
wsl --terminate $DistroName
Start-Sleep -Seconds 5
wsl -d $DistroName -u root -- bash -c "echo AWAKE" | Out-Null
Start-Sleep -Seconds 15

# Verify
$status = wsl -d $DistroName -u root -- bash -c "systemctl is-system-running 2>/dev/null || echo 'degraded'"
Write-Host "systemd status: $status" -ForegroundColor Green
Write-Host "=== Step 1 Complete ===" -ForegroundColor Green
