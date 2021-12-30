(ns app.test-utils
  (:require
   [aprint.core :refer [aprint]]
   [buddy.hashers :as buddy-hashers]
   [app.db :as db]
   [clojure.tools.reader]
   [app.users :as users]
   [cheshire.core :as cheshire]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [clojure.test :as t :refer [deftest use-fixtures]]
   [mount.core :as mount :refer [start stop]]
   [app.utils :as utils]
   [app.test-data :as test-data]
   [app.auth :as auth]))

(def base "http://localhost:8890/")

(defn create-auth-header [username]
  (let [token (auth/create-token {:username username})]
    {:headers {"Authorization" token}}))

(def auth-headers {:headers {"Authorization" "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6MSwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sImV4cCI6MTY0MDQwNjQ3MX0.K_FUbrjpPd2IJaOYnqmG8DtPe43omUueAoupCLC_c7OhCZ68jwm6DgaAlJCN1tPViFP9_-FOeZ6ofR8FxJAYMg"}})

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


