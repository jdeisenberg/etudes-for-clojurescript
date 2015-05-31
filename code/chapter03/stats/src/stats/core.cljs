(ns stats.core
  (:require [clojure.browser.repl :as repl]
            [clojure.string :as str]
            [domina :as dom]
            [domina.events :as ev]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(defn mean
  "Compute mean of a sequence of numbers."
  [x]
  (let [n (count x)]
    (/ (apply + x) n)))

(defn median
  "Compute median of a sequence of numbers."
  [x]
  (let [n (count x)
        remainder (drop (- (int (/ n 2)) 1) (sort x))]
    (if (odd? n)
      (second remainder)
      (/ (+ (first remainder) (second remainder)) 2))))

(defn getsums
  "Reducing function for computing sum and sum of squares.
  The accumulator is a two-vector with the current sum and sum of squares
  Could be made clearer with destructuring, but that's not in
  this chapter."
  [acc item]
  (vector (+ (first acc) item) (+ (last acc) (* item item))))

(defn stdev
  "Compute standard deviation of a sequence of numbers"
  [x]
  (let [[sum sumsq] (reduce getsums [0 0] x)
        n (count x)]
    (.sqrt js/Math (/ (- sumsq (/ (* sum sum) n)) (- n 1)))))

(defn calculate
  "Event handler"
  [evt]
  (let [numbers (map js/window.parseFloat
                  (str/split (domina/value (ev/target evt)) #"[, ]+"))]
       (domina/set-text! (domina/by-id "mean") (mean numbers))
       (domina/set-text! (domina/by-id "median") (median numbers))
       (domina/set-text! (domina/by-id "stdev") (stdev numbers))))

;; connect event handler
(ev/listen! (domina/by-id "numbers") :change calculate)
