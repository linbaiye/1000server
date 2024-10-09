$path = "C:\Users\Administrator\Desktop\test"
If(!(test-path -PathType container $path))
{
    New-Item -ItemType Directory -Path $path
}
Remove-Item -Force -Recurse "$path\*"
Add-Type -Assembly "System.IO.Compression.Filesystem"
[System.IO.Compression.ZipFile]::ExtractToDirectory("$path\Server.zip", "$path")
Copy-Item -Force .\META-INF\prod.xml .\META-INF\persistence.xml
Copy-Item -Force logprod.xml logback.xml
java -Xmx800m -Xms500m -cp . org.y1000.Server