(ns app.user
  (:require
   [clojure.test :as t]
   [aprint.core :refer [aprint]]
   [mount.core :as mount :refer [stop start]]))

(defn refresh-and-run-tests []
  (stop)
  (t/run-tests 'app.user-tests)
  (t/run-tests 'app.todo-tests))

(defn with-mount [test]
  (start)
  (test)
  (stop))

(defn log-through
  ([v]
   (aprint v) v)
  ([m v]
   (println m)
   (aprint v) v))

(defn xlog-through
  ([v]
   v)
  ([m v]
   v))
