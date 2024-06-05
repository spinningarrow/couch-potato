(ns hello-world.core
  (:require [hello-world.api :as api]
            [hello-world.state :as state]
            [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs.core.async :refer [go <!]]
            [cljs.core.async.interop :refer-macros [<p!]]))

(defn nav []
  [:nav {:class "main-nav"}
   [:ol
     [:li
      [:a {:href "#watchlist"} "Watchlist"]]
     [:li
      [:a {:href "#search"} "Search"]]]])

(defn search-form []
  (let [handle-search (fn [event]
                        (.preventDefault event)
                        (go (reset! state/results (into [] (map :show (<! (api/tvmaze-search @state/query)))))))]
    [:form {:on-submit handle-search}
     [:input {:type "text"
              :id "search-input"
              :value @state/query
              :on-change #(reset! state/query (-> % .-target .-value))}]
     [:button {:type "submit"} "Search"]]))

(defn search-result [{show-name :name id :id image :image seasons :seasons}]
  (let [image-url (get-in image [:medium])
        handle-load-seasons (fn [event]
                              (.preventDefault event)
                              (let [show-index (.findIndex (clj->js @state/results) (fn [show] (= (aget show "id") id)))]
                                (.log js/console "showindex is" show-index)
                                (and (> show-index -1) (go (swap! state/results assoc-in [show-index :seasons] (<! (api/tvmaze-show-seasons id)))))))
        watchlist-show {:id id :name show-name :image image-url :date-ended (.toISOString (js/Date.))}
        handle-add-show (fn [event]
                          (.preventDefault event)
                          (.log js/console "add show" (clj->js watchlist-show))
                          (swap! state/watchlist conj watchlist-show))]
    [:li
     (and image-url [:img {:src image-url :style {:height "5rem"}}])
     [:span show-name]
     [:button {:on-click handle-add-show} "Add Show"]]))

(defn search-results [shows]
  [:ol {:class "search-results"}
   (for [show shows]
     [search-result show])])

(defn show-item [{show-name :name id :id date-ended :date-ended}]
  [:div
   [:p show-name]
   [:p date-ended]])

(defn my-shows [shows]
  [:div
   (for [show shows]
     [show-item {:key (show :id)} show])])

(defn couch-potato []
  [:div
   [:h1 "Couch Potato"]
   [nav]
   [:div {:id "search" :class "page"}
     [:h2 "Search"]
     [search-form]
     [search-results @state/results]]
   [:div {:id "watchlist" :class "page"}
     [:h2 "My Shows"]
     [my-shows @state/watchlist]]])

(defn main []
  (rdom/render
    [couch-potato]
    (.querySelector js/document "#app")))

(main)

(go (.log js/console (<! (api/tvmaze-search "sherlock"))))
(go (.log js/console (<! (api/tvmaze-show-details 335))))
(go (.log js/console (<! (api/tvmaze-show-seasons 36795))))
