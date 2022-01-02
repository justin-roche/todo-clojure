(ns app.server
  (:require
   [app.config :refer [config-map]]
   [app.router :as r :refer [routes route-data]]
   [io.pedestal.http :as ps]
   [io.pedestal.http :as http]
   [mount.core :as mount :refer [defstate]]
   [reitit.http :as rt]
   [reitit.pedestal :as pedestal]
   [taoensso.truss :as truss :refer [have]]))

(defn start-server [service-map]
  (have keyword? (::http/type service-map))
  (-> service-map
      (ps/default-interceptors)
      (pedestal/replace-last-interceptor
       (pedestal/routing-interceptor
        (rt/router routes route-data)))
      (ps/dev-interceptors)
      (ps/create-server)
      (ps/start)))

(defstate s
  :start (start-server (:server config-map))
  :stop (http/stop s))
