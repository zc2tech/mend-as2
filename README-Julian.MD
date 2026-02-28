Original Source code is like this, there is no POM file, so I don't know how to download
dependences library.

Copilot adivse below:


Why you’re missing classes
The official community source drop for Mendelson AS2 is not a Maven/Gradle project. It ships without a pom.xml and expects you to put the bundled JARs on the classpath manually. The ready‑made dependency JARs are included with the installer/ZIP distribution (under a lib/ folder), not in the plain “src” archive you likely imported.
There is an unofficial GitHub mirror that adds a Maven pom.xml and makes the code easier to build in modern IDEs, but it’s not the official upstream. 

https://github.com/sarog/mec-as2/

*** I downloaded the POM and will try to adapt to this 1.1B67 source code