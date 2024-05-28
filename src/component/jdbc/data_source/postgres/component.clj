(ns component.jdbc.data-source.postgres.component
  (:require
   [cartus.core :as log]
   [com.stuartsierra.component :as component]
   [component.jdbc.data-source.postgres.data-sources :as data-sources])
  (:import
   [java.io Closeable]))

(defn- with-logging-fn [logger target opts action-fn]
  (let [init-ms (System/currentTimeMillis)]
    (when logger
      (log/debug logger (keyword (name target)
                          (name (get-in opts [:phases :before])))
        (:context opts)))
    (let [result (action-fn)]
      (when logger
        (log/info logger (keyword (name target)
                           (name (get-in opts [:phases :after])))
          (merge {:elapsed-ms (- (System/currentTimeMillis) init-ms)}
            (:context opts))))
      result)))

(defmacro ^:private with-logging [logger target opts & body]
  `(with-logging-fn ~logger ~target ~opts
     (fn [] ~@body)))

(defrecord PostgresJdbcDataSource
  [configuration logger instance]

  component/Lifecycle
  (start [component]
    (with-logging logger :component.jdbc.data-source.postgres
      {:phases {:before :starting :after :started}
       :context {:configuration configuration}}
      (assoc component
        :instance (data-sources/postgres-data-source configuration))))

  (stop [component]
    (with-logging logger :component.jdbc.data-source.postgres
      {:phases {:before :stopping :after :stopped}
       :context {:configuration configuration}}
      (assoc component :instance nil))))
