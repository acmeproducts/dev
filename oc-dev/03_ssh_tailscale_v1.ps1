# 03_ssh_tailscale_v1.ps1 - SSH and Tailscale with environment variable
param([string]$DistroName = "oc-dev")
$ErrorActionPreference = "Stop"

# Check for required env var
if (-not $env:TS_AUTH_KEY) {
    Write-Error "TS_AUTH_KEY not set. Run setup.ps1 first."
    exit 1
}

Write-Host "=== Configuring SSH and Tailscale ===" -ForegroundColor Cyan

# Create support user
wsl -d $DistroName -u root -- bash -c "useradd -m -s /bin/bash support 2>/dev/null || true"
wsl -d $DistroName -u root -- bash -c "echo 'support:support' | chpasswd"
wsl -d $DistroName -u root -- bash -c "usermod -aG sudo support"
wsl -d $DistroName -u root -- bash -c "printf 'support ALL=(ALL) NOPASSWD:ALL\n' > /etc/sudoers.d/90-support"
wsl -d $DistroName -u root -- bash -c "chmod 0440 /etc/sudoers.d/90-support"

# Set hostname
wsl -d $DistroName -u root -- bash -c "echo 'oc-dev' > /etc/hostname"

# Install Tailscale
wsl -d $DistroName -u root -- bash -c "curl -fsSL https://tailscale.com/install.sh | sh"

# Start and register
wsl -d $DistroName -u root -- bash -c "mkdir -p /var/lib/tailscale /run/tailscale /var/run/tailscale"
wsl -d $DistroName -u root -- bash -c "pkill -9 tailscaled 2>/dev/null || true; sleep 2"
wsl -d $DistroName -u root -- bash -c "tailscaled --state=/var/lib/tailscale/tailscaled.state --socket=/var/run/tailscale/tailscaled.sock &"
Start-Sleep -Seconds 3

# Use environment variable for auth key
wsl -d $DistroName -u root -- bash -c "tailscale up --authkey='$env:TS_AUTH_KEY' --hostname=oc-dev --accept-dns=true --accept-routes=false 2>&1"

Start-Sleep -Seconds 3
$ip = wsl -d $DistroName -u root -- bash -c "tailscale ip -4"
Write-Host "Tailscale IP: $ip" -ForegroundColor Green
Write-Host "=== Step 3 Complete ===" -ForegroundColor Green
