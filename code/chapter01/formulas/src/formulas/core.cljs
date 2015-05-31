(ns formulas.core
  (:require [clojure.browser.repl :as repl]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

(defn distance
  "Calculate distance traveled by an object moving
  with a given acceleration for a given amount of time."
  [accel time]
  (* accel time time))
  
(defn kinetic-energy
  "Calculate kinetic energy given mass and velocity"
  [m v]
  (/ (* m v v) 2.0))
  
(defn centripetal
  "Calculate centripetal acceleration given velocity and radius"
  [v r]
  (/ (* v v) r))
  
(defn average
  "Calculate average of two numbers"
  [a b]
  (/ (+ a b) 2.0))

(defn variance
  "Calculate variance of two numbers"
  [a b]
  (- (* 2 (+ (* a a) (* b b))) (* (+ a b) (+ a b))))

(def G 6.6784e-11)

(defn gravitational-force
  "Calculate gravitational force of two objects of
  mass m1 and m2, with centers of gravity at a distance r"
  [m1 m2 r]
  (/ (* G m1 m2) (* r r)))

(defn wind-chill
  "Wind chill, given temperature T in degrees Celsius
  and wind velocity v in km/hour"
  [t kph]
  (let [vpower (.pow js/Math kph 0.16)]
    (+ 13.12 (* 0.6215 t) (* -11.37 vpower) (* 0.3965 t vpower))))

(defn monthly-payment
  "Calculate monthly payment on a loan of amount p,
  with annual percentage rate apr, and a given number of years"
  [p apr years]
  (let [r (/ apr 12.0)
        n (* years 12)
        factor (.pow js/Math (+ 1 r) n)]
    (* p (/ (* r factor) (- factor 1)))))

(defn radians
  "Convert degrees to radians"
  [degrees]
  (* (/ (.-PI js/Math) 180) degrees))

(def axis (radians 23.439))

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

(defn leap-year?
  "Return true if given year is a leap year; false otherwise"
  [year]
  (or (and (= 0 (rem year 4)) (not= 0 (rem year 100)))
    (= 0 (rem year 400))))

(defn ordinal-day
  "Compute ordinal day given Gregorian day, month, and year"
  [day month year]
  (let [leap (leap-year? year)
        feb-days (if leap 29 28)
        days-per-month [0 31 feb-days 31 30 31 30 31 31 30 31 30 31]
        month-ok (and (> month 0) (< month 13))
        day-ok (and month-ok (> day 0) (<= day (+ (nth days-per-month month))))
        subtotal (reduce + (take month days-per-month))]
    (if day-ok (+ subtotal day) 0)))
