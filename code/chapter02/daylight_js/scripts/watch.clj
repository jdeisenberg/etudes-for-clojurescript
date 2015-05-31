(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'daylight_js.core
   :output-to "out/daylight_js.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
