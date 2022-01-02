(ns app.db
  (:require
   [app.config :refer [config-map]]
   [clojure.string :as str]
   [monger.collection :as mc]
   [monger.conversion :as mcv]
   [monger.core :as mg]
   [monger.credentials :as mgc]
   [monger.result :as mr]
   [monger.util :as mu]
   [mount.core :refer [defstate]]))

(defn db-connect [{:keys [cred-db cred-user cred-password host port db]}]
  (let [cred (mgc/create cred-user cred-db cred-password)
        connection (mg/connect-with-credentials host port cred)
        db (mg/get-db connection db)]
    {:db db :conn connection}))

(defstate db-state
  :start (db-connect (:db config-map))
  :stop (mg/disconnect (:conn db-state)))

(defn insert-document [coll item]
  (mc/insert-and-return (:db db-state) coll item))

(defn insert-subdocument "Insert a subdocument, creating a string uuid. `subdocument-key` is the key to an array of subdocuments. Returns uuid or nil."
  [collection query subdocument-key document]
  (let [id (mu/random-uuid)]
    (if (mc/update (:db db-state) collection query
                   {:$push  {(keyword subdocument-key)  (merge document {:id id})}})
      id
      nil)))

(defn update-subdocument "Update a subdocument found with `query`, setting  the key/value pairs in `set-value` map. Returns boolean success value."
  [collection query set-value]
  (mr/updated-existing? (mc/update (:db db-state) collection query {:$set set-value})))

(defn find-subdocument [coll doc-query subdocument-query]
  (mc/find-one-as-map (:db db-state) coll doc-query subdocument-query))

(defn _convert-bson-id-to-string [d]
  (str/replace (mcv/from-db-object (get-in d [:_id]) true) "\"" ""))

(defn _replace-bson-id-with-string [d]
  (dissoc (assoc d :id (_convert-bson-id-to-string d)) :_id))

(defn find-document [coll q]
  (if-let [r (mc/find-one-as-map  (:db db-state) coll q)]
    (_replace-bson-id-with-string r)
    nil))
