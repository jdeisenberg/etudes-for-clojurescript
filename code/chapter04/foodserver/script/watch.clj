(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'foodserver.core
   :output-to "foodserver.js"
   :output-dir "out"
   :target :nodejs
   :verbose true})
