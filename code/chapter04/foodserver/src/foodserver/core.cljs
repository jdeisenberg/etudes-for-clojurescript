(ns foodserver.core
  (:require-macros [hiccups.core :as hiccups])
  (:require [cljs.nodejs :as nodejs]
            [hiccups.runtime :as hiccupsrt]
            [foodserver.mapmaker :as mapmaker]
            [clojure.string :as str]))

(nodejs/enable-util-print!)

(def express (nodejs/require "express"))

(def foodmap (mapmaker/foodmap "food.xml"))

(defn case-insensitive [a b]
  (compare (str/upper-case a) (str/upper-case b)))

(defn condiment-menu
  "Create HTML menu with the given selection
  as the 'selected' item"
  [selection]
  (map (fn [item] [:option
                   (if (= item selection){:value item :selected "selected"} {:value item})
                   item])
       (sort case-insensitive (keys foodmap))))

(defn compatible-foods
  "Create unordered list of foods compatible with selected condiment"
  [selection]
  (if selection
    (map (fn [item] [:li item]) (sort case-insensitive (foodmap selection)))
    nil))

(defn generate-page! [request response]
  (let [query (.-query request)
        chosen-condiment (if query (.-condiment query) "")]
    (.send response
           (hiccups/html
             [:html
              [:head
               [:title "Condiment Matcher"]
               [:meta {:http-equiv "Content-type"
                       :content "text/html; charset=utf-8"}]]
              [:body
               [:h1 "Condiment Matcher"]
               [:form {:action "http://localhost:3000"
                       :method "get"}
                [:select {:name "condiment"}
                 [:option {:value ""} "Choose a condiment"]
                 (condiment-menu chosen-condiment)]
                [:input {:type "submit" :value "Find Compatible Foods"}]]
               [:ul (compatible-foods chosen-condiment)]
               [:p "Source data: "
                [:a {:href "http://catalog.data.gov/dataset/mypyramid-food-raw-data-f9ed6"}
                 "MyPyramid Food Raw Data"]
                " from the Food and Nutrition Service of the United States Department of Agriculture."]]]))))

(defn -main []
  (let [app (express)]
    (.get app "/" generate-page!)
    (.listen app 3000 (fn []
                        (println "Server started on port 3000")))))

(set! *main-cli-fn* -main)
