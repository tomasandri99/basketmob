# Runs the full API acceptance sweep expected for HBV501G.
# Usage:
#   pwsh ./scripts/smoke-all.ps1

param(
    [string]$BaseUrl = "http://localhost:8080"
)

$RunId          = (Get-Date -Format "yyyyMMddHHmmss")
$PrimaryEmail   = "smoke+$RunId@basket.is"
$SecondaryEmail = "smoke-target+$RunId@basket.is"
$Password       = "Password123!"
$FavoriteTeamId = 1
$FavoriteTeamName = "Valur"

function Write-Step($text) { Write-Host "`n>>> $text" -ForegroundColor Cyan }
function Require-Value($value, $message) { if (-not $value) { throw $message } }

Write-Step "Locate a stable game id (uses 2025 schedule data)"
$lookup = Invoke-WebRequest "$BaseUrl/api/v1/games?dateFrom=2025-10-01&dateTo=2025-12-31&page=0&size=1&sort=tipoff,asc"
$games = $lookup.Content | ConvertFrom-Json
$sampleGameId = ($games.content | Select-Object -First 1).id
Require-Value $sampleGameId "No games were returned. Did the seed run?"

Write-Step "POST /api/v1/users (register primary)"
$primaryRegister = @{ email = $PrimaryEmail; password = $Password; displayName = "Smoke $RunId" } | ConvertTo-Json
Invoke-WebRequest "$BaseUrl/api/v1/users" -Method POST -ContentType "application/json" -Body $primaryRegister | Out-Null

Write-Step "POST /api/v1/auth/register (register secondary)"
$secondaryRegister = @{ email = $SecondaryEmail; password = $Password; displayName = "Target $RunId" } | ConvertTo-Json
Invoke-WebRequest "$BaseUrl/api/v1/auth/register" -Method POST -ContentType "application/json" -Body $secondaryRegister | Out-Null

Write-Step "POST /api/v1/auth/login (primary)"
$loginBody  = @{ email = $PrimaryEmail; password = $Password } | ConvertTo-Json
$loginResp  = Invoke-WebRequest "$BaseUrl/api/v1/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
$primaryToken = (ConvertFrom-Json $loginResp.Content).token
$userHeaders  = @{ Authorization = "Bearer $primaryToken" }

Write-Step "GET /api/v1/games (fixtures + pagination)"
Invoke-WebRequest "$BaseUrl/api/v1/games?page=0&size=10&sort=tipoff,asc" | Out-Null

Write-Step "GET /api/v1/games (filtered)"
Invoke-WebRequest "$BaseUrl/api/v1/games?date=2025-11-13&leagueId=1&status=SCHEDULED&page=0&size=5" | Out-Null

Write-Step "GET /api/v1/games/$sampleGameId"
Invoke-WebRequest "$BaseUrl/api/v1/games/$sampleGameId" | Out-Null

Write-Step "GET /api/v1/leagues/1/standings?season=2025-2026"
Invoke-WebRequest "$BaseUrl/api/v1/leagues/1/standings?season=2025-2026" | Out-Null

Write-Step "GET /api/v1/search"
Invoke-WebRequest "$BaseUrl/api/v1/search?q=$FavoriteTeamName&limit=5" | Out-Null

Write-Step "GET /api/v1/users/me"
$userProfile = Invoke-WebRequest "$BaseUrl/api/v1/users/me" -Headers $userHeaders
$primaryUserId = (ConvertFrom-Json $userProfile.Content).id

Write-Step "PUT /api/v1/users/me"
$mePut = @{ displayName = "Smoke PUT $RunId"; avatarUrl = "https://cdn.basketmob.is/avatars/put.png" } | ConvertTo-Json
Invoke-WebRequest "$BaseUrl/api/v1/users/me" -Method PUT -Headers $userHeaders -ContentType "application/json" -Body $mePut | Out-Null

Write-Step "PATCH /api/v1/users/me"
$mePatch = @{ displayName = "Smoke PATCH $RunId" } | ConvertTo-Json
Invoke-WebRequest "$BaseUrl/api/v1/users/me" -Method PATCH -Headers $userHeaders -ContentType "application/json" -Body $mePatch | Out-Null

Write-Step "POST /api/v1/me/favorites?teamId=$FavoriteTeamId"
Invoke-WebRequest "$BaseUrl/api/v1/me/favorites?teamId=$FavoriteTeamId" -Method POST -Headers $userHeaders | Out-Null

Write-Step "GET /api/v1/me/favorites"
Invoke-WebRequest "$BaseUrl/api/v1/me/favorites" -Headers $userHeaders | Out-Null

Write-Step "GET /api/v1/me/notifications (baseline)"
Invoke-WebRequest "$BaseUrl/api/v1/me/notifications" -Headers $userHeaders | Out-Null

Write-Step "Login secondary user to capture ID"
$secondaryLogin = @{ email = $SecondaryEmail; password = $Password } | ConvertTo-Json
$secondaryResp  = Invoke-WebRequest "$BaseUrl/api/v1/auth/login" -Method POST -ContentType "application/json" -Body $secondaryLogin
$secondaryToken = (ConvertFrom-Json $secondaryResp.Content).token
$secondaryHeaders = @{ Authorization = "Bearer $secondaryToken" }
$secondaryProfile = Invoke-WebRequest "$BaseUrl/api/v1/users/me" -Headers $secondaryHeaders
$secondaryUserId  = (ConvertFrom-Json $secondaryProfile.Content).id
Invoke-WebRequest "$BaseUrl/api/v1/auth/logout" -Method POST -Headers $secondaryHeaders | Out-Null

Write-Step "POST /api/v1/auth/login (admin)"
$adminLogin = @{ email = "admin@basketmob.is"; password = "Admin123!" } | ConvertTo-Json
$adminResp  = Invoke-WebRequest "$BaseUrl/api/v1/auth/login" -Method POST -ContentType "application/json" -Body $adminLogin
$adminToken = (ConvertFrom-Json $adminResp.Content).token
$adminHeaders = @{ Authorization = "Bearer $adminToken" }

Write-Step "PATCH /api/v1/admin/games/$sampleGameId"
$adminGamePatch = @{ status = "FINAL"; homeScore = 200; awayScore = 199 } | ConvertTo-Json
Invoke-WebRequest "$BaseUrl/api/v1/admin/games/$sampleGameId" -Method PATCH -Headers $adminHeaders -ContentType "application/json" -Body $adminGamePatch | Out-Null

Write-Step "PATCH /api/v1/users/$secondaryUserId"
$adminUserPatch = @{ displayName = "Target Patched $RunId" } | ConvertTo-Json
Invoke-WebRequest "$BaseUrl/api/v1/users/$secondaryUserId" -Method PATCH -Headers $adminHeaders -ContentType "application/json" -Body $adminUserPatch | Out-Null

Write-Step "PUT /api/v1/users/$secondaryUserId"
$adminUserPut = @{
    email = $SecondaryEmail
    displayName = "Target Put $RunId"
    avatarUrl = "https://cdn.basketmob.is/avatars/target.png"
    gender = "other"
} | ConvertTo-Json
Invoke-WebRequest "$BaseUrl/api/v1/users/$secondaryUserId" -Method PUT -Headers $adminHeaders -ContentType "application/json" -Body $adminUserPut | Out-Null

Write-Step "DELETE /api/v1/users/$secondaryUserId"
Invoke-WebRequest "$BaseUrl/api/v1/users/$secondaryUserId" -Method DELETE -Headers $adminHeaders | Out-Null

Write-Step "POST /api/v1/auth/logout (admin)"
Invoke-WebRequest "$BaseUrl/api/v1/auth/logout" -Method POST -Headers @{ Authorization = "Bearer $adminToken" } | Out-Null

Write-Step "GET /api/v1/me/notifications (after admin update)"
$notificationsResp = Invoke-WebRequest "$BaseUrl/api/v1/me/notifications" -Headers $userHeaders
$notifications = if ($notificationsResp.Content.Trim()) { $notificationsResp.Content | ConvertFrom-Json } else { @() }
if ($notifications.Count -gt 0) {
    $notificationId = $notifications[0].id
    Write-Step "POST /api/v1/me/notifications/$notificationId/read"
    Invoke-WebRequest "$BaseUrl/api/v1/me/notifications/$notificationId/read" -Method POST -Headers $userHeaders | Out-Null
} else {
    Write-Warning "No notifications returned, skipping read endpoint"
}

Write-Step "DELETE /api/v1/me/notifications"
Invoke-WebRequest "$BaseUrl/api/v1/me/notifications" -Method DELETE -Headers $userHeaders | Out-Null

Write-Step "DELETE /api/v1/me/favorites/$FavoriteTeamId"
Invoke-WebRequest "$BaseUrl/api/v1/me/favorites/$FavoriteTeamId" -Method DELETE -Headers $userHeaders | Out-Null

Write-Step "POST /api/v1/auth/logout (primary)"
Invoke-WebRequest "$BaseUrl/api/v1/auth/logout" -Method POST -Headers $userHeaders | Out-Null

Write-Step "Re-login primary to delete self"
$loginResp2  = Invoke-WebRequest "$BaseUrl/api/v1/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
$primaryToken = (ConvertFrom-Json $loginResp2.Content).token
$userHeaders  = @{ Authorization = "Bearer $primaryToken" }

Write-Step "DELETE /api/v1/users/me"
Invoke-WebRequest "$BaseUrl/api/v1/users/me" -Method DELETE -Headers $userHeaders | Out-Null

Write-Host "`nSmoke test complete." -ForegroundColor Green
