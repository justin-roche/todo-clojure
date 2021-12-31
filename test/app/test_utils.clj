(ns app.test-utils
  (:require
   [app.auth :as auth]
   [app.db :as db]
   [app.test-data :as test-data]
   [app.users :as users]
   [clojure.tools.reader]
   [mount.core :as mount :refer [start stop]]))

(def base "http://localhost:8890/")

(defn create-auth-header [name]
  (let [token (auth/create-token {:name name})]
    {:headers {"Authorization" token}}))

(defn get-options
  ([]
   {:socket-timeout 1000
    :throw-exceptions false
    :connection-timeout 1000
    :content-type :application/json
    :as :json
    :accept :json})
  ([p]
   {:socket-timeout 1000
    :connection-timeout 1000
    :throw-exceptions false
    :content-type :application/json
    :as :json
    :query-params p
    :accept :json}))

(defn post-options [b]
  {:socket-timeout 1000
   :throw-exceptions false
   :connection-timeout 1000
   :content-type :application/json
   :as :json
   :body b
   :accept :json})

(defn db-reset []
  (stop)
  (start)
  (db/reset-db)
  (users/add-users test-data/test-users)
  (stop))

(defn system-fixture [test]
  (db-reset)
  (try
    (do  (start)

         (let [result (test)]
           (stop)
           result))
    (catch Exception e (do ;;
                         (stop)
                         (println  "exception of type: " (type e))
                         (def big-error e)))))


