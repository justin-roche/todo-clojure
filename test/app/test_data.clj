(ns app.test-data
  (:require
   [buddy.hashers :as buddy-hashers]
   [clojure.tools.reader]))

(def test-users [{:name "B" :password (buddy-hashers/encrypt "koira12")}

                 {:name "A" :password (buddy-hashers/encrypt "kissa13")}])




