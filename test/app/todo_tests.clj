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

(defn delete-todo-req [id username]
  (let [a (test-utils/create-auth-header username)
        url (str "http://localhost:8890/todo/" id)
        r (client/delete url (merge (get-options) a))]
    r))

(defn update-todo-req [id data username]
  (let [j (json/write-str data)
        a (test-utils/create-auth-header username)
        url (str "http://localhost:8890/todo/" id)
        r (client/post url (merge (post-options j) a))]
    r))

(deftest get-todo-requests
  (t/testing "get todos returns an empty array if there are no todos"
    (let [todos (get-todos-req "A")]
      (t/is (= 200 (:status todos)))
      (t/is (= 0 (count (:data (:body todos))))))))

(deftest deleted-todo-requests
  (t/testing "get-todo does not show deleted todos"
    (let [b (create-todo-req {:name "swim"} "B")
          a (create-todo-req {:name "swim" :visibility "deleted"} "A")
          a2 (create-todo-req {:name "run"} "A")]
      (t/is (= 200 (:status b)))
      (t/is (= 200 (:status a)))
      (t/is (= 200 (:status a2))))
    (let [todos (get-todos-req "A")]
      (t/is (= 200 (:status todos)))
      (t/is (= 1 (count (:data (:body todos)))))
      (t/is (= "run" (:name (first (:data (:body todos)))))))))

(deftest create-todo-requests
  (t/testing "creates todos for a user"
    (let [todos (create-todo-req {:name "swim"} "A")]
      (t/is (= 200 (:status todos))))
    (let [todos (get-todos-req "A")]
      (t/is (= 200 (:status todos)))
      (t/is (= 1 (count (:data (:body todos))))))))

(deftest create-todo-requests-for-users
  (t/testing "get todos returns correct todos for different users"
    (let [b (create-todo-req {:name "swim"} "B")
          a (create-todo-req {:name "swim"} "A")
          a2 (create-todo-req {:name "run"} "A")]
      (t/is (= 200 (:status b)))
      (t/is (= 200 (:status a)))
      (t/is (= 200 (:status a2))))
    (let [todos (get-todos-req "A")]
      (t/is (= 200 (:status todos)))
      (t/is (= 2 (count (:data (:body todos))))))
    (let [todos (get-todos-req "B")]
      (t/is (= 200 (:status todos)))
      (t/is (= 1 (count (:data (:body todos))))))))

(deftest delete-todo
  (t/testing "deletes todos for a user"
    (let [t1 (create-todo-req {:name "swim"} "A")
          t2 (create-todo-req {:name "read"} "A")]
      (t/is (= 200 (:status t1))))
    (let [todos (get-todos-req "A")]
      (t/is (= 200 (:status todos)))
      (t/is (= 2 (count (:data (:body todos)))))
      (let [id (:id (first (:data (:body todos))))]
        (let [todos (delete-todo-req id "A")]
          (t/is (= 200 (:status todos)))))
      (let [todos (get-todos-req "A")]
        (t/is (= 200 (:status todos)))
        (t/is (= 1 (count (:data (:body todos)))))
        (t/is (= "read" (:name (first (:data (:body todos))))))))))

(deftest update-todo-status
  (t/testing "deletes todos for a user"
    (let [t1 (create-todo-req {:name "swim"} "A")
          t2 (create-todo-req {:name "read"} "A")]
      (t/is (= 200 (:status t1))))
    (let [todos (get-todos-req "A")]
      (t/is (= 200 (:status todos)))
      (t/is (= 2 (count (:data (:body todos)))))
      (let [id (:id (first (:data (:body todos))))]
        (let [todos (update-todo-req id {:status "completed"} "A")]
          (t/is (= 200 (:status todos)))))
      (let [todos (get-todos-req "A")]
        (t/is (= 200 (:status todos)))
        (t/is (= 2 (count (:data (:body todos)))))
        (t/is (= "swim" (:name (first (:data (:body todos))))))
        (t/is (= "completed" (:status (first (:data (:body todos))))))))))

(use-fixtures :each system-fixture)
