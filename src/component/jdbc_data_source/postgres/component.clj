(ns component.jdbc-data-source.postgres.component
  (:require
   [com.stuartsierra.component :as component]

   [component.jdbc-data-source.postgres.data-sources :as data-sources])
  (:import [java.io Closeable]))

(defrecord PostgresJdbcDataSource
           [configuration data-source]
  component/Lifecycle

  (start [component]
    (let [postgres-data-source
          (data-sources/postgres-data-source configuration)
          hikari-data-source
          (data-sources/hikari-data-source
            (merge configuration
              {:data-source postgres-data-source}))]
      (assoc component :data-source hikari-data-source)))

  (stop [component]
    (let [^Closeable data-source (:data-source component)]
      (when data-source
        (.close data-source))
      (assoc component :data-source nil))))
