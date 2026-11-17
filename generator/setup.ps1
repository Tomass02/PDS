$ErrorActionPreference = 'Stop'

$venvPath = Join-Path $PSScriptRoot '.venv'
$venvPython = Join-Path $venvPath 'Scripts\python.exe'

if (-not (Test-Path -LiteralPath $venvPath)) {
    python -m venv $venvPath
    if ($LASTEXITCODE -ne 0) {
        throw 'Vytvorenie virtualneho prostredia zlyhalo.'
    }
}

if (-not (Test-Path -LiteralPath $venvPython -PathType Leaf)) {
    throw "Virtualne prostredie v $venvPath neobsahuje Scripts\python.exe."
}

& $venvPython -m pip install -r (Join-Path $PSScriptRoot 'requirements.txt')
if ($LASTEXITCODE -ne 0) {
    throw 'Instalacia zavislosti zlyhala.'
}

Write-Output "Virtualne prostredie je pripravene: $venvPath"
