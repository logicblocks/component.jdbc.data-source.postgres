(ns component.jdbc.data-source.postgres.core-test
  (:require
   [clojure.test :refer :all]
   [com.stuartsierra.component :as component]
   [component.jdbc.data-source.postgres.core :as data-source])
  (:import
   [java.util.concurrent TimeUnit]
   [org.postgresql.ds PGSimpleDataSource]))

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

(deftest creates-data-source-with-default-parameters
  (let [configuration (configuration)]
    (with-started-component (data-source/component configuration)
      (fn [component]
        (let [^PGSimpleDataSource data-source (:data-source component)]
          (is (= (into [] (.getServerNames data-source))
                [(:host configuration)]))
          (is (= (into [] (.getPortNumbers data-source))
                [(:port configuration)]))
          (is (= (.getUser data-source) (:user configuration)))
          (is (= (.getPassword data-source) (:password configuration)))
          (is (= (.getDatabaseName data-source) (:database-name configuration)))
          (is (= (.getReadOnly data-source) false))
          (is (= (.getLoginTimeout data-source) 0))
          (is (= (.getConnectTimeout data-source) 10))
          (is (= (.getSocketTimeout data-source) 0))
          (is (nil? (.getSslMode data-source)))
          (is (nil? (.getSslRootCert data-source)))
          (is (nil? (.getSslCert data-source)))
          (is (nil? (.getSslKey data-source)))
          (is (nil? (.getSslPassword data-source))))))))

(deftest uses-specified-configuration-for-data-source-when-provided
  (let [configuration
        (configuration
          :read-only true
          :connect-timeout 2
          :login-timeout 5
          :socket-timeout 30
          :ssl-mode "prefer"
          :ssl-root-cert "ca.crt"
          :ssl-cert "client.crt"
          :ssl-key "client.key"
          :ssl-password "some-password")]
    (with-started-component (data-source/component configuration)
      (fn [component]
        (let [^PGSimpleDataSource data-source (:data-source component)]
          (is (= (.getReadOnly data-source) true))
          (is (= (.getConnectTimeout data-source) 2))
          (is (= (.getLoginTimeout data-source) 5))
          (is (= (.getSocketTimeout data-source) 30))
          (is (= (.getSslMode data-source) "prefer"))
          (is (= (.getSslRootCert data-source) "ca.crt"))
          (is (= (.getSslCert data-source) "client.crt"))
          (is (= (.getSslKey data-source) "client.key"))
          (is (= (.getSslPassword data-source) "some-password")))))))
