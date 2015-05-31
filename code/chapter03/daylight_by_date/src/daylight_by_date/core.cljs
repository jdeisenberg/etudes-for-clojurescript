; Given a Gregorian date, calculate minutes of
; daylight. Uses domina for DOM manipulation.

(ns daylight_by_date.core
  (:require [clojure.browser.repl :as repl]
            [clojure.string :as str]
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

(defn leap-year?
  "Return true if given year is a leap year; false otherwise"
  [year]
  (or (and (= 0 (rem year 4)) (not= 0 (rem year 100)))
    (= 0 (rem year 400))))

(defn ordinal-day
  "Compute ordinal day given Gregorian day, month, and year"
  [day month year]
  (let [feb-days (if (leap-year? year) 29 28)
        days-per-month [0 31 feb-days 31 30 31 30 31 31 30 31 30 31]
        month-ok (and (> month 0) (< month 13))
        day-ok (and month-ok (> day 0) (<= day (+ (nth days-per-month month))))
        subtotal (reduce + (take month days-per-month))]
    (if day-ok (+ subtotal day) 0)))

(defn to-julian
  "Convert Gregorian date to Julian"
  []
  (let [greg (domina/value (domina/by-id "gregorian"))
        parts (str/split greg #"[-/]")
        [y m d] (map (fn [x] (.parseInt js/window x 10)) parts)]
    (ordinal-day d m y)))

(defn calculate [evt]
  (let [lat-d (get-float-value "latitude")
        julian (to-julian)
        minutes (daylight lat-d julian)]
    (domina/set-text! (domina/by-id "result") (str (quot minutes 60) "h "
                      (.toFixed (rem minutes 60) 2) "m"))))
    
(events/listen! (domina/by-id "calculate") :click calculate)
