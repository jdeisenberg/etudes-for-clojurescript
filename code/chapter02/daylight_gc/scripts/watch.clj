(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'daylight_gc.core
   :output-to "out/daylight_gc.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
