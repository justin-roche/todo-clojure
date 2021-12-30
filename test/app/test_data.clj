(ns app.test-data
  (:require
   [buddy.hashers :as buddy-hashers]
   [clojure.tools.reader]))

(def test-users [{:name "B" :password (buddy-hashers/encrypt "koira12")}

                 {:name "A" :password (buddy-hashers/encrypt "kissa13")}])

(def auth-headers {:headers {"Authorization" "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VybmFtZSI6IkEiLCJleHAiOjE2NDE1ODUzNTV9.xLOVSdVWONuvX_oeCKAFlbIzvA-aUwCSZKvO9AJzESenKAJrvfG6kg1fJJjZ38EitOXxhRExvlgX0uONBMwi-Q"}})

(def invalid-auth-headers {:headers {"Authorization" "this is an invalid header"}})

(def non-admin-auth-headers {:headers {"Authorization" "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6Miwicm9sZXMiOlsidXNlciJdLCJleHAiOjE2NDA0MDcxMDF9.hElFSsbzZN_Q0hdvUXFsTsWHEhCTr75SBwiMBKQTTEujXIN91cfTQXk2nA2o553pZtPw0371H3X85pSj5S_jTg"}})
