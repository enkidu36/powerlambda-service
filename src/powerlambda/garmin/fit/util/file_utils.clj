(ns powerlambda.garmin.fit.util.file-utils
  (:require [clojure.tools.logging :as log])
  (:import (java.io FileInputStream)))

(defn make-input-stream
  "Make new FileInputStream from either
  file name or File object"
  [file-name]
  (try
    (log/debug "make-input-stream")
    (FileInputStream. file-name)
    (catch Exception _
      (throw (RuntimeException. (format "Error opening file %s" file-name))))))

(defn close-input-stream
  [in]
  (log/debug "close-input-stream")
  (try
    (.close in)
    (catch Exception e
      (throw (RuntimeException. ^Throwable e)))))

(defn is-stream?
  "Check file can be opened. If so close and return true"
  [file]
  (try
    (if-let [in (make-input-stream file)]
      (do
        (close-input-stream in)
        true))
    (catch RuntimeException _
      (log/info "Failed to validate stream."))))
