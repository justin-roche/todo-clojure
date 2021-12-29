(ns app.todo-tests
  (:require
   [app.test-data :as test-data :refer [auth-headers]]
   [app.test-utils :as test-utils :refer [post-options get-options system-fixture]]
   [clj-http.client :as client]
   [clojure.test :as t :refer [deftest use-fixtures]]

   [clojure.data.json :as json]
   [clojure.tools.reader]
   [app.utils :as utils]))

(defn create-todo-req [u]
  (let [j (json/write-str u)
        url (str "http://localhost:8890/todo/")
        r (client/post url (merge (post-options j) auth-headers))]
    r))

(defn get-todos-req []
  (let [o (merge (get-options) auth-headers)
        r (client/get "http://localhost:8890/todos" o)]
    r))

(deftest create-todo-requests
  (let [todos (create-todo-req {:name "swim"})]
    (t/is (= 200 (:status todos))))
  (let [todos (get-todos-req)]
    (t/is (= 200 (:status todos)))
    (t/is (= 1 (count (:data (:body todos)))))))

(use-fixtures :each system-fixture)
