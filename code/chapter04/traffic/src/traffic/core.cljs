(ns traffic.core
  (:require-macros [hiccups.core :as hiccups])
  (:require [cljs.nodejs :as nodejs]
            [clojure.string :as str]
            [cljs-made-easy.line-seq :as cme]
            [hiccups.runtime :as hiccupsrt]
            [traffic.crosstab :as ct]))

(nodejs/enable-util-print!)

(def express (nodejs/require "express"))

(def filesystem (js/require "fs")) ;;require nodejs lib

;; These keywords are the "column headers" from the spreadsheet.
;; An entry of nil means that I am ignoring that column.
(def headers [:date :time nil :accident :injury :property-damage :fatal nil
              :vehicle :year :make :model :color :type nil :race :gender :driver-state nil])

(defn zipmap-omit-nil
  "Does the same as zipmap, except when there's a nil in the
  first vector, it doesn't put anything into the map.
  I wrote it this way just to prove to myself that I could do it.
  It's easier to just say (dissoc (zipmap a-vec b-vec) nil)"
  [a-vec b-vec]
  (loop [result {}
          a a-vec
          b b-vec]
    (if (or (empty? a) (empty? b))
      result
      (recur (if-not (nil? (first a))
               (assoc result (first a) (first b))
               result)
             (rest a) (rest b)))))

(defn add-row
  "Convenience function that adds a row from the CSV file
  to the data map."
  [line]
  (zipmap-omit-nil headers (str/split line #"\t")))

(defn create-data-structure
  "Create a vector of maps from a tab-separated value file
  and a list of header keywords."
  [filename headers]
  (cme/with-open [file-descriptor (.openSync filesystem filename "r")]
             (reduce (fn [result line] (conj result (add-row line))) [] (rest (cme/line-seq file-descriptor)))))

(def traffic (create-data-structure "traffic_july_2014_edited.csv" headers))

(defn day [entry] (.substr (:date entry) 3 2))
(defn hour [entry] (.substr (:time entry) 0 2))

(def field-list [
               ["Choose a field" nil]
               ["Day" day]
               ["Hour" hour]
               ["Accident" :accident]
               ["Injury" :injury]
               ["Property Damage" :property-damage]
               ["Fatal" :fatal]
               ["Vehicle year" :year]
               ["Vehicle Color" :color]
               ["Driver's Race" :race]
               ["Driver's Gender" :gender]
               ["Driver's State" :driver-state]])

(defn traffic-menu
  "Create a <select> menu with the given choice selected"
  [option-list selection]
  (map-indexed (fn [n item]
                 (let [menu-text (first item)]
                   [:option
                    (if (= n selection){:value n :selected "selected"} {:value n})
                    menu-text]))
                 option-list))

(defn field-name [n] (first (get field-list n)))
(defn field-code [n] (last (get field-list n)))

(defn add-table-row
  [row-label counts row-total result]
    (conj result (reduce (fn [acc item] (conj acc [:td item])) [:tr [:th row-label]] (conj counts row-total))))

(defn html-table
  [[row-labels col-labels counts row-totals col-totals grand-total]]
  [:div
   [:table
    (if (not (nil? (first col-labels)))
      [:thead (reduce (fn [acc item] (conj acc [:th item])) [:tr [:th "\u00a0"]]
                      (conj col-labels "Total"))]
      [:thead [:tr [:th "\u00a0"] [:th "Total"]]])
    (if (not (nil? (first col-labels)))
        (vec (loop [rl row-labels
                    freq counts
                    rt row-totals
                    result [:tbody]]
               (if-not (empty? rl)
                 (recur (rest rl) (rest freq) (rest rt)
                        (add-table-row (first rl) (first freq) (first rt) result))
                 (add-table-row "Total" col-totals grand-total result))))
        (vec (loop [rl row-labels
                    rt row-totals
                    result [:tbody]]
               (if-not (empty? rl)
                 (recur (rest rl) (rest rt)
                        (conj result [:tr [:th (first rl)] [:td (first rt)]]))
                 (conj result [:tr [:th "Total"] [:td grand-total]])))))]
   ])

(defn show-table
  [row-spec col-spec]
  (cond
    (and (not= 0 row-spec) (not= 0 col-spec))
      [:div [:h2 (str (field-name row-spec) " vs. " (field-name col-spec))]
      (html-table (ct/cross-tab traffic (field-code row-spec) (field-code col-spec)))]
    (not= 0 row-spec)
      [:div [:h2 (field-name row-spec)]
       (html-table (ct/cross-tab traffic (field-code row-spec) nil))]
    :else
      nil))

(defn generate-page! [request response]
  (let [query (.-query request)
        col-spec (if query (js/parseInt (.-column query)) nil)
        row-spec (if query (js/parseInt (.-row query)) nil)]
    (.send response
           (hiccups/html
             [:html
              [:head
               [:title "Traffic Violations"]
               [:meta {:http-equiv "Content-type"
                       :content "text/html; charset=utf-8"}]
               [:link {:rel "stylesheet" :type "text/css" :href "style.css"}]]
              [:body
               [:h1 "Traffic Violations"]
               [:form {:action "http://localhost:3000"
                       :method "get"}
                "Row: "
                [:select {:name "row"}
                 (traffic-menu field-list row-spec)]
                "Column: "[:select {:name "column"}
                 (traffic-menu field-list col-spec)]
                [:input {:type "submit" :value "Calculate"}]]
               (show-table row-spec col-spec)
               [:hr]
               [:p "Source data: "
                [:a {:href "http://catalog.data.gov/dataset/traffic-violations-56dda"}
                 "Montgomery County Traffic Violation Database"]]]]))))

(defn -main []
  (let [app (express)]
    (.use app (.static express "."))
    (.get app "/" generate-page!)
    (.listen app 3000 (fn []
                        (println "Server started on port 3000")))))

(set! *main-cli-fn* -main)
