(ns hello-world.core
  (:require [hello-world.api :as api]
            [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs.core.async :refer [go <!]]
            [cljs.core.async.interop :refer-macros [<p!]]))

(def app (.querySelector js/document "#app"))

(def results (r/atom [{:name "Sherlock" :id 335}
                      {:name "Breaking Bad" :id 169 :seasons [{:number 1 :summary "Season 1"} {:number 2 :summary "Season 2"}]}]))
(def query (r/atom ""))

(defn search-form []
  (let [handle-search (fn [event]
                        (.preventDefault event)
                        (go (reset! results (into [] (map :show (<! (api/tvmaze-search @query)))))))]
    [:form {:on-submit handle-search}
     [:input {:type "text"
              :id "search-input"
              :value @query
              :on-change #(reset! query (-> % .-target .-value))}]
     [:button {:type "submit"} "Search"]]))

(defn search-result-seasons [seasons]
  (for [season seasons]
    (let [season-number (get-in season [:number])
          summary (get-in season [:summary])]
      [:div
       [:strong season-number]
       [:span summary]])))

(defn search-result [{show-name :name id :id image :image seasons :seasons}]
  (let [image-url (get-in image [:medium])
        handle-load-seasons (fn [event]
                              (.preventDefault event)
                              (let [show-index (.findIndex (clj->js @results) (fn [show] (= (aget show "id") id)))]
                                (.log js/console "showindex is" show-index)
                                (and (> show-index -1) (go (swap! results assoc-in [show-index :seasons] (<! (api/tvmaze-show-seasons id)))))))]
    [:div
     (and image-url [:img {:src image-url :style {:height "5rem"}}])
     [:span show-name]
     (and (empty? seasons) [:button {:on-click handle-load-seasons} "Load Seasons"])
     (or (empty? seasons) (search-result-seasons seasons))]))

(defn search-results [shows]
  [:div
   (for [show shows]
     [search-result show])])

(defn show-item [{show-name :name id :id date-ended :date-ended}]
  [:div
   [:p show-name]
   [:p date-ended]])

(defn my-shows [shows]
  [:div
   (for [show shows]
     [show-item show])])

(defn couch-potato []
  [:div
   [:h1 "Couch Potato"]
   [:h2 "Search"]
   [search-form]
   [search-results @results]
   [:h2 "My Shows"]
   [my-shows [{:name "Sherlock" :id 335 :date-ended "2017-01-01"}
              {:name "Breaking Bad" :id 169 :date-ended "2013-01-01"}]]])

(defn main []
  (rdom/render
    [couch-potato]
    (.querySelector js/document "#app")))

(main)

(go (.log js/console (<! (api/tvmaze-search "sherlock"))))
(go (.log js/console (<! (api/tvmaze-show-details 335))))
(go (.log js/console (<! (api/tvmaze-show-seasons 36795))))
