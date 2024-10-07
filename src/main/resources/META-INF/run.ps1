$path = "C:\Users\Administrator\Desktop\test"
If(!(test-path -PathType container $path))
{
    New-Item -ItemType Directory -Path $path
}
Remove-Item -Force -Recurse "$path\*"
Copy-Item "\\tsclient\D\work\java\1000server\target\Server-1.0-SNAPSHOT-jar-with-dependencies.jar" $path\Server.zip
Set-Location $path
Expand-Archive .\Server.zip .\
Copy-Item -Force .\META-INF\prod.xml .\META-INF\persistence.xml
Copy-Item -Force logprod.xml logback.xml