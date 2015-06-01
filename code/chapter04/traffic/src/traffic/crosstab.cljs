(ns traffic.crosstab)

(defn marginal
  "Get marginal totals for a frequency"
  [freq]
  (vec (map last (sort (seq freq)))))

(defn cross-tab
  "Accumulate frequencies for given row and column in data-map,
  returning row and column totals, plus grand total."
  [data-map row-spec col-spec]
  (let [[row-freq  col-freq cross-freq] (reduce
                                          (fn [acc item]
                                            (let [r (if row-spec (row-spec item) nil)
                                                  c (if col-spec (col-spec item) nil)]
                                              [(assoc (first acc) r (+ 1 (get (first acc) r)))
                                               (assoc (second acc) c (+ 1 (get (second acc) c)))
                                               (assoc-in (last acc) [r c] (+ 1 (get-in (last acc) [r c])))]))
                                          [{} {} {}] data-map)
        row-totals (marginal row-freq)]
    [(vec (sort (keys row-freq)))
     (vec (sort (keys col-freq)))
     (vec (for [r (sort (keys row-freq))]
            (vec (for [c (sort (keys col-freq))]
                   (if-let [n (get-in cross-freq (list r c))] n 0)))))
     row-totals
     (marginal col-freq)
     (reduce + row-totals)]))

(defn frequency-table
  "Accumulate frequencies for specifier in data-map,
  optionally returning a total. Use a call to cross-tab
  to re-use code."
  [data-map specifier]
  (let [[row-labels _ row-totals _ grand-total] (cross-tab data-map specifier nil)]
    [row-labels (vec (map first row-totals)) grand-total]))
