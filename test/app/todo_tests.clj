(ns app.todo-tests
  (:require
   [app.test-data :as test-data :refer [auth-headers]]
   [app.test-utils :as test-utils :refer [post-options get-options system-fixture]]
   [clj-http.client :as client]
   [clojure.test :as t :refer [deftest use-fixtures]]

   [clojure.data.json :as json]
   [clojure.tools.reader]
   [app.utils :as utils]))

(defn create-todo-req [data username]
  (let [j (json/write-str data)
        a (test-utils/create-auth-header username)
        url (str "http://localhost:8890/todo/")
        r (client/post url (merge (post-options j) a))]
    r))

(defn get-todos-req [username]
  (let [o (get-options)
        a (test-utils/create-auth-header username)
        r (client/get "http://localhost:8890/todos" (merge o a))]
    r))

(deftest create-todo-requests
  (let [todos (create-todo-req {:name "swim"} "A")]
    (t/is (= 200 (:status todos))))
  (let [todos (get-todos-req "A")]
    (t/is (= 200 (:status todos)))
    (t/is (= 1 (count (:data (:body todos)))))))

(deftest create-todo-requests
  (let [b (create-todo-req {:name "swim"} "B")
        a (create-todo-req {:name "swim"} "A")]
    (t/is (= 200 (:status b)))
    (t/is (= 200 (:status a))))
  (let [todos (get-todos-req "A")]
    (t/is (= 200 (:status todos)))
    (t/is (= 1 (count (:data (:body todos))))))
  (let [todos (get-todos-req "B")]
    (t/is (= 200 (:status todos)))
    (t/is (= 1 (count (:data (:body todos)))))))

(use-fixtures :each system-fixture)
