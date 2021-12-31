(ns app.todo-tests
  (:require
   [app.test-data :as test-data]
   [app.test-utils :as test-utils :refer [post-options get-options system-fixture]]
   [clj-http.client :as client]
   [clojure.test :as t :refer [deftest use-fixtures]]

   [clojure.data.json :as json]
   [clojure.tools.reader]
   [app.utils :as utils]))

(defn create-todo-req [data name]
  (let [j (json/write-str data)
        a (test-utils/create-auth-header name)
        url (str "http://localhost:8890/todo/")
        r (client/post url (merge (post-options j) a))]
    r))

(defn get-todos-req [name]
  (let [o (get-options)
        a (test-utils/create-auth-header name)
        r (client/get "http://localhost:8890/todos" (merge o a))]
    r))

(defn delete-todo-req [id name]
  (let [a (test-utils/create-auth-header name)
        url (str "http://localhost:8890/todo/" id)
        r (client/delete url (merge (get-options) a))]
    r))

(defn update-todo-req [id data name]
  (let [j (json/write-str data)
        a (test-utils/create-auth-header name)
        url (str "http://localhost:8890/todo/" id "/status")
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
      (let [todos (get-todos-req "A")
            t1 (first (:data (:body todos)))]
        (t/is (= 200 (:status todos)))
        (t/is (= 1 (count (:data (:body todos)))))
        (t/is (= "read" (:name t1)))))))

(deftest change-todo-status
  (t/testing "changes todo status to complete"
    (let [t1 (create-todo-req {:name "swim"} "A")
          t2 (create-todo-req {:name "read"} "A")]
      (t/is (= 200 (:status t1))))
    (let [todos (get-todos-req "A")]
      (t/is (= 200 (:status todos)))
      (t/is (= 2 (count (:data (:body todos)))))
      (let [id (:id (first (:data (:body todos))))]
        (let [todos (update-todo-req id {} "A")]
          (t/is (= 200 (:status todos)))))
      (let [todos (get-todos-req "A")]
        (t/is (= 200 (:status todos)))
        (t/is (= 2 (count (:data (:body todos)))))
        (t/is (= "swim" (:name (first (:data (:body todos))))))
        (t/is (string? (:completedAt (first (:data (:body todos))))))
        (t/is (= "complete" (:status (first (:data (:body todos))))))))))

(deftest change-todo-status
  (t/testing "changes todo status to incomplete"
    (let [t1 (create-todo-req {:name "swim"} "A")
          t2 (create-todo-req {:name "read"} "A")]
      (t/is (= 200 (:status t1))))
    (let [todos (get-todos-req "A")]
      (let [id (:id (first (:data (:body todos))))]
        (let [todos (update-todo-req id {} "A")]
          (t/is (= 200 (:status todos)))))
      (let [todos (get-todos-req "A")]
        (t/is (= 200 (:status todos)))
        (t/is (= "complete" (:status (first (:data (:body todos))))))
        (let [id (:id (first (:data (:body todos))))]
          (let [todos (update-todo-req id {} "A")]
            (t/is (= 200 (:status todos))))))
      (let [todos (get-todos-req "A")]
        (t/is (= 200 (:status todos)))
        (t/is (= nil (:completedAt (first (:data (:body todos))))))
        (t/is (= "incomplete" (:status (first (:data (:body todos))))))))))

(use-fixtures :each system-fixture)
