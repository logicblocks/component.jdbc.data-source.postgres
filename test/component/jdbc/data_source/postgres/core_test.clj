(ns component.jdbc.data-source.postgres.core-test
  (:require
   [clojure.test :refer :all]
   [com.stuartsierra.component :as component]
   [component.jdbc.data-source.postgres.core :as data-source]
   [component.jdbc.data-source.postgres.configuration :as configuration]
   [configurati.component :as conf-comp]
   [configurati.core :as conf])
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

(deftest creates-postgres-data-source-with-default-parameters
  (let [configuration (configuration)]
    (with-started-component
      (data-source/component
        {:configuration configuration})
      (fn [component]
        (let [^PGSimpleDataSource data-source (:datasource component)]
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

(deftest uses-specified-configuration-for-postgres-data-source-when-provided
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
    (with-started-component
      (data-source/component
        {:configuration configuration})
      (fn [component]
        (let [^PGSimpleDataSource data-source (:datasource component)]
          (is (= (.getReadOnly data-source) true))
          (is (= (.getConnectTimeout data-source) 2))
          (is (= (.getLoginTimeout data-source) 5))
          (is (= (.getSocketTimeout data-source) 30))
          (is (= (.getSslMode data-source) "prefer"))
          (is (= (.getSslRootCert data-source) "ca.crt"))
          (is (= (.getSslCert data-source) "client.crt"))
          (is (= (.getSslKey data-source) "client.key"))
          (is (= (.getSslPassword data-source) "some-password")))))))

(deftest configures-component-using-default-specification
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
          :ssl-password "some-password")
        component (data-source/component)
        component (conf-comp/configure component
                    {:configuration-source (conf/map-source configuration)})]
    (is (= configuration (:configuration component)))))

(deftest allows-specification-to-be-overridden
  (let [configuration
        {:port          5555
         :user          "ops"
         :password      "super-secret"
         :database-name "the-database"}
        specification
        (conf/configuration-specification
          (conf/with-parameter :host :default "127.0.0.1")
          (conf/with-parameter configuration/port-parameter)
          (conf/with-parameter configuration/user-parameter)
          (conf/with-parameter configuration/password-parameter)
          (conf/with-parameter configuration/database-name-parameter))
        component (data-source/component
                    {:configuration-specification specification})
        component (conf-comp/configure component
                    {:configuration-source (conf/map-source configuration)})]
    (is (= {:host          "127.0.0.1"
            :port          5555
            :user          "ops"
            :password      "super-secret"
            :database-name "the-database"}
          (:configuration component)))))

(deftest allows-default-source-to-be-provided
  (let [default-source
        (conf/map-source
          {:host            "127.0.0.1"
           :port            5566
           :user            "admin"
           :password        "super-secret"
           :read-only       false
           :connect-timeout 2
           :login-timeout   5
           :socket-timeout  30})
        configure-time-source
        (conf/map-source
          {:user          "ops"
           :password      "everyone-knows"
           :database-name "the-database"
           :read-only     true
           :ssl-mode      "prefer"
           :ssl-root-cert "ca.crt"
           :ssl-cert      "client.crt"
           :ssl-key       "client.key"
           :ssl-password  "some-password"})
        component (data-source/component
                    {:configuration-source default-source})
        component (conf-comp/configure component
                    {:configuration-source configure-time-source})]
    (is (= {:host            "127.0.0.1"
            :port            5566
            :user            "ops"
            :password        "everyone-knows"
            :database-name   "the-database"
            :read-only       true
            :connect-timeout 2
            :login-timeout   5
            :socket-timeout  30
            :ssl-mode        "prefer"
            :ssl-root-cert   "ca.crt"
            :ssl-cert        "client.crt"
            :ssl-key         "client.key"
            :ssl-password    "some-password"}
          (:configuration component)))))

(deftest allows-configuration-lookup-key-to-be-provided
  (let [configuration-source
        (conf/map-source
          {:data-source-host            "localhost"
           :data-source-port            5432
           :data-source-user            "admin"
           :data-source-password        "super-secret"
           :data-source-database-name   "some-database"
           :data-source-read-only       true
           :data-source-connect-timeout 2
           :data-source-login-timeout   5
           :data-source-socket-timeout  30
           :data-source-ssl-mode        "prefer"
           :data-source-ssl-root-cert   "ca.crt"
           :data-source-ssl-cert        "client.crt"
           :data-source-ssl-key         "client.key"
           :data-source-ssl-password    "some-password"})
        component (data-source/component
                    {:configuration-lookup-prefix :data-source})
        component (conf-comp/configure component
                    {:configuration-source configuration-source})]
    (is (= {:host            "localhost"
            :port            5432
            :user            "admin"
            :password        "super-secret"
            :database-name   "some-database"
            :read-only       true
            :connect-timeout 2
            :login-timeout   5
            :socket-timeout  30
            :ssl-mode        "prefer"
            :ssl-root-cert   "ca.crt"
            :ssl-cert        "client.crt"
            :ssl-key         "client.key"
            :ssl-password    "some-password"}
          (:configuration component)))))
