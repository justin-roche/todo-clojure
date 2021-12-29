(ns app.user-tests
  (:require
   [app.test-data :as test-data :refer [auth-headers]]
   [app.test-utils :as test-utils :refer [get-options system-fixture]]
   [clj-http.client :as client]
   [clojure.test :as t :refer [deftest use-fixtures]]
   [clojure.tools.reader]))

(defn get-users-req []
  (let [o (merge (get-options) auth-headers)
        r (client/get "http://localhost:8890/users" (get-options))]
    r))

(defn get-user-req [id]
  (client/get (str "http://localhost:8890/user/" id)  (get-options)))

(deftest user-requests
  (let [users (get-users-req)]
    (t/is (= 200 (:status users)))
    (t/is (= 2 (count (:users (:body users)))))))

(use-fixtures :each system-fixture)
