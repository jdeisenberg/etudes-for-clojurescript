(ns frequency.core
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
             (reduce (fn [result line] (conj result (add-row line))) [] (rest (cme/line-seq file-descriptor)))))

(def traffic (create-data-structure "traffic_july_2014_edited.csv" headers))

(defn frequency-table
  "Accumulate frequencies for specifier (a heading keyword
   or a function that returns a value) in data-map,
   optionally returning a total."
  [data-map specifier]
  (let [result-map (reduce
                    (fn [acc item]
                      (let [v (if specifier (specifier item) nil)]
                        (assoc acc v (+ 1 (get acc v)))))
                    {} data-map)
        result-seq (sort (seq result-map))
        freq (map last result-seq)]
    [(vec (map first result-seq)) (vec freq) (reduce + freq)]))

(defn -main []
  (println "Hello world!"))

(set! *main-cli-fn* -main)
