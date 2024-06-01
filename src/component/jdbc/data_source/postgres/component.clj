(ns component.jdbc.data-source.postgres.component
  (:require
   [com.stuartsierra.component :as component]
   [component.jdbc.data-source.postgres.data-sources :as data-sources]
   [component.jdbc.data-source.postgres.configuration :as configuration]
   [component.support.logging :as comp-log]
   [configurati.component :as conf-comp]
   [configurati.core :as conf]))

(defrecord PostgresJdbcDataSource
  [configuration-specification
   configuration-source
   configuration
   logger
   datasource]

  conf-comp/Configurable
  (configure [component opts]
    (comp-log/with-logging logger :component.jdbc.data-source.postgres
      {:phases  {:before :configuring :after :configured}}
      (assoc component
        :configuration
        (conf/resolve
          (conf/configuration
            (conf/with-specification
              (or configuration-specification configuration/specification))
            (conf/with-source
              (apply conf/multi-source
                (remove nil?
                  [(:configuration-source opts)
                   configuration-source]))))))))

  component/Lifecycle
  (start [component]
    (comp-log/with-logging logger :component.jdbc.data-source.postgres
      {:phases  {:before :starting :after :started}
       :context {:configuration configuration}}
      (assoc component
        :datasource (data-sources/postgres-data-source configuration))))

  (stop [component]
    (comp-log/with-logging logger :component.jdbc.data-source.postgres
      {:phases  {:before :stopping :after :stopped}
       :context {:configuration configuration}}
      (assoc component :datasource nil))))
