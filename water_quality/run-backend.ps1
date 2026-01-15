$localJdkPath = Join-Path $PSScriptRoot "local_java"

# 1. Setup Java Environment
if (Test-Path $localJdkPath) {
    Write-Host "Found local Java installation."
    $env:JAVA_HOME = $localJdkPath
    $env:PATH = "$localJdkPath\bin;$env:PATH"
} else {
    Write-Host "Using System Java"
}

# 2. Verify Java
Write-Host "Java Version:"
java -version
if ($LASTEXITCODE -ne 0) {
    Write-Error "Java is still not working. Please check the local_java folder."
    exit
}

# 3. Start Services
$services = @(
    @{ Name = "api-gateway-ms"; Path = "api-gateway-ms"; Port = 8080 },
    @{ Name = "crowdsourced-data-ms"; Path = "crowdsourced-data-ms"; Port = 8083 },
    @{ Name = "rewards-ms"; Path = "rewards-ms"; Port = 8082 },
    @{ Name = "auth-ms"; Path = "auth-ms"; Port = 8089 }
)

foreach ($service in $services) {
    Write-Host "Starting $($service.Name) on port $($service.Port)..."
    $servicePath = Join-Path $PSScriptRoot $service.Path
    
    # Build if target/ doesn't exist (first run)
    if (-not (Test-Path "$servicePath\target\*.jar")) {
        Write-Host "Building $($service.Name) (This may take a while)..."
        # Use quotes to handle spaces in path 'final (2)'
        $buildCmd = "/c cd /d ""$servicePath"" && set ""JAVA_HOME=$env:JAVA_HOME"" && set ""PATH=$env:PATH"" && mvnw clean install -DskipTests"
        Start-Process -FilePath "cmd.exe" -ArgumentList $buildCmd -Wait
    }

    # Run the service
    Write-Host "Launching $($service.Name)..."
    $runCmd = "/k cd /d ""$servicePath"" && set ""JAVA_HOME=$env:JAVA_HOME"" && set ""PATH=$env:PATH"" && mvnw spring-boot:run"
    Start-Process -FilePath "cmd.exe" -ArgumentList $runCmd -WindowStyle Normal
}

Write-Host "---------------------------------------------------"
Write-Host "Services are launching in new windows."
Write-Host "Wait until you see 'Started ...' in each window."
Write-Host "Then go to front end: http://localhost:3000"
Write-Host "---------------------------------------------------"
