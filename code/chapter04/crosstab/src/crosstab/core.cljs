(ns crosstab.core
  (:require [cljs.nodejs :as nodejs]
            [clojure.string :as str]
            [cljs-made-easy.line-seq :as cme]))

(nodejs/enable-util-print!)

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
             (reduce (fn [result line] (conj result (add-row line))) []
               (rest (cme/line-seq file-descriptor)))))

(def traffic (create-data-structure "traffic_july_2014_edited.csv" headers))

(defn marginal
  "Get marginal totals for a frequency map. (Utility function)"
  [freq]
  (vec (map last (sort (seq freq)))))

(defn cross-tab
  "Accumulate frequencies for given row and column in data-map,
  returning row and column totals, plus grand total."
  [data-map row-spec col-spec]
  
  ; In the following call to reduce, the accumulator is a
  ; vector of three maps.
  ; The first maps row values => frequency
  ; The second maps column values => frequency
  ; The third is a map of maps, mapping  row values => column values => frequency
 
  (let [[row-freq  col-freq cross-freq] (reduce
                     (fn [acc item]
                       (let [r (if row-spec (row-spec item) nil)
                             c (if col-spec (col-spec item) nil)]
                         [(assoc (first acc) r (+ 1 (get (first acc) r)))
                          (assoc (second acc) c (+ 1 (get (second acc) c)))
                          (assoc-in (last acc) [r c] (+ 1 (get-in (last acc) [r c])))]))
                     [{} {} {}] data-map)
        ; I need row totals as part of the return, and I also
        ; add them to get grand total - don't want to re-calculate
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

(defn -main []
  (println "Hello world!"))

(set! *main-cli-fn* -main)
