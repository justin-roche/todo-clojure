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

(defn me-req []
  (let [o (merge (get-options) (test-utils/create-auth-header "A"))
        r (client/get "http://localhost:8890/me" o)]
    r))

(defn invalid-token-req []
  (let [o  (merge (get-options) invalid-auth-headers)
        r (client/get "http://localhost:8890/me" o)]
    r))

(defn login-req []
  (let [j (json/write-str {:username "A" :password "kissa13"})
        r (client/post "http://localhost:8890/login" (post-options j))]
    r))

(defn invalid-login-req []
  (let [j (json/write-str {:username "x" :password "kissa13"})
        r (client/post "http://localhost:8890/login" (post-options j))]
    r))

;; (t/deftest auth-header
;;   (test-utils/create-auth-header "A"))

(t/deftest auth
  (t/testing "/me endpoint returns user data (token matches user)"
    (let [r (me-req)
          s (:status r)
          u (:data (:body r))]
      (t/is (= 200 s))
      (t/is (= "A" (:name u))))

    (t/is (= 200 (:status (login-req))))))

;; (t/deftest auth-errors
;;   (t/is (= 401 (:status (invalid-token-req))))
;;   (t/is (= 401 (:status (invalid-login-req)))))

(use-fixtures :each system-fixture)
