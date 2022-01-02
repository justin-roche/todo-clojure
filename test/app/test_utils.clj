(ns app.test-utils
  (:require
   [app.auth :as auth]
   [app.db :as db]
   [app.test-data :as test-data]
   [clojure.tools.reader]
   [monger.collection :as mc]
   [mount.core :as mount :refer [start stop]]))

(def base "http://localhost:8890")

(defn create-auth-header [name]
  (let [token (auth/create-token {:name name})]
    {:headers {"Authorization" token}}))

(defn create-invalid-auth-header []
  (let [token "bad"]
    {:headers {"Authorization" token}}))

(defn make-url [fragment]
  (if (vector? fragment)
    (str base "/" (apply str (interpose "/" fragment)))
    (str base "/" fragment)))

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
  (mc/purge-many (:db db/db-state) ["users"])
  (doall (map #(db/insert-document "users" %1) test-data/test-users))
  (stop))

(defn system-fixture [test]
  (db-reset)
  (try
    (do  (start)
         (let [result (test)]
           (stop)
           result))
    (catch Exception e (do
                         (stop)
                         (println  "exception of type: " (type e))))))

