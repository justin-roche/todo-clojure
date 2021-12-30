(ns app.users
  (:require
   [app.db :refer [find-document  insert-multiple]]
   [app.utils :refer [log-through]]
   [cheshire.core :refer :all]
   [monger.result :refer :all]
   [app.auth :as auth :refer [create-token verify-login]]))

(defn add-users [users]
  (if (insert-multiple "users" users)
    {:status 200}
    {:status 409}))

(defn search-user [q]
  (if-let [user (find-document "users" q)]
    {:status 200 :body {:data user}}
    {:status 409}))

(defn login [rq]
  (let [username (get-in rq [:body-params :username])
        password (get-in rq [:body-params :password])]
    (if-let [user (verify-login username password)]
      {:status 200
       :token (create-token user)
       :body
       {:message "Authorization success"}}
      {:status 401})))

(defn get-me [req]
  (search-user {:name (get-in req [:user :username])}))


