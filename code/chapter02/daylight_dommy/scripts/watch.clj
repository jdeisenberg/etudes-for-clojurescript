(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'daylight_dommy.core
   :output-to "out/daylight_dommy.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
