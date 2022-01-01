(ns app.user-tests
  (:require
   [app.test-utils :as test-utils :refer [post-options get-options system-fixture make-url]]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [clojure.test :as t :refer [deftest use-fixtures]]
   [clojure.tools.reader]
   [app.utils :as utils]))

(defn login-req [data]
  (let [j (json/write-str data)
        url (make-url "login")
        r (client/post url (post-options j))]
    r))

(defn me-req [name]
  (let [o (merge (get-options) (test-utils/create-auth-header name))
        r (client/get (make-url "me") o)]
    r))

(defn token-req [name token]
  (let [h {:headers {"Authorization" token}}
        o (merge (get-options) h)
        r (client/get (make-url "me") o)]
    r))


(t/deftest me-endpoint
  (t/testing "/me endpoint returns user data (demonstrates that token matches user)"
    (let [r (me-req "a@gmail.com")
          s (:status r)
          u (:data (:body r))]
      (t/is (= 200 s))
      (t/is (= "a@gmail.com" (:name u))))))

(deftest login-creates-user
  (t/testing "creates user if user does not exist"
    (let [l (login-req {:name "c@b.com"})
          me (me-req "c@b.com")]
      (t/is (= 200 (:status me)))
      (t/is (= "c@b.com" (:name (:data (:body me))))))))

(deftest login-body-validation
  (t/testing "validates that username is present"
    (let [l (login-req {})]
      (t/is (= 400 (:status l))))))

(deftest login-email-validation
  (t/testing "validates that username is email address"
    (let [l (login-req {:name "john"})]
      (t/is (= 400 (:status l))))))

(deftest new-login-token
  (t/testing "sends valid token for new users"
    (let [l (login-req {:name "c@b.com"})
          t (:token (:body l))
          me (token-req "c@b.com" t)]
      (t/is string? (:token (:body l)))
      (t/is (= 200 (:status me))))))

(deftest existing-login-token
  (t/testing "sends valid token for existing users"
    (let [res (login-req {:name "a@gmail.com"})
          token (:token (:body res))
          me-res (token-req "a@gmail.com" token)]
      (t/is string? token)
      (t/is (= 200 (:status me-res))))))

(use-fixtures :each system-fixture)
