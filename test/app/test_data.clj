(ns app.test-data
  (:require
   [buddy.hashers :as buddy-hashers]
   [clojure.tools.reader]))

(def test-usernames ["a@gmail.com" "b@yahoo.com"])
(def test-users [{:name (first test-usernames) :password (buddy-hashers/encrypt "koira12")}

                 {:name (second test-usernames) :password (buddy-hashers/encrypt "kissa13")}])




