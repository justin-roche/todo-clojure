(ns app.test-data
  (:require
   [buddy.hashers :as buddy-hashers]
   [clojure.tools.reader]))

(def test-users [{:name "B" :password (buddy-hashers/encrypt "koira12")
                  :age 19
                  :roles    ["admin" "user"]}
                 {:name "A" :password (buddy-hashers/encrypt "kissa13")
                  :age 19
                  :roles    ["user"]}])

(def auth-headers {:headers {"Authorization" "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6IjYxY2NiMDEzMzliYTZkNzhlMDAyNWRmMiIsInJvbGVzIjpbInVzZXIiXSwiZXhwIjoxNjQxNTgxNTcyfQ.yrf1aVF5P_G-i6-QVudZpBAwF5bJMPUZ0bylriDd1rHhcClYFjCFv36EEgqtJaA6GbCrlFSjNPB15QcyThAv4w"}})

(def invalid-auth-headers {:headers {"Authorization" "this is an invalid header"}})

(def non-admin-auth-headers {:headers {"Authorization" "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6Miwicm9sZXMiOlsidXNlciJdLCJleHAiOjE2NDA0MDcxMDF9.hElFSsbzZN_Q0hdvUXFsTsWHEhCTr75SBwiMBKQTTEujXIN91cfTQXk2nA2o553pZtPw0371H3X85pSj5S_jTg"}})
