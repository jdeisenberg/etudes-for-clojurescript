(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'stats.core
   :output-to "out/stats.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
