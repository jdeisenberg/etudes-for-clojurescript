(ns move-zeros.core
  (:require [clojure.browser.repl :as repl]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

(defn move-zeros
  "Move zeros to end of a list or vector of numbers"
  [numbers]
  (let [nonzero (filter (fn[x] (not= x 0)) numbers)]
    (concat nonzero
       (repeat (- (count numbers) (count nonzero)) 0))))
