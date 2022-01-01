(ns app.user
  (:require
   [clojure.test :as t]
   [mount.core :as mount :refer [stop]]))

(defn refresh-and-run-tests []
  (stop)
  (t/run-tests 'app.user-tests)
  (t/run-tests 'app.todo-tests))

