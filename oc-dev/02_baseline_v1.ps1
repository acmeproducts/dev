# 02_baseline_v1.ps1 - Install baseline packages
param([string]$DistroName = "oc-dev")
$ErrorActionPreference = "Stop"

Write-Host "=== Installing baseline packages ===" -ForegroundColor Cyan

# Create directories
wsl -d $DistroName -u root -- bash -c "mkdir -p /home/support/.openclaw/{workspace,logs}"
wsl -d $DistroName -u root -- bash -c "chown -R support:support /home/support/.openclaw"

# Install baseline script
$script = @'
#!/bin/bash
set -e
export DEBIAN_FRONTEND=noninteractive

echo "Updating package lists..."
apt-get update -qq

echo "Installing packages..."
apt-get install -y -qq sudo curl ca-certificates gnupg jq openssh-server python3 python3-pip python3-venv iproute2 net-tools procps psmisc

echo "Installing Node.js 20..."
curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
apt-get install -y -qq nodejs

echo "Verifying installations..."
echo "Python: $(python3 --version)"
echo "Node: $(node --version)"
echo "npm: $(npm --version)"
echo "=== Baseline Complete ==="
'@

wsl -d $DistroName -u root -- bash -c "cat > /tmp/baseline.sh << 'EOF'
$script
EOF
chmod +x /tmp/baseline.sh
bash /tmp/baseline.sh"

Write-Host "=== Step 2 Complete ===" -ForegroundColor Green
