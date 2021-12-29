(ns app.user
  (:require
   [clojure.test :as t]
   [mount.core :as mount :refer [defstate start stop]]
   [taoensso.timbre :as log]))

(defn refresh-and-run-tests []
  (stop)
  (t/run-tests 'app.test-utils)
  (t/run-tests 'app.user-tests)
  (t/run-tests 'app.auth-tests))

;; (println "evaled user")
