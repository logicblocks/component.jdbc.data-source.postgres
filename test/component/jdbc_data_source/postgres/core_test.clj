(ns component.jdbc-data-source.postgres.core-test
  (:require
   [clojure.test :refer :all]

   [com.stuartsierra.component :as component]

   [component.jdbc-data-source.postgres.core :as data-source])
  (:import
   [com.zaxxer.hikari HikariDataSource]
   [java.util.concurrent TimeUnit]
   [com.impossibl.postgres.jdbc PGDataSource]))

(defn configuration [& {:as overrides}]
  (merge
    {:host          "localhost"
     :port          5432
     :user          "admin"
     :password      "super-secret"
     :database-name "some-database"}
    overrides))

(defn with-started-component [component f]
  (let [container (atom component)]
    (try
      (do
        (swap! container component/start)
        (f @container))
      (finally
        (swap! container component/stop)))))

(deftest creates-connection-pooled-data-source
  (let [configuration (configuration)]
    (with-started-component (data-source/component configuration)
      (fn [component]
        (let [^HikariDataSource data-source (:data-source component)]
          (is (= (.getMaximumPoolSize data-source) 10))
          (is (= (.getMinimumIdle data-source) 10))
          (is (= (.getIdleTimeout data-source)
                (.toMillis TimeUnit/MINUTES 10)))
          (is (= (.getConnectionTimeout data-source)
                (.toMillis TimeUnit/SECONDS 30)))
          (is (= (.getMaxLifetime data-source)
                (.toMillis TimeUnit/MINUTES 30)))
          (is (not (nil? (.getPoolName data-source))))
          (is (true? (.isAutoCommit data-source))))))))

(deftest uses-provided-configuration-for-pooled-data-source
  (let [configuration
        (configuration
          :pool-name "main"
          :maximum-pool-size 15
          :minimum-idle 10
          :idle-timeout (.toMillis TimeUnit/MINUTES 15)
          :connection-timeout (.toMillis TimeUnit/SECONDS 20)
          :maximum-lifetime (.toMillis TimeUnit/MINUTES 20)
          :auto-commit false)]
    (with-started-component (data-source/component configuration)
      (fn [component]
        (let [^HikariDataSource data-source (:data-source component)]
          (is (= (.getMaximumPoolSize data-source) 15))
          (is (= (.getMinimumIdle data-source) 10))
          (is (= (.getIdleTimeout data-source)
                (.toMillis TimeUnit/MINUTES 15)))
          (is (= (.getConnectionTimeout data-source)
                (.toMillis TimeUnit/SECONDS 20)))
          (is (= (.getMaxLifetime data-source)
                (.toMillis TimeUnit/MINUTES 20)))
          (is (= (.getPoolName data-source) "main"))
          (is (false? (.isAutoCommit data-source))))))))

(deftest uses-postgres-data-source-internally
  (let [configuration (configuration)]
    (with-started-component (data-source/component configuration)
      (fn [component]
        (let [^HikariDataSource data-source (:data-source component)
              ^PGDataSource data-source (.getDataSource data-source)]
          (is (= (.getHost data-source) (:host configuration)))
          (is (= (.getPort data-source) (:port configuration)))
          (is (= (.getUser data-source) (:user configuration)))
          (is (= (.getPassword data-source) (:password configuration)))
          (is (= (.getDatabaseName data-source) (:database-name configuration)))
          (is (= (.getReadOnly data-source) false))
          (is (= (.getLoginTimeout data-source) 30))
          (is (= (.getNetworkTimeout data-source) 0))
          (is (= (.getSslMode data-source) "disable"))
          (is (= (.getSslHomeDir data-source) ".postgresql"))
          (is (= (.getSslCaCertificateFile data-source) "root.crt"))
          (is (= (.getSslCertificateFile data-source) "postgresql.crt"))
          (is (= (.getSslKeyFile data-source) "postgresql.pk8"))
          (is (nil? (.getSslKeyPassword data-source))))))))

(deftest uses-provided-configuration-for-postgres-data-source
  (let [configuration
        (configuration
          :read-only true
          :network-timeout (.toMillis TimeUnit/SECONDS 2)
          :ssl-mode "prefer"
          :ssl-home-dir (System/getProperty "user.dir")
          :ssl-ca-certificate-file "ca.crt"
          :ssl-certificate-file "client.crt"
          :ssl-key-file "client.key"
          :ssl-key-password "some-password")]
    (with-started-component (data-source/component configuration)
      (fn [component]
        (let [^HikariDataSource data-source (:data-source component)
              ^PGDataSource data-source (.getDataSource data-source)]
          (is (= (.getReadOnly data-source) true))
          (is (= (.getNetworkTimeout data-source)
                (.toMillis TimeUnit/SECONDS 2)))
          (is (= (.getSslMode data-source) "prefer"))
          (is (= (.getSslHomeDir data-source) (System/getProperty "user.dir")))
          (is (= (.getSslCaCertificateFile data-source) "ca.crt"))
          (is (= (.getSslCertificateFile data-source) "client.crt"))
          (is (= (.getSslKeyFile data-source) "client.key"))
          (is (= (.getSslKeyPassword data-source) "some-password")))))))
