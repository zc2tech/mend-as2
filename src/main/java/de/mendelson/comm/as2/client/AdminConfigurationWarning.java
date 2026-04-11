package de.mendelson.comm.as2.client;

import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.comm.as2.usermanagement.clientserver.UserListRequest;
import de.mendelson.comm.as2.usermanagement.clientserver.UserListResponse;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.systemevents.notification.clientserver.NotificationGetRequest;
import de.mendelson.util.systemevents.notification.clientserver.NotificationGetResponse;
import de.mendelson.util.systemevents.notification.NotificationData;
import de.mendelson.util.systemevents.notification.NotificationDataImplAS2;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Check and display configuration warnings for admin user
 *
 * @author Julian Xu
 */
public class AdminConfigurationWarning {

    private static final Logger LOGGER = Logger.getLogger("de.mendelson.as2.client");

    /**
     * Check configuration and show warnings if needed for admin user
     *
     * @param parent Parent frame
     * @param username Current username
     * @param baseClient Base client for server communication
     */
    public static void checkAndShowWarnings(JFrame parent, String username, BaseClient baseClient) {

        // Only check for admin user
        if (username == null || !username.equals("admin")) {
            return;
        }

        LOGGER.info("Checking admin configuration warnings...");

        // Run in background to not block UI
        new Thread(() -> {
            List<String> warnings = new ArrayList<>();

            try {
                // Check admin user's email
                LOGGER.info("Requesting user list...");
                UserListRequest userRequest = new UserListRequest();
                UserListResponse userResponse = (UserListResponse) baseClient.sendSync(userRequest);

                LOGGER.info("User list response: " + (userResponse != null ? "received" : "null"));
                if (userResponse != null && userResponse.getException() == null) {
                    List<WebUIUser> users = userResponse.getUsers();
                    LOGGER.info("Number of users: " + (users != null ? users.size() : "null"));
                    if (users != null) {
                        for (WebUIUser user : users) {
                            LOGGER.info("Checking user: " + user.getUsername());
                            if ("admin".equals(user.getUsername())) {
                                String email = user.getEmail();
                                LOGGER.info("Admin email: '" + email + "'");
                                if (email == null || email.trim().isEmpty()) {
                                    warnings.add("• Admin user email is not configured in File → User Management.");
                                    LOGGER.info("Added admin email warning");
                                }
                                break;
                            }
                        }
                    }
                } else if (userResponse != null && userResponse.getException() != null) {
                    LOGGER.warning("User list request failed: " + userResponse.getException().getMessage());
                }

                // Check notification settings
                NotificationGetRequest notifRequest = new NotificationGetRequest();
                NotificationGetResponse notifResponse = (NotificationGetResponse) baseClient.sendSync(notifRequest);

                if (notifResponse != null && notifResponse.getException() == null) {
                    NotificationData data = notifResponse.getData();
                    if (data instanceof NotificationDataImplAS2 impl) {
                        String mailServer = impl.getMailServer();
                        boolean useSMTPAuth = impl.usesSMTPAuthCredentials();
                        String smtpUser = impl.getSMTPUser();

                        // Check mail server
                        if (mailServer == null || mailServer.trim().isEmpty()) {
                            warnings.add("• Mail server host is not configured in File → Preferences → Notification.");
                        }

                        // Check SMTP user only if SMTP auth is enabled
                        if (useSMTPAuth && (smtpUser == null || smtpUser.trim().isEmpty())) {
                            warnings.add("• SMTP authorization user is not configured in File → Preferences → Notification.");
                        }
                    }
                }

            } catch (Exception e) {
                LOGGER.warning("Failed to check admin configuration: " + e.getMessage());
                return;
            }

            // Show warnings if any
            if (!warnings.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    showWarningDialog(parent, warnings);
                });
            }
        }).start();
    }

    private static void showWarningDialog(JFrame parent, List<String> warnings) {
        StringBuilder message = new StringBuilder();
        message.append("Configuration Warning:\n\n");

        for (String warning : warnings) {
            message.append(warning).append("\n\n");
        }

        JOptionPane.showMessageDialog(
            parent,
            message.toString(),
            "Admin Configuration Warning",
            JOptionPane.WARNING_MESSAGE
        );
    }
}
