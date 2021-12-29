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

(defn get-id-string [d]
  (str/replace (mcv/from-db-object (get-in d [:_id]) true) "\"" ""))

(defn change-id-string [d]
  (dissoc (assoc d :id (get-id-string d)) :_id))

(defn db-disconnect [connection]
  mg/disconnect connection)

(defn db-connect [{:keys [cred-db cred-user cred-password host port db]}]
  (let [cred (mgc/create cred-user cred-db cred-password)
        connection (mg/connect-with-credentials host port cred)
        db   (mg/get-db connection db)]
    {:db db :conn connection}))

(defstate conn :start (db-connect config-map)
  :stop (db-disconnect conn))

(defn create-users-collection []
  (try (mc/create (:db conn) "users" {})
       (catch Exception e (clojure.pprint/pprint (str "caught exception: " (.toString e))))))

(defn reset-db []
  (mc/purge-many (:db conn) ["users"])
  {:status 200})

(defn create-collection [db name]
  (try (mc/create db name {})
       (catch Exception e (clojure.pprint/pprint (str "caught exception: " (.toString e))))))

(defn get-collections [db]
  (mdb/get-collection-names db))

(defn get-collection [coll]
  (map change-id-string (mc/find-maps (:db conn) coll)))

(defn insert [db coll item]
  (mc/insert-and-return db coll item))

(defn find-by-id [coll id]
  (mc/find-by-id (:db conn) coll  (object-id id)))

(defn find-document [coll q]
  (if-let [r (mc/find-one-as-map  (:db conn) coll q)]
    (change-id-string r)
    nil))

(defn insert-multiple [coll items]
  (mr/acknowledged? (mc/insert-batch (:db conn) coll items)))

(defn update-fields [coll q u]
  (mc/update (:db conn) coll q {$set u}))

(defn update-by-id [coll oid q]
  (utils/xlog-through "updated by id" (mc/update-by-id (:db conn) coll (object-id oid) q)))

(defn save-by-id [coll oid q]
  (utils/xlog-through (mc/save-and-return (:db conn) coll oid q)))

(defn remove-document [db coll doc]
  (mc/remove db coll doc))

(defn remove-collection [db coll]
  (mc/remove db coll))

(defn update-multiple [db coll q u]
  (mc/update db coll  q u {:multi true}))

(defn update-all [db coll u]
  (mc/update db coll {} u {:multi true}))

;; (utils/with-mount (fn []
;;                     (initialize-db)))

