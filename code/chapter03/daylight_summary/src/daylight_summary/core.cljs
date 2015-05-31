(ns daylight_summary.core
  (:require [clojure.browser.repl :as repl]
            [domina :as dom]
            [domina.css :as css]
            [enfocus.core :as ef]
            [enfocus.events :as ev]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

(defn radians
  "Convert degrees to radians"
  [degrees]
  (* (/ (.-PI js/Math) 180) degrees))

(defn daylight
  "Find minutes of daylight given day of year and latitude in degrees.
  Formula from http://mathforum.org/library/drmath/view/56478.html"
  [lat-degrees day]
  (let [lat (radians lat-degrees)
        part1 (* 0.9671396 (.tan js/Math (* 0.00860 (- day 186))))
        part2 (.cos js/Math (+ 0.2163108 (* 2 (.atan js/Math part1))))
        p (.asin js/Math (* 0.39795 part2))
        numerator (+ (.sin js/Math 0.01454) (* (.sin js/Math lat) (.sin js/Math p)))
        denominator (* (.cos js/Math lat) (.cos js/Math p))]
    (* 60 (- 24 (* 7.63944 (.acos js/Math (/ numerator denominator)))))))

(defn make-ranges
  "Return vector of begin-end ordinal dates for a list of days per month"
  [mlist]
  (reduce (fn [acc x] (conj acc (+ x (last acc)))) [1] (rest mlist)))

(def month-ranges
  "Days per month for non-leap years"
  (make-ranges '(0 31 28 31 30 31 30 31 31 30 31 30 31)))

(defn to-hours-minutes
  "Convert minutes to hours and minutes"
  [m]
  (str (quot m 60) "h "  (.toFixed (mod m 60) 0) "m"))

(defn get-value
  "Get the value from a field"
  [field]
  (ef/from field (ef/get-prop :value)))

(defn mean
  "Compute mean of a sequence of numbers."
  [x]
  (/ (apply + x) (count x)))

(defn mean-daylight
  "Get mean daylight for a range of days"
  [start finish latitude]
  (let [f (fn [x] (daylight latitude x))]
    (mean (map f (range start finish)))))
  
(defn generate-averages
  "Generate monthly averages for a given latitude"
  [latitude]
  (loop [ranges month-ranges
         result []]
    (if (< (count ranges) 2)
        result
        (recur (rest ranges) (conj result (mean-daylight (first ranges) (second ranges) latitude))))))
        
(defn calculate [evt]
  (let [fromMenu (first (ef/from "input[name='locationType']" (ef/get-prop :checked)))
        lat-d (if fromMenu (.parseFloat js/window (get-value "#cityMenu"))
                          (.parseFloat js/window (get-value "#latitude")))
        averages (generate-averages lat-d)]
    (doall (map-indexed
             (fn [n item] (ef/at (str "#m" (inc n)) (ef/content (to-hours-minutes item))))
             averages))))

(ef/at "#calculate" (ev/listen :click calculate))
