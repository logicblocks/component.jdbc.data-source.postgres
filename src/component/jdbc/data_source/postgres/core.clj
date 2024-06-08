(ns component.jdbc.data-source.postgres.core
  (:require
   [component.jdbc.data-source.postgres.component :as component]))

(defn component
  ([]
   (component/map->PostgresJdbcDataSource {}))
  ([{:keys [configuration-specification
            configuration-source
            configuration-lookup-prefix
            configuration
            logger]}]
   (component/map->PostgresJdbcDataSource
     {:configuration-specification configuration-specification
      :configuration-source        configuration-source
      :configuration-lookup-prefix configuration-lookup-prefix
      :configuration               configuration
      :logger                      logger})))
