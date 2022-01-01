(ns app.users
  (:require
   [app.auth :as auth :refer [create-token]]
   [app.db :refer [find-document insert-document insert-multiple]]
   [cheshire.core :refer :all]
   [monger.result :refer :all]))

(def email-regexp #".+\@.+\..+")

(defn verify-login [name]
  (if-let [user (find-document "users" {:name name})]
    user
    nil))

(defn login [data]
  (let [name (get-in data [:name])]
    (if-let [user (verify-login name)]
      {:status 200
       :body
       {:token (create-token user)
        :message "Authorization success"}}
      (let [user (insert-document "users" {:name name})]
        {:status 200
         :body
         {:token (create-token {:name name})
          :message "User created"}}))))

(defn add-users [users]
  (if (insert-multiple "users" users)
    {:status 200}
    {:status 409}))

(defn get-me [req]
  (if-let [user (find-document "users"
                               {:name (get-in req [:user :name])})]
    {:status 200 :body {:data user}}
    {:status 409}))

