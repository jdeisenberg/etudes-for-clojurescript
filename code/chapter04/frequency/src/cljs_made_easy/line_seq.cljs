(ns cljs-made-easy.line-seq
  (:require clojure.string)
  (:require-macros [cljs-made-easy.line-seq :refer [with-open]]))

(def fs (js/require "fs"))

(defn- read-chunk [fd]
  (let [length 128
        b (js/Buffer. length)
        bytes-read (.readSync fs fd b 0 length nil)]
    (if (> bytes-read 0)
      (.toString b "utf8" 0 bytes-read))))

(defn line-seq
  ([fd]
   (line-seq fd nil))
  ([fd line]
   (if-let [chunk (read-chunk fd)]
     (if (re-find #"\n" (str line chunk))
       (let [lines (clojure.string/split (str line chunk) #"\n")]
         (if (= 1 (count lines))
           (lazy-cat lines (line-seq fd))
           (lazy-cat (butlast lines) (line-seq fd (last lines)))))
       (recur fd (str line chunk)))
     (if line
       (list line)
       ()))))

