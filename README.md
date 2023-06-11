# component.jdbc-data-source.postgres

[![Clojars Project](https://img.shields.io/clojars/v/io.logicblocks/component.jdbc-data-source.postgres.svg)](https://clojars.org/io.logicblocks/component.jdbc-data-source.postgres)
[![Clojars Downloads](https://img.shields.io/clojars/dt/io.logicblocks/component.jdbc-data-source.postgres.svg)](https://clojars.org/io.logicblocks/component.jdbc-data-source.postgres)
[![GitHub Contributors](https://img.shields.io/github/contributors-anon/logicblocks/component.jdbc-data-source.postgres.svg)](https://github.com/logicblocks/component.jdbc-data-source.postgres/graphs/contributors)

A component providing a pooled postgres JDBC data source.

## Install

Add the following to your `project.clj` file:

```clj
[io.logicblocks/component.jdbc-data-source.postgres "0.1.2"]
```

## Documentation

* [API Docs](https://logicblocks.github.io/component.jdbc-data-source.postgres/index.html)

## Usage

```clojure
(require '[com.stuartsierra.component :as component])
(require '[component.jdbc-data-source.postgres.core 
            :as postgres-jdbc-data-source])

(def system
  (component/system-map
    :data-source (postgres-jdbc-data-source/create
                   {:host "localhost"
                    :port 5432
                    :user "admin"
                    :password "super-secret-password"
                    :database-name "test"})))
```

## License

Copyright &copy; 2023 LogicBlocks Maintainers

Distributed under the terms of the 
[MIT License](http://opensource.org/licenses/MIT).
