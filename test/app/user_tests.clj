(ns app.user-tests
  (:require
   [app.test-utils :as test-utils :refer [post-options get-options system-fixture]]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [clojure.test :as t :refer [deftest use-fixtures]]
   [clojure.tools.reader]
   [app.utils :as utils]))

(defn login-req [data]
  (let [j (json/write-str data)
        url (str "http://localhost:8890/login")
        r (client/post url (post-options j))]
    r))

(defn me-req [name]
  (let [o (merge (get-options) (test-utils/create-auth-header name))
        r (client/get "http://localhost:8890/me" o)]
    r))

(defn token-req [name token]
  (let [h {:headers {"Authorization" token}}
        o (merge (get-options) h)
        r (client/get "http://localhost:8890/me" o)]
    r))

(t/deftest me-endpoint
  (t/testing "/me endpoint returns user data (token matches user)"
    (let [r (me-req "A")
          s (:status r)
          u (:data (:body r))]
      (t/is (= 200 s))
      (t/is (= "A" (:name u))))))

(deftest login
  (t/testing "creates user if user does not exist"
    (let [l (login-req {:name "c@b.com"})
          me (me-req "c@b.com")]
      (t/is (= 200 (:status me))))))

(deftest new-login-token
  (t/testing "sends valid token for new users"
    (let [l (login-req {:name "c@b.com"})
          t (:token (:body l))
          me (token-req "c@b.com" t)]
      (t/is string? (:token (:body l)))
      (t/is (= 200 (:status me))))))

(deftest existing-login-token
  (t/testing "sends valid token for existing users"
    (let [l (login-req {:name "A"})
          t (:token (:body l))
          me (token-req "A" t)]
      (t/is (= 200 (:status me))))))

(use-fixtures :each system-fixture)
