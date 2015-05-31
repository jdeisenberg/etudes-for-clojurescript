(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'daylight_summary.core
   :output-to "out/daylight_summary.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
