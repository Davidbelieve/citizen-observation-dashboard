$ErrorActionPreference = "Stop"

# Use JDK 21 specifically since Spring Boot 3.5.7 likely requires Java 21+ but we'll try 17 first or use recommended
# The user mentioned "java not install". We will download a portable JDK.
# URL for Adoptium OpenJDK 17 (LTS) Hotspot for Windows x64
$jdkUrl = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.10_7.zip"
$jdkZip = "openjdk.zip"
$jdkDir = "jdk-17.0.10+7"
$localJdkPath = Join-Path $PSScriptRoot "local_java"

Write-Host "Checking for Java..."

# Function to check if java command works
function Test-Java {
    try {
        $version = java -version 2>&1
        if ($LASTEXITCODE -eq 0) {
            return $true
        }
    } catch {}
    return $false
}

if (-not (Test-Java)) {
    Write-Host "Java not found in PATH. Checking for local portable JDK..."
    
    if (-not (Test-Path "$localJdkPath")) {
        Write-Host "Downloading Portable OpenJDK 17... (This may take a minute)"
        Invoke-WebRequest -Uri $jdkUrl -OutFile $jdkZip
        
        Write-Host "Extracting JDK..."
        Expand-Archive -Path $jdkZip -DestinationPath $PSScriptRoot -Force
        
        # Renaissance the extracted folder to a fixed name 'local_java'
        $extractedFolder = Get-ChildItem -Path $PSScriptRoot -Directory | Where-Object { $_.Name -like "jdk-17*" } | Select-Object -First 1
        Rename-Item -Path $extractedFolder.FullName -NewName "local_java"
        
        # Cleanup zip
        Remove-Item $jdkZip
        
        Write-Host "JDK Installed locally at $localJdkPath"
    } else {
        Write-Host "Using existing local JDK at $localJdkPath"
    }

    # Set JAVA_HOME and PATH for this session
    $env:JAVA_HOME = $localJdkPath
    $env:PATH = "$localJdkPath\bin;$env:PATH"
    
    # Verify again
    if (Test-Java) {
        Write-Host "Java configured successfully!"
        java -version
    } else {
        Write-Error "Failed to configure Java automatically. Please install Java manually."
        exit 1
    }
} else {
    Write-Host "System Java found."
}

# Now run the services using the original logic
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
        # We need to pass the new env vars to the new process
        Start-Process -FilePath "cmd.exe" -ArgumentList "/c cd /d $servicePath && set JAVA_HOME=$env:JAVA_HOME && set PATH=$env:PATH && mvnw clean install -DskipTests" -Wait
    }

    # Start the service in a new window with the correct JAVA environment
    # We construct a command that sets env vars then runs mvnw
    $cmdArgs = "/k cd /d $servicePath && set JAVA_HOME=$env:JAVA_HOME && set PATH=$env:PATH && mvnw spring-boot:run"
    Start-Process -FilePath "cmd.exe" -ArgumentList $cmdArgs -WindowStyle Normal
}

Write-Host "---------------------------------------------------"
Write-Host "ALL BACKEND SERVICES STARTED!"
Write-Host "---------------------------------------------------"
Write-Host "Please wait approx 1-2 minutes for them to initialize."
Write-Host "You can verify they are running by checking the new terminal windows."
