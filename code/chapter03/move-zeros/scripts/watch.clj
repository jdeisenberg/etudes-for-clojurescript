(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'move-zeros.core
   :output-to "out/move_zeros.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
