(ns app.db
  (:require
   [app.config :refer [config-map]]
   [app.user]
   [app.utils :as utils]
   [cheshire.core :refer [generate-string]]
   [clojure.data.json :as json]
   [clojure.string :as str]
   [monger.collection :as mc]
   [monger.conversion :as mcv]
   [monger.core :as mg]
   [monger.credentials :as mgc]
   [monger.db :as mdb]
   [monger.operators :refer :all]
   [monger.result :as mr]
   [monger.util :refer :all]
   [mount.core :refer [defstate]]))

(defn db-connect [{:keys [cred-db cred-user cred-password host port db]}]
  (let [cred (mgc/create cred-user cred-db cred-password)
        connection (mg/connect-with-credentials host port cred)
        db   (mg/get-db connection db)]
    {:db db :conn connection}))

(defn db-disconnect [connection]
  mg/disconnect connection)

(defstate conn :start (db-connect config-map)
  :stop (db-disconnect conn))

(defn get-id-string [d]
  (str/replace (mcv/from-db-object (get-in d [:_id]) true) "\"" ""))

(defn change-id-string [d]
  (dissoc (assoc d :id (get-id-string d)) :_id))

(defn reset-db []
  (mc/purge-many (:db conn) ["users"])
  (mc/purge-many (:db conn) ["todos"])
  {:status 200})

(defn insert [coll item]
  (mc/insert-and-return (:db conn) coll item))

(defn find-by-id [coll id]
  (mc/find-by-id (:db conn) coll  (object-id id)))

(defn find-document [coll q]
  (if-let [r (mc/find-one-as-map  (:db conn) coll q)]
    (change-id-string r)
    nil))

(defn find-documents [coll q]
  (if-let [r (mc/find-maps (:db conn) coll q)]
    (map change-id-string r)
    nil))

(defn insert-multiple [coll items]
  (mr/acknowledged? (mc/insert-batch (:db conn) coll items)))

(defn update-fields [coll q u]
  (mc/update (:db conn) coll q {$set u}))

