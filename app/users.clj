(ns app.users
  (:require
   [app.db :refer [find-by-id find-document get-collection insert-multiple
                   update-by-id]]
   [app.utils :refer [log-through xlog-through]]
   [cheshire.core :refer :all]
   [monger.result :refer :all]))

(defn add-users [users]
  (if (insert-multiple "users" users) {:status 200} {:status 409}))

(defn get-users []
  (if-let [users (get-collection "users")]
    {:status 200 :body {:users users}}
    {:status 409}))

(defn get-user [id]
  (if-let [user (find-by-id "users" id)]
    {:status 200 :body {:data user}}
    {:status 409}))

(defn search-user [p]
  (if-let [user (find-document "users" p)]
    (do
      {:status 200 :body {:user user}})
    {:status 409}))

(defn update-user [body id]
  (if-let [user (update-by-id "users" id body)]
    (if-let [r (xlog-through (str "found with id " id) (find-by-id "users" id))]
      (do
        {:status 200 :body {:data r}}))
    {:status 409}))

(app.utils/with-mount (fn []
                        (log-through "users:" (get-users))))
