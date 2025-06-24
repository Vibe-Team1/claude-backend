# PostgreSQL 데이터베이스 연결 테스트 스크립트
Write-Host "PostgreSQL 데이터베이스 연결 테스트 시작..." -ForegroundColor Green

$dbHost = "vibe-backend.ctom2ogom3wd.ap-northeast-2.rds.amazonaws.com"
$dbPort = "5432"
$dbName = "stockroom_db"
$dbUser = "stockroom_user"

Write-Host "호스트: $dbHost" -ForegroundColor Yellow
Write-Host "포트: $dbPort" -ForegroundColor Yellow
Write-Host "데이터베이스: $dbName" -ForegroundColor Yellow
Write-Host "사용자명: $dbUser" -ForegroundColor Yellow

# 네트워크 연결 테스트
Write-Host "`n1. 네트워크 연결 테스트..." -ForegroundColor Cyan
try {
    $tcpClient = New-Object System.Net.Sockets.TcpClient
    $tcpClient.ConnectAsync($dbHost, $dbPort).Wait(10000) | Out-Null
    
    if ($tcpClient.Connected) {
        Write-Host "✅ 네트워크 연결 성공!" -ForegroundColor Green
        $tcpClient.Close()
    } else {
        Write-Host "❌ 네트워크 연결 실패!" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 네트워크 연결 오류: $($_.Exception.Message)" -ForegroundColor Red
}

# DNS 확인
Write-Host "`n2. DNS 확인..." -ForegroundColor Cyan
try {
    $ipAddress = [System.Net.Dns]::GetHostAddresses($dbHost)
    Write-Host "✅ DNS 확인 성공: $($ipAddress[0].IPAddressToString)" -ForegroundColor Green
} catch {
    Write-Host "❌ DNS 확인 실패: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n테스트 완료!" -ForegroundColor Green 