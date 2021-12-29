(ns app.user-tests
  (:require
   [aprint.core :refer [aprint]]
   [buddy.hashers :as buddy-hashers]
   [app.db :as db]
   [clojure.tools.reader]
   [app.users :as users]
   [cheshire.core :as cheshire]

   [app.test-utils :as test-utils :refer [get-options post-options system-fixture]]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [app.test-data :as test-data :refer [auth-headers invalid-auth-headers non-admin-auth-headers]]
   [clojure.test :as t :refer [deftest use-fixtures]]
   [mount.core :as mount :refer [start stop]]
   [app.utils :as utils]))

(defn get-users-req []
  (let [o (merge (get-options) auth-headers)
        r (client/get "http://localhost:8890/users" (get-options))]
    r))

(defn get-user-req [id]
  (client/get (str "http://localhost:8890/user/" id)  (get-options)))

(defn search-req []
  (client/get "http://localhost:8890/user-search"  (get-options {:name "A"})))

(defn update-user-req [u id]
  (let [j (json/write-str u)
        url (str "http://localhost:8890/user/" id)
        r (client/post url (post-options j))]
    r))

(deftest user-requests
  (let [users (get-users-req)]
    (t/is (= 200 (:status users)))
    (t/is (= 2 (count (:users (:body users))))))
  (let [user (search-req)]
    (t/is (= 200 (:status user)))
    (t/is (= "A" (:name (:user (:body user))))))
  (let [user (:user (:body (search-req)))
        id (:id user)
        get (get-user-req id)]
    (t/is (= "A" (:name user)))
    (t/is (= "A" (:name (:data (:body get))))))
  (let [user (:user (:body (search-req)))
        id (:id user)
        update (update-user-req (merge user {:age 2}) id)]
    (t/is (= 2 (:age (:data (:body update)))))
    (t/is (= "A" (:name user)))))

(use-fixtures :each system-fixture)
