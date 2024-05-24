(ns hello-world.state
  (:require [reagent.core :as r]))

(def results (r/atom [{:name "Sherlock" :id 335}
                      {:name "Breaking Bad" :id 169 :seasons [{:number 1 :summary "Season 1"} {:number 2 :summary "Season 2"}]}]))
(def query (r/atom ""))

