(ns make_teeth.core
  (:require [clojure.browser.repl :as repl]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(defn one-tooth
  "Generate one tooth"
  [tooth-present probability]
  (if (= tooth-present "F")
    []
    (let [base-depth (if (< (rand) probability) 2 3)]
      (loop [n 6
             result []]
         (if (= n 0) result
           (recur (dec n) (conj result (+ base-depth (- 1 (rand-int 3))))))))))

(defn generate-list
  "Take list of teeth, probability, and current vector of vectors.
  Add pockets for each tooth."
  [teeth-present probability result]
  (if (empty? teeth-present) result
    (recur (rest teeth-present) probability (conj result (one-tooth (first teeth-present) probability)))))
    
(defn generate-pockets
  "Take list of teeth present and probability of a good tooth,
  and create a list of pocket depths."
  [teeth-present probability]
  (generate-list teeth-present probability []))
