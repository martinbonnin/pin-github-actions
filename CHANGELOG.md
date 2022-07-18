# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## [Unreleased]

## [0.3.0] - 2022-07-17

### Changed

- There are no subcommands anymore. It's now possible to omit "pin": `pin-github-actions .` 
- The binary is now released as a jar to avoid having to notarize it.
 
## [0.2.0] - 2022-07-17

### Fixed

- Use Darwin Ktor engine instead of CIO. Fixes TLS.
 
## [0.1.0] - 2022-07-17

### Added

- Initial commit
