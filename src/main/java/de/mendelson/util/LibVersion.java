package de.mendelson.util;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class LibVersion {

    /**
     * Tries to find out the current lib version of the passed class. This is
     * either done by package access of direct access of the jars MANIFEST.MF
     * file
     *
     * @param singleClass
     * @return
     * @throws Exception
     */
    public static String getLibVersion(Class<?> singleClass) throws Exception {
        //try to get the implementation version
        Package packageObj = singleClass.getPackage();
        String implementationVersion = packageObj.getImplementationVersion();
        if (implementationVersion != null) {
            return (implementationVersion);
        }
        //try to extract the information from the MANIFEST.MF file of the jar
        String classJar = singleClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        try (JarFile jarFile = new JarFile(classJar)) {
            Manifest manifest = jarFile.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            String version = attributes.getValue("Bundle-Version");
            return (version);
        }
    }

    /**
     * Returns a list of strings that contains the lib versions found in the
     * classpath
     *
     * @return
     */
    public static List<String> getLibVersions() {
        List<String> libList = new ArrayList<String>();
        Map<String, String> classMap = new LinkedHashMap<String, String>();
        classMap.put("org.bouncycastle.jce.provider.BouncyCastleProvider", "BouncyCastle v{0} (Crypto API)");
        classMap.put("org.apache.mina.core.service.IoService", "MINA v{0} (Client-server, IO)");
        classMap.put("org.apache.lucene.index.IndexReader", "Lucene v{0} (Log index, event index)");
        classMap.put("org.apache.batik.transcoder.image.ImageTranscoder", "SVG Batik v{0} (SVG processing)");
        classMap.put("com.fasterxml.jackson.databind.node.ObjectNode", "Jackson v{0} (JSON)");
        classMap.put("com.zaxxer.hikari.HikariDataSource", "Hikari v{0} (DB pool)");
        try {
            for (String singleClassStr : classMap.keySet()) {
                Class<?> singleClass;
                try {
                    singleClass = Class.forName(singleClassStr);
                } catch (Throwable e) {
                    continue;
                }
                String version = LibVersion.getLibVersion(singleClass);
                if (version != null) {
                    String template = classMap.get(singleClassStr);
                    libList.add(MessageFormat.format(template, version));
                }
            }
        } catch (Throwable e) {
            //ignore
        }
        return( libList );
    }

    public static void main(String[] args) {
        List<String> libVersions = LibVersion.getLibVersions();
        for (String entry : libVersions) {
            System.out.println(entry);
        }
    }
}
