(require '[cljs.closure :as cljsc])

(println "Building ...")

(let [start (System/nanoTime)]
  (cljsc/build "src"
    {:main 'daylight_by_date.core
     :output-to "out/daylight_by_date.js"
     :output-dir "out"
     :optimizations :none
     :cache-analysis true
     :source-map true
     :verbose true})
  (println "... done. Elapsed" (/ (- (System/nanoTime) start) 1e9) "seconds"))


