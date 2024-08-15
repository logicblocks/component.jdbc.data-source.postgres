(ns component.jdbc.data-source.postgres.data-sources
  (:import
   [org.postgresql.ds PGSimpleDataSource]))

(defn postgres-data-source
  [{:keys [host
           port
           user
           password
           database-name
           default-schema
           read-only
           login-timeout
           connect-timeout
           socket-timeout
           ssl-mode
           ssl-root-cert
           ssl-cert
           ssl-key
           ssl-password]}]
  (let [ds (PGSimpleDataSource.)]
    (when (some? host) (.setServerNames ds (into-array String [host])))
    (when (some? port) (.setPortNumbers ds (int-array [port])))
    (when (some? user) (.setUser ds user))
    (when (some? password) (.setPassword ds password))
    (when (some? database-name) (.setDatabaseName ds database-name))
    (when (some? default-schema) (.setCurrentSchema ds default-schema))
    (when (some? read-only) (.setReadOnly ds read-only))
    (when (some? login-timeout) (.setLoginTimeout ds (int login-timeout)))
    (when (some? connect-timeout) (.setConnectTimeout ds (int connect-timeout)))
    (when (some? socket-timeout) (.setSocketTimeout ds (int socket-timeout)))
    (when (some? ssl-mode) (.setSslMode ds ssl-mode))
    (when (some? ssl-root-cert) (.setSslRootCert ds ssl-root-cert))
    (when (some? ssl-cert) (.setSslCert ds ssl-cert))
    (when (some? ssl-key) (.setSslKey ds ssl-key))
    (when (some? ssl-password) (.setSslPassword ds ssl-password))
    ds))
