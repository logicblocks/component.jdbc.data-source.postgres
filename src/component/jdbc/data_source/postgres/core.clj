(ns component.jdbc.data-source.postgres.core
  (:require
   [component.jdbc.data-source.postgres.component :as component]))

(defn component
  ([] (component {}))
  ([configuration]
   (component/map->PostgresJdbcDataSource
     {:configuration configuration})))
