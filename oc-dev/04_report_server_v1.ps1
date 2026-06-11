# 04_report_server_v1.ps1 - Report server setup
param([string]$DistroName = "oc-dev")
$ErrorActionPreference = "Stop"

Write-Host "=== Setting up report server ===" -ForegroundColor Cyan

# Create directories
wsl -d $DistroName -u root -- bash -c "mkdir -p /home/support/.report/{logs,pages}"

# HTML files
wsl -d $DistroName -u support -- bash -c "cat > /home/support/.report/index.html << 'EOF'
<!DOCTYPE html><html><head><meta charset="utf-8"><title>oc-dev</title><style>body{font-family:monospace;background:#080c14;color:#c9d1d9;padding:1.5rem}h1{color:#58a6ff}a{color:#4fc3f7}</style></head><body><h1>oc-dev</h1><ul><li><a href="server-card.html">Server Card</a></li><li><a href="certification.html">Certification</a></li><li><a href="logs/">Logs</a></li></ul></body></html>
EOF"

wsl -d $DistroName -u support -- bash -c "cat > /home/support/.report/server-card.html << 'EOF'
<!DOCTYPE html><html><head><meta charset="utf-8"><title>oc-dev Server Card</title><style>body{font-family:monospace;background:#080c14;color:#c9d1d9;padding:1.5rem}h1{color:#58a6ff}table{border-collapse:collapse;width:100%}td,th{border:1px solid #21262d;padding:.5rem 1rem}th{background:#0d1117;color:#58a6ff}.pass{color:#3fb950;font-weight:600}.fail{color:#f85149;font-weight:600}</style></head><body><h1>oc-dev Server Card</h1><table><tr><th>Field</th><th>Value</th></tr><tr><td>Environment</td><td>oc-dev</td></tr><tr><td>FQDN</td><td>oc-dev.fell-dojo.ts.net</td></tr><tr><td>Status</td><td class="pass">RUNNING</td></tr></table></body></html>
EOF"

# Python server
wsl -d $DistroName -u root -- bash -c "cat > /usr/local/bin/oc-report-server << 'EOF'
#!/usr/bin/env python3
import http.server, socketserver, os
os.chdir('/home/support/.report')
class Handler(http.server.SimpleHTTPRequestHandler):
    def log_message(self, fmt, *args): pass
socketserver.TCPServer.allow_reuse_address = True
with socketserver.TCPServer(('0.0.0.0', 18080), Handler) as httpd:
    httpd.serve_forever()
EOF
chmod +x /usr/local/bin/oc-report-server"

# systemd service
wsl -d $DistroName -u root -- bash -c "cat > /etc/systemd/system/oc-report-server.service << 'EOF'
[Unit]
Description=OC Report Server
After=network.target
[Service]
Type=simple
User=support
WorkingDirectory=/home/support/.report
ExecStart=/usr/local/bin/oc-report-server
Restart=always
RestartSec=5
[Install]
WantedBy=multi-user.target
EOF"

# Start
wsl -d $DistroName -u root -- bash -c "systemctl daemon-reload; systemctl enable oc-report-server; systemctl start oc-report-server"
Start-Sleep -Seconds 2

# Verify
wsl -d $DistroName -u root -- bash -c "curl -fsS http://127.0.0.1:18080/ > /dev/null && echo 'Report server: OK' || echo 'Report server: FAIL'"

Write-Host "=== Step 4 Complete ===" -ForegroundColor Green
