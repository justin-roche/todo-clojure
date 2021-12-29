(ns app.users
  (:require
   [app.db :refer [find-by-id find-document get-collection insert-multiple]]
   [app.utils :refer [log-through]]
   [cheshire.core :refer :all]
   [monger.result :refer :all]))

(defn add-users [users]
  (if (insert-multiple "users" users) {:status 200} {:status 409}))

(defn get-users []
  (if-let [users (get-collection "users")]
    {:status 200 :body {:users users}}
    {:status 409}))

(defn search-user [p]
  (if-let [user (find-document "users" p)]
    (do
      {:status 200 :body {:user user}})
    {:status 409}))

(defn get-me [req]
  (search-user {:name (:username (:user req))}))

(comment (app.utils/with-mount (fn []
                                 (log-through "users:" (get-users)))))


