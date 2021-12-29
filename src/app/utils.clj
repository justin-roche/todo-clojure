(ns app.utils
  (:require
   [aprint.core :refer [aprint]]
   [sc.api :as sc :refer [spy letsc defsc ep-info]]
   [mount.core :refer [start defstate stop]]))

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

(defn update-res [ctx v]
  (update-in ctx [:response] #(merge % v)))

(defn update-req [ctx v]
  (update-in ctx [:request] #(merge % v)))
