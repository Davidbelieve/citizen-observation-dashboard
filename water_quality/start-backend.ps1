$services = @(
    @{ Name = "api-gateway-ms"; Path = "api-gateway-ms"; Port = 8080 },
    @{ Name = "crowdsourced-data-ms"; Path = "crowdsourced-data-ms"; Port = 8083 },
    @{ Name = "rewards-ms"; Path = "rewards-ms"; Port = 8082 },
    @{ Name = "auth-ms"; Path = "auth-ms"; Port = 8089 }
)

foreach ($service in $services) {
    Write-Host "Starting $($service.Name) on port $($service.Port)..."
    $servicePath = Join-Path $PSScriptRoot $service.Path
    
    # Check if target exists, if not build it
    if (-not (Test-Path "$servicePath\target\*.jar")) {
        Write-Host "Building $($service.Name)..."
        Start-Process -FilePath "cmd.exe" -ArgumentList "/c cd /d $servicePath && mvnw clean install -DskipTests" -Wait
    }

    # Start the service in a new window
    Start-Process -FilePath "cmd.exe" -ArgumentList "/k cd /d $servicePath && mvnw spring-boot:run" -WindowStyle Normal
}

Write-Host "All backend services are starting in separate windows."
Write-Host "Please wait a few moments for them to initialize."
