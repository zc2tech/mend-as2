package de.mendelson.comm.as2.server;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.security.LoginRateLimiter;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.ClientServerSessionHandler;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.LoginRequest;
import de.mendelson.util.clientserver.messages.LoginResponse;
import de.mendelson.util.clientserver.messages.ServerLogMessage;
import de.mendelson.util.clientserver.user.User;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.PBKDF2;
import de.mendelson.util.systemevents.SystemEventManager;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.session.IoSession;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Session handler for the server implementation
 *
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class ClientServerSessionHandlerLocalhost extends ClientServerSessionHandler {

    private static final String SESSION_ATTRIB_AUTHENTICATED = "authenticated";
    private final MecResourceBundle rb;
    private boolean allowAllClients = false;
    private IDBDriverManager dbDriverManager;
    private Logger myLogger;

    public ClientServerSessionHandlerLocalhost(Logger logger, String[] validClientIds, boolean allowAllClients, int maxClients,
            SystemEventManager eventManager, IDBDriverManager dbDriverManager) {
        super(logger, validClientIds, maxClients, eventManager);
        this.myLogger = logger;
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleClientServerSessionHandlerLocalhost.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.allowAllClients = allowAllClients;
        this.dbDriverManager = dbDriverManager;
        System.out.println(this.rb.getResourceString("allowallclients." + String.valueOf(allowAllClients)));
    }

    @Override
    /**
     * The session has been opened: send a server info object
     */
    public void sessionOpened(IoSession session) {
        try {
            InetSocketAddress localAddress = (InetSocketAddress) session.getLocalAddress();
            InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
            if (!this.allowAllClients && !localAddress.getHostName().equalsIgnoreCase(remoteAddress.getHostName())) {
                ServerLogMessage message = new ServerLogMessage();
                message.setLevel(Level.SEVERE);
                message.setMessage(this.rb.getResourceString("only.localhost.clients"));
                session.write(message);
                session.closeNow();
            } else {
                super.sessionOpened(session);
            }
        } catch (Exception e) {
            session.closeNow();
        }
    }

    @Override
    /**
     * Intercept messages to handle authentication first
     */
    public void messageReceived(IoSession session, Object message) {
        if (!(message instanceof ClientServerMessage)) {
            super.messageReceived(session, message);
            return;
        }

        // Handle login request
        if (message instanceof LoginRequest) {
            LoginRequest request = (LoginRequest) message;
            LoginResponse response = this.processLoginRequest(request, session);
            session.write(response);

            if (response.isSuccess()) {
                // Mark session as authenticated on successful login
                session.setAttribute(SESSION_ATTRIB_AUTHENTICATED, Boolean.TRUE);
            }
            // Note: Do NOT close session on failed login - allow retry
            return;
        }

        // For all other messages, check authentication first
        Boolean authenticated = (Boolean) session.getAttribute(SESSION_ATTRIB_AUTHENTICATED);
        if (authenticated == null || !authenticated) {
            // Not authenticated - reject message
            this.log(Level.SEVERE, "Rejecting unauthenticated message: " + message.getClass().getSimpleName());
            session.closeNow();
            return;
        }

        // Authenticated - pass to parent handler
        super.messageReceived(session, message);
    }

    /**
     * Process login request and validate credentials
     */
    private LoginResponse processLoginRequest(LoginRequest request, IoSession session) {
        LoginResponse response = new LoginResponse(request);
        Connection connection = null;

        // Get remote address for rate limiting
        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
        String remoteAddr = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";

        try {
            // Get database connection for rate limiting
            connection = this.dbDriverManager.getConnectionWithoutErrorHandling(
                    IDBDriverManager.DB_RUNTIME);
            PreferencesAS2 prefs = new PreferencesAS2(this.dbDriverManager);

            // Check if IP is blocked due to rate limiting
            if (LoginRateLimiter.isBlocked(remoteAddr)) {
                long remainingSeconds = LoginRateLimiter.getBlockRemainingSeconds(remoteAddr);
                response.setSuccess(false);
                response.setErrorMessage("Too many failed login attempts. Access temporarily blocked for " +
                        remainingSeconds + " seconds.");
                this.log(Level.WARNING, "Blocked SwingUI login attempt from " + remoteAddr +
                        " - " + remainingSeconds + "s remaining");
                return response;
            }

            // For SwingUI: Only allow "admin" user
            if (!"admin".equals(request.getUsername())) {
                // Record failed attempt
                LoginRateLimiter.recordAuthFailure(remoteAddr, request.getUsername(),
                        LoginRateLimiter.SOURCE_SWING_UI, null, connection);

                // Check if should block after this failure
                LoginRateLimiter.checkAndBlock(remoteAddr, connection, prefs);

                response.setSuccess(false);
                response.setErrorMessage("Only 'admin' user can access SwingUI");
                this.log(Level.WARNING, "SwingUI login rejected: user '" + request.getUsername() + "' not allowed");
                return response;
            }

            // Validate credentials against database
            UserManagementAccessDB userDB = new UserManagementAccessDB(this.dbDriverManager, this.myLogger);
            WebUIUser dbUser = userDB.getUserByUsername(request.getUsername());

            if (dbUser == null || !dbUser.isEnabled()) {
                // Record failed attempt
                LoginRateLimiter.recordAuthFailure(remoteAddr, request.getUsername(),
                        LoginRateLimiter.SOURCE_SWING_UI, null, connection);

                // Check if should block after this failure
                LoginRateLimiter.checkAndBlock(remoteAddr, connection, prefs);

                response.setSuccess(false);
                response.setErrorMessage("Invalid credentials or user disabled");
                this.log(Level.WARNING, "SwingUI login failed: user not found or disabled");
                return response;
            }

            // Verify password using PBKDF2
            boolean passwordValid = PBKDF2.validatePassword(
                    new String(request.getPassword()),
                    dbUser.getPasswordHash()
            );

            if (!passwordValid) {
                // Record failed attempt
                LoginRateLimiter.recordAuthFailure(remoteAddr, request.getUsername(),
                        LoginRateLimiter.SOURCE_SWING_UI, null, connection);

                // Check if should block after this failure
                LoginRateLimiter.checkAndBlock(remoteAddr, connection, prefs);

                response.setSuccess(false);
                response.setErrorMessage("Invalid credentials");
                this.log(Level.WARNING, "SwingUI login failed: invalid password for user '" + request.getUsername() + "'");
                return response;
            }

            // Success - create User object
            User user = new User();
            user.setName(dbUser.getUsername());

            // Store user in session attributes
            session.setAttribute(SESSION_ATTRIB_USER, user.getName());
            session.setAttribute(SESSION_ATTRIB_CLIENT_TYPE, Integer.valueOf(BaseClient.CLIENT_RICH_CLIENT));
            // Mark session as authenticated
            session.setAttribute(SESSION_ATTRIB_AUTHENTICATED, Boolean.TRUE);

            response.setSuccess(true);
            response.setUser(user);
            response.setMustChangePassword(dbUser.isMustChangePassword());

            this.log(Level.INFO, "SwingUI login successful: " + request.getUsername() +
                    " from " + remoteAddr);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("Authentication error: " + e.getMessage());
            this.log(Level.SEVERE, "SwingUI login error: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    this.log(Level.WARNING, "Failed to close database connection: " + e.getMessage());
                }
            }
        }

        return response;
    }
}
