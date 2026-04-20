(ns powerlambda.handler
  (:require
    [reitit.ring :as ring]
    [reitit.coercion.spec]
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.dev.pretty :as pretty]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.exception :as exception]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.reload :refer [wrap-reload]]
    ;; Uncomment to use
    ; [reitit.ring.middleware.dev :as dev]
    ; [reitit.ring.spec :as spec]
    ; [spec-tools.spell :as spell]
    [muuntaja.core :as m]
    [clojure.spec.alpha :as s]
    [powerlambda.fit-handler :as fit]))

(def file-regex #"^.*\.(fit|csv)$")
(s/def ::file multipart/temp-file-part)
(s/def ::file-name #(re-matches file-regex %))
(s/def ::file-params (s/keys :req-un [::file]))

(s/def ::name string?)
(s/def ::size int?)
(s/def ::error string?)
(s/def ::file-response (s/keys :req-un [::name ::size]))
(s/def ::error-response (s/keys :req-un [::error]))

(def app
  (ring/ring-handler
    (ring/router
      [["/swagger.json"
        {:get {:no-doc  true
               :swagger {:info {:title "PBranes Training Services"}}
               :handler (swagger/create-swagger-handler)}}]

       ["/files"
        {:swagger {:tags ["files"]}}

        ["/upload"
         {:post {:summary    "upload a file"
                 :parameters {:multipart ::file-params}
                 :responses  {200 {:body ::file-response}
                              400 {:body ::error-response}}
                 :handler    (fn [{{{:keys [file]} :multipart} :parameters}]
                               (if (s/valid? ::file-name (:filename file))
                                 {:status 200
                                  :body   (fit/decode-handler file)}
                                 {:status 400
                                  :body   {:error "has to be fit or csv"}}))}}]]]

      {;;:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
       ;;:validate spec/validate ;; enable spec validation for route data
       ;;:reitit.spec/wrap spell/closed ;; strict top-level validation
       :exception pretty/exception
       :data      {:coercion   reitit.coercion.spec/coercion
                   :muuntaja   m/instance
                   :middleware [;; swagger feature
                                swagger/swagger-feature
                                ;; re-load
                                [wrap-reload]
                                ;; cors
                                [wrap-cors :access-control-allow-origin [#"http://localhost:9500"]
                                 :access-control-allow-methods [:get :put :post :delete]]
                                ;; query-params & form-params
                                parameters/parameters-middleware
                                ;; content-negotiation
                                muuntaja/format-negotiate-middleware
                                ;; encoding response body
                                muuntaja/format-response-middleware
                                ;; exception handling
                                exception/exception-middleware
                                ;; decoding request body
                                muuntaja/format-request-middleware
                                ;; coercing response body's
                                coercion/coerce-response-middleware
                                ;; coercing request parameters
                                coercion/coerce-request-middleware
                                ;; multipart
                                multipart/multipart-middleware]}})

    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path   "/"
         :config {:validatorUrl     nil
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))))
