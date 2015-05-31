(require '[cljs.build.api :as b])

(b/build "src"
  {:main 'foodserver.core
   :output-to "foodserver.js"
   :output-dir "out"
   :target :nodejs
   :verbose true})
