(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'formulas.core
   :output-to "out/formulas.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
