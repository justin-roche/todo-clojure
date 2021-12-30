(ns app.users
  (:require
   [app.db :refer [find-document  insert-multiple]]
   [app.utils :refer [log-through]]
   [cheshire.core :refer :all]
   [monger.result :refer :all]))

(defn add-users [users]
  (if (insert-multiple "users" users)
    {:status 200}
    {:status 409}))

(defn search-user [q]
  (if-let [user (find-document "users" q)]
    {:status 200 :body {:data user}}
    {:status 409}))

(defn get-me [req]
  (search-user {:name (get-in req [:user :username])}))


