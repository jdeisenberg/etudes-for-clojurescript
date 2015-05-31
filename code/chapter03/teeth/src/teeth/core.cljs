(ns teeth.core
  (:require [clojure.browser.repl :as repl]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

(def pocket-depths
  [[], [2 2 1 2 2 1], [3 1 2 3 2 3],
  [3 1 3 2 1 2], [3 2 3 2 2 1], [2 3 1 2 1 1],
  [3 1 3 2 3 2], [3 3 2 1 3 1], [4 3 3 2 3 3],
  [3 1 1 3 2 2], [4 3 4 3 2 3], [2 3 1 3 2 2],
  [1 2 1 1 3 2], [1 2 2 3 2 3], [1 3 2 1 3 3], [],
  [3 2 3 1 1 2], [2 2 1 1 3 2], [2 1 1 1 1 2],
  [3 3 2 1 1 3], [3 1 3 2 3 2], [3 3 1 2 3 3],
  [1 2 2 3 3 3], [2 2 3 2 3 3], [2 2 2 4 3 4],
  [3 4 3 3 3 4], [1 1 2 3 1 2], [2 2 3 2 1 3],
  [3 4 2 4 4 3], [3 3 2 1 2 3], [2 2 2 2 3 3],
  [3 2 3 2 3 2]])

(defn bad-tooth
  "Accumulator: vector of bad tooth numbers and current index"
  [[bad-list index] tooth]
  (if (some (fn[x] (>= x 4)) tooth)
    (vector (conj bad-list index) (inc index))
    (vector bad-list (inc index))))

(defn alert
  "Display tooth numbers where any of the
  pocket depths is 4 or greater."
  [depths]
  (first (reduce bad-tooth [[] 1] depths)))

