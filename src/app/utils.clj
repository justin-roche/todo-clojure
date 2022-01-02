(ns app.utils)

(defn update-res [ctx v]
  (update-in ctx [:response] #(merge % v)))

(defn update-req [ctx v]
  (update-in ctx [:request] #(merge % v)))

