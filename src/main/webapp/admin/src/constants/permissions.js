/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

/**
 * Permission constants for the AS2 WebUI
 * Must match backend Permissions.java
 */
export const PERMISSIONS = {
  // Partners
  PARTNER_READ: 'PARTNER_READ',
  PARTNER_WRITE: 'PARTNER_WRITE',

  // Certificates - Sign/Crypt
  CERT_READ: 'CERT_READ',
  CERT_WRITE: 'CERT_WRITE',

  // Certificates - TLS
  CERT_TLS_READ: 'CERT_TLS_READ',
  CERT_TLS_WRITE: 'CERT_TLS_WRITE',

  // Messages
  MESSAGE_READ: 'MESSAGE_READ',
  MESSAGE_WRITE: 'MESSAGE_WRITE',

  // System Configuration
  SYSTEM_CONFIG_CONNECTIVITY: 'SYSTEM_CONFIG_CONNECTIVITY',
  SYSTEM_CONFIG_INBOUND_AUTH: 'SYSTEM_CONFIG_INBOUND_AUTH',
  SYSTEM_CONFIG_DIRECTORIES: 'SYSTEM_CONFIG_DIRECTORIES',
  SYSTEM_CONFIG_MAINTENANCE: 'SYSTEM_CONFIG_MAINTENANCE',
  SYSTEM_CONFIG_NOTIFICATIONS: 'SYSTEM_CONFIG_NOTIFICATIONS',
  SYSTEM_CONFIG_INTERFACE: 'SYSTEM_CONFIG_INTERFACE',
  SYSTEM_CONFIG_LOGGING: 'SYSTEM_CONFIG_LOGGING',

  // System Monitoring
  SYSTEM_INFO_READ: 'SYSTEM_INFO_READ',
  SYSTEM_EVENTS_READ: 'SYSTEM_EVENTS_READ',
  SYSTEM_LOGS_READ: 'SYSTEM_LOGS_READ',

  // Tracker
  TRACKER_CONFIG_READ: 'TRACKER_CONFIG_READ',
  TRACKER_CONFIG_WRITE: 'TRACKER_CONFIG_WRITE',
  TRACKER_MESSAGE_READ: 'TRACKER_MESSAGE_READ',

  // User Management
  USER_MANAGE: 'USER_MANAGE',
  USER_SWITCH: 'USER_SWITCH',
};

/**
 * Permission categories for organizing permissions in the UI
 */
export const PERMISSION_CATEGORIES = {
  PARTNERS: 'Partners',
  CERTIFICATES_SIGN: 'Certificates - Sign/Crypt',
  CERTIFICATES_TLS: 'Certificates - TLS',
  MESSAGES: 'Messages',
  SYSTEM_CONFIG: 'System Configuration',
  SYSTEM_MONITORING: 'System Monitoring',
  TRACKER: 'Tracker',
  USER_MANAGEMENT: 'User Management',
};

/**
 * Permission metadata with descriptions and categories
 * This structure matches the database schema and helps with UI rendering
 */
export const PERMISSION_METADATA = {
  [PERMISSIONS.PARTNER_READ]: {
    category: PERMISSION_CATEGORIES.PARTNERS,
    description: 'View partner configurations',
  },
  [PERMISSIONS.PARTNER_WRITE]: {
    category: PERMISSION_CATEGORIES.PARTNERS,
    description: 'Create and modify partners',
  },
  [PERMISSIONS.CERT_READ]: {
    category: PERMISSION_CATEGORIES.CERTIFICATES_SIGN,
    description: 'View sign/crypt certificates',
  },
  [PERMISSIONS.CERT_WRITE]: {
    category: PERMISSION_CATEGORIES.CERTIFICATES_SIGN,
    description: 'Manage sign/crypt certificates',
  },
  [PERMISSIONS.CERT_TLS_READ]: {
    category: PERMISSION_CATEGORIES.CERTIFICATES_TLS,
    description: 'View TLS/SSL certificates',
  },
  [PERMISSIONS.CERT_TLS_WRITE]: {
    category: PERMISSION_CATEGORIES.CERTIFICATES_TLS,
    description: 'Manage TLS/SSL certificates',
  },
  [PERMISSIONS.MESSAGE_READ]: {
    category: PERMISSION_CATEGORIES.MESSAGES,
    description: 'View AS2 messages',
  },
  [PERMISSIONS.MESSAGE_WRITE]: {
    category: PERMISSION_CATEGORIES.MESSAGES,
    description: 'Send and resend messages',
  },
  [PERMISSIONS.SYSTEM_CONFIG_CONNECTIVITY]: {
    category: PERMISSION_CATEGORIES.SYSTEM_CONFIG,
    description: 'Modify HTTP/HTTPS ports and proxy settings',
  },
  [PERMISSIONS.SYSTEM_CONFIG_INBOUND_AUTH]: {
    category: PERMISSION_CATEGORIES.SYSTEM_CONFIG,
    description: 'Configure inbound authentication',
  },
  [PERMISSIONS.SYSTEM_CONFIG_DIRECTORIES]: {
    category: PERMISSION_CATEGORIES.SYSTEM_CONFIG,
    description: 'Change message directories',
  },
  [PERMISSIONS.SYSTEM_CONFIG_MAINTENANCE]: {
    category: PERMISSION_CATEGORIES.SYSTEM_CONFIG,
    description: 'Configure auto-delete and cleanup settings',
  },
  [PERMISSIONS.SYSTEM_CONFIG_NOTIFICATIONS]: {
    category: PERMISSION_CATEGORIES.SYSTEM_CONFIG,
    description: 'Configure email notifications',
  },
  [PERMISSIONS.SYSTEM_CONFIG_INTERFACE]: {
    category: PERMISSION_CATEGORIES.SYSTEM_CONFIG,
    description: 'Modify UI preferences',
  },
  [PERMISSIONS.SYSTEM_CONFIG_LOGGING]: {
    category: PERMISSION_CATEGORIES.SYSTEM_CONFIG,
    description: 'Configure logging settings',
  },
  [PERMISSIONS.SYSTEM_INFO_READ]: {
    category: PERMISSION_CATEGORIES.SYSTEM_MONITORING,
    description: 'View HTTP server information',
  },
  [PERMISSIONS.SYSTEM_EVENTS_READ]: {
    category: PERMISSION_CATEGORIES.SYSTEM_MONITORING,
    description: 'View system events',
  },
  [PERMISSIONS.SYSTEM_LOGS_READ]: {
    category: PERMISSION_CATEGORIES.SYSTEM_MONITORING,
    description: 'Search server logs',
  },
  [PERMISSIONS.TRACKER_CONFIG_READ]: {
    category: PERMISSION_CATEGORIES.TRACKER,
    description: 'View tracker configuration',
  },
  [PERMISSIONS.TRACKER_CONFIG_WRITE]: {
    category: PERMISSION_CATEGORIES.TRACKER,
    description: 'Modify tracker settings',
  },
  [PERMISSIONS.TRACKER_MESSAGE_READ]: {
    category: PERMISSION_CATEGORIES.TRACKER,
    description: 'View tracker messages',
  },
  [PERMISSIONS.USER_MANAGE]: {
    category: PERMISSION_CATEGORIES.USER_MANAGEMENT,
    description: 'Manage users and roles',
  },
  [PERMISSIONS.USER_SWITCH]: {
    category: PERMISSION_CATEGORIES.USER_MANAGEMENT,
    description: 'Impersonate other users',
  },
};
