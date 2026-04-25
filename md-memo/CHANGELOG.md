# Changelog

All notable changes to Mend AS2 will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

### Removed

### Added

### Fixed

## [1.1.2] - 2026-04-20

### Added
- IP Whitelist
- MDN detail displayed issue fixed
- option to disable dirpoll on system level
- 'My Tracker Config' to support basic/cert auth on user level
- Fix selected sing/crypt algorithm not matching real world

### Changed
- Remove user-level TLS certificate. All user certs should from My sign/crypt/auth

### Security

## [1.1.1] - 2026-04-20

### Added
- Role-Based Access Control (RBAC)
- HTTP Authentication preferences (outbound)
- Inbound message authentication (Basic + Certificate)
- Partner visibility controls
- Message filtering by partner visibility
- Forced password change for admin user
- Mode selection (GUI vs headless) via config/flag
- Database auto-creation on first run
- Fat JAR with all dependencies
- Single release distribution

### Changed
- Database: PostgreSQL (replaced HSQLDB)
- WebUI: React (replaced Vaadin)
- Auth: JWT-based (replaced session-based)
- Modern Tech Stack: Java 17+, Jetty 12, Jakarta EE 10

### Security
- JWT authentication with HttpOnly cookies
- PBKDF2 password hashing
- Permission-based API endpoint protection
- Partner-level visibility controls
- User-specific HTTP authentication credentials

## [1.1.0] - 2026-04-08

### Added
- Initial fork from mendelson AS2
- PostgreSQL database support
- React-based WebUI
- Basic user management
- Certificate management
- Partner configuration
- Message sending and tracking

[Unreleased]: https://github.com/zc2tech/mend-as2/compare/v1.1.2...HEAD
[1.1.1]: https://github.com/zc2tech/mend-as2/compare/v1.1.0...v1.1.1
[1.1.2]: https://github.com/zc2tech/mend-as2/releases/tag/v1.1.2
