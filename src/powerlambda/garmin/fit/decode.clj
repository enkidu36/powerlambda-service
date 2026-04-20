(ns powerlambda.garmin.fit.decode
  (:require  [clojure.tools.logging :as log]
             [powerlambda.garmin.fit.sdk :as sdk]))

(defn decode [file]
  (log/info (str "Decode fit file. SDK - " sdk/version)))
