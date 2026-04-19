# Changelog

All notable changes to Mend AS2 will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- **BREAKING: Removed Apache Mina client-server framework** - Replaced with in-memory EventBus
  - SwingUI now uses EventBus for server communication (no network ports)
  - Zero attack surface for GUI mode (no TCP socket exposure)
  - Eliminates startup race conditions and port binding conflicts
  - Improves performance: zero-latency events vs. TCP socket overhead
  - Simplifies architecture: direct method calls vs. network protocol
  - See [MINA_REMOVAL.md](md-memo/MINA_REMOVAL.md) for technical details
- SwingUI authentication removed (runs in same JVM, no login needed)
- Improved security: no network ports for SwingUI communication

### Removed
- Apache Mina dependency (~2700 lines of networking code)
- Mina TCP port 1234 (no longer exposed)
- SwingUI login dialog (authentication not needed for in-process communication)
- 18 Mina infrastructure files

### Added
- EventBus system for in-memory pub/sub notifications
- AS2MessageProcessor for direct HttpReceiver → AS2ServerProcessing calls
- 21 backward-compatibility stub classes (maintain API compatibility)
- Technical documentation: MINA_REMOVAL.md

### Fixed
- Startup race conditions between HttpReceiver and Mina server
- "Session closed by remote host" errors
- "Connection refused" errors on startup
- Port binding conflicts on port 1234

## [1.1.0] - 2026-03-XX

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

## [1.0.0] - 2025-XX-XX

### Added
- Initial fork from mendelson AS2
- PostgreSQL database support
- React-based WebUI
- Basic user management
- Certificate management
- Partner configuration
- Message sending and tracking

[Unreleased]: https://github.com/zc2tech/mend-as2/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/zc2tech/mend-as2/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/zc2tech/mend-as2/releases/tag/v1.0.0
