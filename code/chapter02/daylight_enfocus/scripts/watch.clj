(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'daylight_enfocus.core
   :output-to "out/daylight_enfocus.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
