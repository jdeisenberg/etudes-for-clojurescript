(require '[cljs.closure :as cljsc])

(cljsc/watch "src"
  {:main 'react_q.core
   :output-to "out/react_q.js"
   :output-dir "out"
   :optimizations :none
   :cache-analysis true
   :source-map true})
