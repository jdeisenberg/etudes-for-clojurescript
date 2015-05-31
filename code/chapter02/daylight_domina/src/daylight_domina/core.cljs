; Given an ordinal date, calculate minutes of daylight.
; Uses domina for DOM manipulation.

(ns daylight_domina.core
  (:require [clojure.browser.repl :as repl]
            [domina]
            [domina.events :as events]))
            
(enable-console-print!)

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(defn radians
  "Convert degrees to radians"
  [degrees]
  (* (/ (.-PI js/Math) 180) degrees))

(defn daylight
  "Find minutes of daylight given latitude in degrees and day of year.
  Formula from http://mathforum.org/library/drmath/view/56478.html"
  [lat-degrees day]
  (let [lat (radians lat-degrees)
        part1 (* 0.9671396 (.tan js/Math (* 0.00860 (- day 186))))
        part2 (.cos js/Math (+ 0.2163108 (* 2 (.atan js/Math part1))))
        p (.asin js/Math (* 0.39795 part2))
        numerator (+ (.sin js/Math 0.01454) (* (.sin js/Math lat) (.sin js/Math p)))
        denominator (* (.cos js/Math lat) (.cos js/Math p))]
    (* 60 (- 24 (* 7.63944 (.acos js/Math (/ numerator denominator)))))))

(defn get-float-value
  "Get the floating point value of a field"
  [field]
  (.parseFloat js/window (domina/value (domina/by-id field))))

(defn calculate [evt]
  (let [lat-d (get-float-value "latitude")
        julian (get-float-value "julian")
        minutes (daylight lat-d julian)]
    (domina/set-text! (domina/by-id "result") minutes)))
    
(events/listen! (domina/by-id "calculate") :click calculate)
