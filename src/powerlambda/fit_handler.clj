(ns powerlambda.fit-handler
  (:require [clojure.tools.logging :as log]
            [powerlambda.garmin.fit.decode :refer [decode]]
            [powerlambda.garmin.fit.util.file-utils :refer [is-stream?]]))

(defn ok-response
  [{:keys [filename size]}]
  {:name filename
   :size size})


(defn valid-file?
  [{:keys [filename size tempfile]}]
  (log/info (str "filename: " filename " size: " size " tempfile: " tempfile))
  (if (not (is-stream? tempfile))
    (do
      (prn "Input Stream is nil")
      false)
    true))

(defn decode-handler
  [file]
  (log/info "fit decode handler")
  (if (valid-file? file)
    (do
      (log/info "Decode file")
      (decode (:tempfile file))
      (ok-response file))))
