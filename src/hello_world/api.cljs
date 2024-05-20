(ns hello-world.api
  (:require [cljs.core.async :refer [go <!]]
            [cljs.core.async.interop :refer-macros [<p!]]))

(defn tvmaze-search [query]
  (go
    (let [url (str "http://api.tvmaze.com/search/shows?q=" query)
          result (<p! (js/fetch url))
          json (<p! (.json result))]
      (js->clj json :keywordize-keys true))))

(defn tvmaze-show-details [id]
  (go
    (let [url (str "http://api.tvmaze.com/shows/" id)
          result (<p! (js/fetch url))
          json (<p! (.json result))]
      (js->clj json :keywordize-keys true))))

(defn tvmaze-show-seasons [id]
  (go
    (let [url (str "http://api.tvmaze.com/shows/" id "/seasons")
          result (<p! (js/fetch url))
          json (<p! (.json result))]
      (js->clj json :keywordize-keys true))))

