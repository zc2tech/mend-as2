Original Source code is like this, there is no POM file, so I don't know how to download
dependences library.

Copilot adivse below:


Why you’re missing classes
The official community source drop for Mendelson AS2 is not a Maven/Gradle project. It ships without a pom.xml and expects you to put the bundled JARs on the classpath manually. The ready‑made dependency JARs are included with the installer/ZIP distribution (under a lib/ folder), not in the plain “src” archive you likely imported.
There is an unofficial GitHub mirror that adds a Maven pom.xml and makes the code easier to build in modern IDEs, but it’s not the official upstream. 

https://github.com/sarog/mec-as2/

*** I downloaded the POM and will try to adapt to this 1.1B67 source code


# * Copy from Mendenson Official Site

https://mendelson.de/as2

# Test Your AS2 Setup
To help you verify compatibility and functionality, Mendelson operates a public AS2 test server. This is ideal for ensuring your setup works correctly with our solution.

If you are using the mendelson open-source AS2 installation, it is pre-configured to communicate with this server out of the box.

# Test Server URLs
Check server status:
http://testas2.mendelson-e-c.com:8080/as2/ServerState
Web monitoring interface:
http://testas2.mendelson-e-c.com:8080/webas2
Login: guest / Password: guest

# Configuration for Testing
```
MDN: sync
URL: http://testas2.mendelson-e-c.com:8080/as2/HttpReceiver
Sender AS2 ID: mycompanyAS2
Receiver AS2 ID: mendelsontestAS2
Signature algorithm: SHA-1, SHA-2, SHA-3 or any other
Encryption algorithm: 3DES or any supported
```

# Keys and Certificates
## Sender key (PKCS#12):
[Download Key3](https://mendelson.de/media/47) (password: test)

## Receiver certificate (PEM):
[Download Key4](https://mendelson.de/media/48)

# TLS Test Options
You can also test TLS-secured communication using:

## AS2 Receiver:
https://testas2.mendelson-e-c.com:8444/as2/HttpReceiver

## Web interface:
https://testas2.mendelson-e-c.com:8444/webas2 (guest / guest)

Please note: These URLs are secured with a self-signed certificate. Your browser may flag them as untrusted — this is expected. In production, you can use your own trusted certificates.