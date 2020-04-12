(ns component.jdbc-data-source.postgres.data-sources
  (:import
   [clojure.lang Reflector]
   [com.impossibl.postgres.jdbc PGDataSource]
   [com.zaxxer.hikari HikariConfig HikariDataSource]))

(defn- doto-if-present [value thing method]
  (if-not (nil? value)
    (let [; satisfy linter
          _ (Reflector/invokeInstanceMethod
              thing (name method) (object-array [value]))]
      thing)
    thing))

(defn- convert-if-present [value converter]
  (if-not (nil? value) (converter value) value))

(defn postgres-data-source
  [{:keys [host
           port
           user
           password
           database-name
           read-only
           login-timeout
           network-timeout
           ssl-mode
           ssl-home-dir
           ssl-ca-certificate-file
           ssl-certificate-file
           ssl-key-file
           ssl-key-password]}]
  (reduce
    (fn [data-source [value method]]
      (doto-if-present value data-source method))
    (PGDataSource.)
    [[host 'setHost]
     [port 'setPort]
     [user 'setUser]
     [password 'setPassword]
     [database-name 'setDatabaseName]
     [read-only 'setReadOnly]
     [(convert-if-present login-timeout int) 'setLoginTimeout]
     [(convert-if-present network-timeout int) 'setNetworkTimeout]
     [ssl-mode 'setSslMode]
     [ssl-home-dir 'setSslHomeDir]
     [ssl-ca-certificate-file 'setSslCaCertificateFile]
     [ssl-certificate-file 'setSslCertificateFile]
     [ssl-key-file 'setSslKeyFile]
     [ssl-key-password 'setSslKeyPassword]]))

(defn hikari-data-source
  [{:keys [data-source
           pool-name
           maximum-pool-size
           minimum-idle
           idle-timeout
           connection-timeout
           maximum-lifetime
           auto-commit]}]
  (let [hikari-config
        (reduce
          (fn [hikari-config [value method]]
            (doto-if-present value hikari-config method))
          (HikariConfig.)
          [[data-source 'setDataSource]
           [pool-name 'setPoolName]
           [maximum-pool-size 'setMaximumPoolSize]
           [minimum-idle 'setMinimumIdle]
           [idle-timeout 'setIdleTimeout]
           [connection-timeout 'setConnectionTimeout]
           [maximum-lifetime 'setMaxLifetime]
           [auto-commit 'setAutoCommit]])
        hikari-data-source (HikariDataSource. hikari-config)]
    hikari-data-source))
