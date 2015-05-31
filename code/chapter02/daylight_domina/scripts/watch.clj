(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'daylight_domina.core
   :output-to "out/daylight_domina.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
