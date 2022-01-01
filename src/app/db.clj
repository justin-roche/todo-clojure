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
        db   (mg/get-db connection db)]
    {:db db :conn connection}))

(defn db-disconnect [connection]
  mg/disconnect connection)

(defstate conn :start (db-connect (:db config-map))
  :stop (db-disconnect conn))

(defn get-id-string [d]
  (str/replace (mcv/from-db-object (get-in d [:_id]) true) "\"" ""))

(defn convert-bson-id-to-string [d]
  (dissoc (assoc d :id (get-id-string d)) :_id))

(defn reset-db []
  (mc/purge-many (:db conn) ["users"])
  {:status 200})

(defn insert [coll item]
  (mc/insert-and-return (:db conn) coll item))

(defn insert-subdocument "Insert a subdocument, providing a string uuid"
  [collection query subdocument-key document]
  (mc/update (:db conn)
             collection
             query
             {:$push  {(keyword subdocument-key)  (merge document {:id (mu/random-uuid)})}}))

(defn aggregate-subdocuments [collection pipeline]
  (mc/aggregate (:db conn)
                collection
                pipeline
                :cursor {}))

(defn update-subdocument [collection query setter]
  (mr/updated-existing? (mc/update (:db conn)
                                   collection
                                   query
                                   {:$set setter})))

(defn find-subdocument [coll doc-query subdocument-query]
  (mc/find-one-as-map (:db conn) coll doc-query subdocument-query))

(defn find-by-id [coll id]
  (mc/find-by-id (:db conn) coll  (mu/object-id id)))

(defn find-document [coll q]
  (if-let [r (mc/find-one-as-map  (:db conn) coll q)]
    (convert-bson-id-to-string r)
    nil))

(defn find-documents [coll q]
  (if-let [r (mc/find-maps (:db conn) coll q)]
    (map convert-bson-id-to-string r)
    nil))

(defn insert-multiple [coll items]
  (mr/acknowledged? (mc/insert-batch (:db conn) coll items)))

