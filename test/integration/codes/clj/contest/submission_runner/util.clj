(ns integration.codes.clj.contest.submission-runner.util
  (:require [codes.clj.contest.submission-runner.routes :as routes]
            [com.stuartsierra.component :as component]
            [parenthesin.components.config.aero :as components.config]
            [parenthesin.components.db.jdbc-hikari :as components.database]
            [parenthesin.components.http.clj-http :as components.http]
            [parenthesin.components.router.reitit-malli :as components.router]
            [parenthesin.components.server.reitit-pedestal-jetty :as components.webserver]
            [parenthesin.helpers.logs :as logs]
            [parenthesin.helpers.migrations :as migrations]
            [pg-embedded-clj.core :as pg-emb]))

(defn- create-and-start-components! []
  (component/start-system
   (component/system-map
    :config (components.config/new-config)
    :http (components.http/new-http-mock {})
    :router (components.router/new-router routes/routes)
    :database (component/using (components.database/new-database)
                               [:config])
    :webserver (component/using (components.webserver/new-webserver)
                                [:config :http :router :database]))))

(defn start-system!
  ([]
   ((start-system! create-and-start-components!)))
  ([system-start-fn]
   (fn []
     (logs/setup :info :auto)
     (pg-emb/init-pg)
     (migrations/migrate (migrations/configuration-with-db))
     (system-start-fn))))

(defn stop-system!
  [system]
  (component/stop-system system)
  (pg-emb/halt-pg!))

