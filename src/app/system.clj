;; (ns app.system
;;   (:require [com.stuartsierra.component :as component]
;;             [environ.core :refer [env]]
;;             [app.db :as db]
;;             [io.pedestal.http :as http]
;;             [app.server :as server]
;;             [app.router :as rt]))

;; (def env-mode 'dev')

;; (defn new-system
;;   []
;;   (component/system-map
;;    :service-map
;;    {:env          env-mode
;;     ::http/type   :jetty
;;     ::http/port   8890
;;     ::http/join?  false
;;     ::http/routes  []}
;;    :db-config
;;    {:env          env-mode
;;     :port   27017
;;     :host (env :db-host)
;;     :db (env :db)
;;     :cred-user (env :cred-user)
;;     :cred-db (env :cred-db)
;;     :cred-password (env :cred-password)}
;;    :router
;;    (component/using (rt/new-router) [:db :service-map])
;;    :pedestal
;;    (component/using (server/new-server) [:service-map
;;                                          :router])))

