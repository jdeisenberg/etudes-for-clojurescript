(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'traffic.core
   :output-to "traffic.js"
   :output-dir "out"
   :target :nodejs
   :verbose true})
