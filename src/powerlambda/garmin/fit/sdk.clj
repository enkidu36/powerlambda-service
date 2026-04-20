(ns powerlambda.garmin.fit.sdk
  (:import (com.garmin.fit Fit)))

(def version
  "Protocol/Version/Profile type text"
  (format "Protocol %d.%d Profile %.2f %s\n"
          (int Fit/PROTOCOL_VERSION_MAJOR)
          (int Fit/PROFILE_VERSION_MINOR)
          (/ (double Fit/PROFILE_VERSION) 100) Fit/PROFILE_TYPE))

