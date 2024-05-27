# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com)
and this project adheres to
[Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changes

- The data source component is no longer pooled by default and the HikariCP 
  data source has been extracted into a separate library.
- `org.postgresql/postgresql` has been used in place of
  `com.impossibl.pgjdbc-ng/pgjdbc-ng` for the PostgreSQL data source.
- If a `cartus.core/Logger` is set on the component, the lifecycle actions are
  logged.

## [0.1.2] — 2023-06-11

### Changes

- All dependencies have been upgraded to the latest available versions.

[0.1.2]: https://github.com/logicblocks/component.jdbc-data-source.postgres/compare/0.1.0...0.1.2
[Unreleased]: https://github.com/logicblocks/component.jdbc-data-source.postgres/compare/0.1.2...HEAD
