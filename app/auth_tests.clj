(ns app.auth-tests
  (:require
   [aprint.core :refer [aprint]]
   [buddy.hashers :as buddy-hashers]
   [app.db :as db]
   [clojure.tools.reader]
   [app.test-utils :as test-utils :refer [get-options post-options system-fixture]]
   [app.users :as users]
   [cheshire.core :as cheshire]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [clojure.test :as t :refer [deftest use-fixtures]]
   [mount.core :as mount :refer [start stop]]
   [app.utils :as utils]
   [app.test-data :as test-data :refer [auth-headers invalid-auth-headers non-admin-auth-headers]]))

(defn admin-req []
  (let [o (merge (get-options) auth-headers)
        r (client/get "http://localhost:8890/admin" o)]
    r))

(defn valid-token-req []
  (let [o (merge (get-options) auth-headers)
        r (client/get "http://localhost:8890/authorized" o)]
    r))

(defn invalid-token-req []
  (let [o  (merge (get-options) invalid-auth-headers)
        r (client/get "http://localhost:8890/authorized" o)]
    r))

(defn denial-by-role-req []
  (let [o  (merge (get-options) non-admin-auth-headers)
        r (client/get "http://localhost:8890/admin" o)]
    r))

(defn login-req []
  (let [j (json/write-str {:username "A" :password "kissa13"})
        r (client/post "http://localhost:8890/login" (post-options j))]
    r))
(defn invalid-login-req []
  (let [j (json/write-str {:username "x" :password "kissa13"})
        r (client/post "http://localhost:8890/login" (post-options j))]
    r))
(defn login-normal-user-req []
  (let [j (json/write-str {:username "B" :password "koira12"})
        r (client/post "http://localhost:8890/login" (post-options j))]
    r))

(t/deftest auth
  (t/is (= 200 (:status (valid-token-req))))
  (t/is (= 200 (:status (login-req))))
  (t/is (= 200 (:status (login-normal-user-req))))
  (t/is (= 200 (:status (admin-req))))
  (t/is (= 200 (:status (valid-token-req)))))

(t/deftest auth-errors
  (t/is (= 401 (:status (invalid-token-req))))
  (t/is (= 401 (:status (invalid-login-req))))
  (t/is (= 401 (:status (denial-by-role-req)))))

(use-fixtures :each system-fixture)
