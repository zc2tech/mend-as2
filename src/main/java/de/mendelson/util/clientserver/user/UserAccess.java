package de.mendelson.util.clientserver.user;

import java.io.BufferedReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Contains several utilities for the user access
 *
 * @author S.Heller
 * @version $Revision: 13 $
 */
public class UserAccess {

    private Path passwdFile = Paths.get("passwd");
    private final Logger logger;
    private long lastUserReadTime = 0;
    private final Map<String, User> userMap = Collections.synchronizedMap(new HashMap<String, User>());

    public UserAccess(Logger logger) {
        this.logger = logger;
    }

    public UserAccess(Logger logger, String passwdFilename) {
        this.logger = logger;
        this.passwdFile = Paths.get(passwdFilename);
    }

    public User addUser(String userName, char[] password) throws Exception {
        if (this.readUser(userName) != null) {
            throw new Exception("User \"" + userName + "\" does already exist.");
        }
        User user = new User();
        user.setName(userName);
        user.setPasswdCrypted(User.cryptPassword(password));
        for (int i = 0; i < 3; i++) {
            user.setPermission(i, "");
        }
        try (RandomAccessFile file = new RandomAccessFile(this.passwdFile.toFile(), "rw")) {
            file.seek(Files.size(this.passwdFile));
            String userLine = User.serialize(user);
            file.writeBytes("\n");
            file.writeBytes(userLine);
            file.writeBytes("\n");
        }
        return (user);
    }

    /**
     * Reads a user from of the actual passwd file
     */
    public User readUser(String userName) {
        try {
            long lastUserFileModificationTime = Files.getLastModifiedTime(this.passwdFile).toMillis();
            if (lastUserFileModificationTime > this.lastUserReadTime) {
                this.readAllUserToCache();
            }
            synchronized (this.userMap) {
                User user = this.userMap.get(userName);
                return (user);
            }
        } catch (Throwable e) {
            this.logger.warning(
                    "Password storage read error: ["
                    + e.getClass().getSimpleName()
                    + "] " + e.getMessage());
            return (null);
        }
    }

    /**
     * Reads all user and stores them in the user map
     *
     */
    private void readAllUserToCache() throws Exception {
        synchronized (this.userMap) {
            this.userMap.clear();
            try (BufferedReader bufferedReader 
                    = Files.newBufferedReader(this.passwdFile, StandardCharsets.UTF_8)) {
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    if (line != null && line.trim().length() > 0) {
                        if (line.startsWith("#")) {
                            continue;
                        }
                        User user = User.parse(line);
                        this.userMap.put(user.getName(), user);
                    }
                }
                this.lastUserReadTime = System.currentTimeMillis();
            }
        }
    }
}
