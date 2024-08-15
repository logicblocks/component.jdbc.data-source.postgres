(ns component.jdbc.data-source.postgres.configuration
  (:require
   [configurati.core :as conf]))

(def host-parameter
  (conf/parameter :host
    {:default "localhost"}))
(def port-parameter
  (conf/parameter :port
    {:type :integer :default 5432}))
(def user-parameter
  (conf/parameter :user))
(def password-parameter
  (conf/parameter :password))
(def database-name-parameter
  (conf/parameter :database-name))
(def default-schema-parameter
  (conf/parameter :default-schema
    {:nilable true}))
(def ssl-mode-parameter
  (conf/parameter :ssl-mode
    {:nilable true}))
(def ssl-root-cert-parameter
  (conf/parameter :ssl-root-cert
    {:nilable true}))
(def ssl-cert-parameter
  (conf/parameter :ssl-cert
    {:nilable true}))
(def ssl-key-parameter
  (conf/parameter :ssl-key
    {:nilable true}))
(def ssl-password-parameter
  (conf/parameter :ssl-password
    {:nilable true}))
(def read-only-parameter
  (conf/parameter :read-only
    {:type :boolean :default false}))
(def login-timeout-parameter
  (conf/parameter :login-timeout
    {:type :integer :default 60}))
(def socket-timeout-parameter
  (conf/parameter :socket-timeout
    {:type :integer :default 60}))
(def connect-timeout-parameter
  (conf/parameter :connect-timeout
    {:type :integer :default 60}))

(def specification
  (conf/configuration-specification
    (conf/with-parameter host-parameter)
    (conf/with-parameter port-parameter)
    (conf/with-parameter user-parameter)
    (conf/with-parameter password-parameter)
    (conf/with-parameter database-name-parameter)
    (conf/with-parameter default-schema-parameter)
    (conf/with-parameter ssl-mode-parameter)
    (conf/with-parameter ssl-root-cert-parameter)
    (conf/with-parameter ssl-cert-parameter)
    (conf/with-parameter ssl-key-parameter)
    (conf/with-parameter ssl-password-parameter)
    (conf/with-parameter read-only-parameter)
    (conf/with-parameter login-timeout-parameter)
    (conf/with-parameter socket-timeout-parameter)
    (conf/with-parameter connect-timeout-parameter)))
