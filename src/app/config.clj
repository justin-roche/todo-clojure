(ns app.config
  (:require [mount.core :refer [defstate]]
            [io.pedestal.http :as http]
            [environ.core :refer [env]]
            [app.utils :as utils]))

(defstate config-map
  :start
  {:auth {:auth-key (env :auth-key)}
   :db {:host (env :db-host)
        :db (env :db)
        :port (Integer/parseInt (env :db-port))
        :cred-user (env :cred-user)
        :cred-db (env :cred-db)
        :cred-password (env :cred-password)}
   :server {::http/type   :jetty
            ::http/port   (Integer/parseInt (env :http-port))
            ::http/join?  false
            ::http/routes  []}})

