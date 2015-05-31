(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'daylight_by_date.core
   :output-to "out/daylight_by_date.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
