(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'teeth.core
   :output-to "out/teeth.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
