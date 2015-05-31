(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'make_teeth.core
   :output-to "out/make_teeth.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
