(ns app.todo-tests
  (:require
   [app.test-data :as test-data]
   [app.test-utils :as test-utils :refer [post-options get-options system-fixture make-url]]
   [clj-http.client :as client]
   [clojure.test :as t :refer [deftest use-fixtures]]
   [clojure.data.json :as json]
   [clojure.tools.reader]
   [app.utils :as utils]))

(defn create-todo-req [data name]
  (let [j (json/write-str data)
        a (test-utils/create-auth-header name)
        url (make-url "todo")
        r (client/post url (merge (post-options j) a))]
    r))

(defn get-todos-req [name]
  (let [o (get-options)
        a (test-utils/create-auth-header name)
        url (make-url "todos")
        r (client/get url (merge o a))]
    r))

(defn delete-todo-req [id name]
  (let [a (test-utils/create-auth-header name)
        url (make-url ["todo" id])
        r (client/delete url (merge (get-options) a))]
    r))

(defn update-todo-req [id data name]
  (let [j (json/write-str data)
        a (test-utils/create-auth-header name)
        url (make-url ["todo" id "status"])
        r (client/post url (merge (post-options j) a))]
    r))

(defn completion-report-req [name]
  (let [j (json/write-str {})
        a (test-utils/create-auth-header name)
        url (make-url ["todos" "completion-report"])
        r (client/get url (merge (post-options j) a))]
    r))

(defn burn-down-report-req [name]
  (let [j (json/write-str {})
        a (test-utils/create-auth-header name)
        url (make-url ["todos" "burn-down-report"])
        r (client/get url (merge (post-options j) a))]
    r))

(deftest get-todo-requests
  (t/testing "get todos returns an empty array if there are no todos"
    (let [todos (get-todos-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (t/is (= 0 (count (:data (:body todos))))))))

(deftest deleted-todo-requests
  (t/testing "get-todo does not show deleted todos"
    (let [b (create-todo-req {:name "swim"} "b@yahoo.com")
          a (create-todo-req {:name "swim" :visibility "deleted"} "a@gmail.com")
          a2 (create-todo-req {:name "run"} "a@gmail.com")]
      (t/is (= 200 (:status b)))
      (t/is (= 200 (:status a)))
      (t/is (= 200 (:status a2))))
    (let [todos (get-todos-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (t/is (= 1 (count (:data (:body todos)))))
      (t/is (= "run" (:name (first (:data (:body todos)))))))))

(deftest create-todo-requests
  (t/testing "creates todos for a user"
    (let [created (create-todo-req {:name "swim"} "a@gmail.com")]
      (t/is (= 200 (:status created)))
      (t/is (= "swim" (:name (:data (:body created))))))
    (let [todos (get-todos-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (t/is (= 1 (count (:data (:body todos))))))))

(deftest invalid-create-todo-requests
  (t/testing "validates todo has a name"
    (let [todos (create-todo-req {} "a@gmail.com")]
      (t/is (= 400 (:status todos))))
    (let [todos (get-todos-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (t/is (= 0 (count (:data (:body todos))))))))

(deftest create-todo-requests-for-users
  (t/testing "get todos returns correct todos for different users"
    (let [b (create-todo-req {:name "swim"} "b@yahoo.com")
          a (create-todo-req {:name "swim"} "a@gmail.com")
          a2 (create-todo-req {:name "run"} "a@gmail.com")]
      (t/is (= 200 (:status b)))
      (t/is (= "swim" (:name (:data (:body b)))))
      (t/is (= 200 (:status a)))
      (t/is (= 200 (:status a2))))
    (let [todos (get-todos-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (t/is (= 2 (count (:data (:body todos))))))
    (let [todos (get-todos-req "b@yahoo.com")]
      (t/is (= 200 (:status todos)))
      (t/is (= 1 (count (:data (:body todos))))))))

(deftest delete-todo
  (t/testing "deletes todos for a user"
    (let [t1 (create-todo-req {:name "swim"} "a@gmail.com")
          t2 (create-todo-req {:name "read"} "a@gmail.com")]
      (t/is (= 200 (:status t1))))
    (let [todos (get-todos-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (t/is (= 2 (count (:data (:body todos)))))
      (let [id (:id (first (:data (:body todos))))]
        (let [deleted (delete-todo-req id "a@gmail.com")]
          (t/is (= 200 (:status deleted)))))
      (let [todos (get-todos-req "a@gmail.com")
            t1 (first (:data (:body todos)))]
        (t/is (= 200 (:status todos)))
        (t/is (= 1 (count (:data (:body todos)))))
        (t/is (= "read" (:name t1)))))))

(deftest invalid-delete-todo
  (t/testing "gives correct response when todo id not found"
    (let [t1 (create-todo-req {:name "swim"} "a@gmail.com")
          t2 (create-todo-req {:name "read"} "a@gmail.com")]
      (t/is (= 200 (:status t1))))
    (let [id "abc"]
      (let [delete-res (delete-todo-req id "a@gmail.com")]
        (t/is (= 409 (:status delete-res)))))))

(deftest change-todo-status
  (t/testing "changes todo status to complete"
    (let [t1 (create-todo-req {:name "swim"} "a@gmail.com")
          t2 (create-todo-req {:name "read"} "a@gmail.com")]
      (t/is (= 200 (:status t1))))
    (let [todos (get-todos-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (t/is (= 2 (count (:data (:body todos)))))
      (let [id (:id (first (:data (:body todos))))]
        (let [updated (update-todo-req id {} "a@gmail.com")]
          (t/is (= 200 (:status updated)))
          (t/is (= "complete" (:status (:data (:body updated)))))))
      (let [todos (get-todos-req "a@gmail.com")]
        (t/is (= 200 (:status todos)))
        (t/is (= 2 (count (:data (:body todos)))))
        (t/is (= "swim" (:name (first (:data (:body todos))))))
        (t/is (string? (:completedAt (first (:data (:body todos))))))
        (t/is (= "complete" (:status (first (:data (:body todos))))))))))

(deftest change-todo-status-to-incomplete
  (t/testing "changes todo status to incomplete"
    (let [t1 (create-todo-req {:name "swim"} "a@gmail.com")
          t2 (create-todo-req {:name "read"} "a@gmail.com")]
      (t/is (= 200 (:status t1))))
    (let [todos (get-todos-req "a@gmail.com")]
      (let [id (:id (first (:data (:body todos))))]
        (let [todos (update-todo-req id {} "a@gmail.com")]
          (t/is (= 200 (:status todos)))))
      (let [todos (get-todos-req "a@gmail.com")]
        (t/is (= 200 (:status todos)))
        (t/is (= "complete" (:status (first (:data (:body todos))))))
        (let [id (:id (first (:data (:body todos))))]
          (let [todos (update-todo-req id {} "a@gmail.com")]
            (t/is (= 200 (:status todos))))))
      (let [todos (get-todos-req "a@gmail.com")]
        (t/is (= 200 (:status todos)))
        (t/is (= nil (:completedAt (first (:data (:body todos))))))
        (t/is (= "incomplete" (:status (first (:data (:body todos))))))))))

(deftest invalid-change-todo-status
  (t/testing "returns correct response for wrong todo id"
    (let [t1 (create-todo-req {:name "swim"} "a@gmail.com")
          t2 (create-todo-req {:name "read"} "a@gmail.com")]
      (t/is (= 200 (:status t1))))
    (let [todos (get-todos-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (t/is (= 2 (count (:data (:body todos)))))
      (let [id "bad"]
        (let [todos (update-todo-req id {} "a@gmail.com")]
          (t/is (= 409 (:status todos))))))))

(deftest empty-completion-report-request
  (t/testing "creates completion report for a user"
    (let [todos (completion-report-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (t/is (map? (:data (:body todos))))
      (t/is (vector? (:complete (:data (:body todos)))))
      (t/is (= 0 (count (:complete (:data (:body todos))))))
      (t/is (= 0 (count (:incomplete (:data (:body todos))))))
      (t/is (vector? (:incomplete (:data (:body todos)))))
      (t/is (vector? (:incomplete (:data (:body todos))))))))

(deftest completion-report-request
  (t/testing "creates completion report for a user"
    (let [t1 (create-todo-req {:name "run"} "a@gmail.com")
          t2 (create-todo-req {:name "swim"} "a@gmail.com")
          t3 (create-todo-req {:name "cook"} "a@gmail.com")
          t4 (create-todo-req {:name "read" :status "complete"} "a@gmail.com")
          t5 (create-todo-req {:name "write" :status "complete"} "a@gmail.com")])
    (let [todos (completion-report-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (t/is (map? (:data (:body todos))))
      (t/is (vector? (:complete (:data (:body todos)))))
      (t/is (= 2 (count (:complete (:data (:body todos))))))
      (t/is (= 3 (count (:incomplete (:data (:body todos))))))
      (t/is (vector? (:incomplete (:data (:body todos)))))
      (t/is (vector? (:incomplete (:data (:body todos))))))))

(deftest empty-burn-down-report-request
  (t/testing "returns empty array of events if no todos exist"
    (let [report-res (burn-down-report-req "a@gmail.com")
          report (:data (:body report-res))]
      (t/is (= 200 (:status report-res)))
      (t/is (= 0 (count report))))))

(deftest burn-down-report-request
  (t/testing "creates burn down report for a user"
    (let [t1 (create-todo-req {:name "run"} "a@gmail.com")
          t2 (create-todo-req {:name "swim"} "a@gmail.com")
          t3 (create-todo-req {:name "cook"} "a@gmail.com")
          t4 (create-todo-req {:name "read"} "a@gmail.com")
          t5 (create-todo-req {:name "write"} "a@gmail.com")])
    (let [todos (get-todos-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (let [id (:id (first (:data (:body todos))))]
        (let [todos (update-todo-req id {} "a@gmail.com")]
          (t/is (= 200 (:status todos))))
        (let [report-res (burn-down-report-req "a@gmail.com")
              report (:data (:body report-res))]
          (t/is (= 200 (:status report-res)))
          (t/is (= 6 (count report)))
          (t/is (= "completion" (:type (last report)))))))))

(deftest burn-down-report-request-deletions
  (t/testing "burn down report includes deletions"
    (let [t1 (create-todo-req {:name "run"} "a@gmail.com")
          t2 (create-todo-req {:name "swim"} "a@gmail.com")
          t3 (create-todo-req {:name "cook"} "a@gmail.com")
          t4 (create-todo-req {:name "read"} "a@gmail.com")
          t5 (create-todo-req {:name "write"} "a@gmail.com")])
    (let [todos (get-todos-req "a@gmail.com")]
      (t/is (= 200 (:status todos)))
      (let [id1 (:id (first (:data (:body todos))))
            id2 (:id (second (:data (:body todos))))]
        (let [todos (update-todo-req id1 {} "a@gmail.com")]
          (t/is (= 200 (:status todos))))
        (let [todos (delete-todo-req id2 "a@gmail.com")]
          (t/is (= 200 (:status todos))))
        (let [report-res (burn-down-report-req "a@gmail.com")
              report (:data (:body report-res))]
          (t/is (= 200 (:status report-res)))
          (t/is (= 7 (count report)))
          (t/is (= "deletion" (:type (last report)))))))))

(use-fixtures :each system-fixture)
