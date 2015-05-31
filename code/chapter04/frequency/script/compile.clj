(require '[cljs.build.api :as b])

(b/build "src"
  {:main 'frequency.core
   :output-to "frequency.js"
   :output-dir "out"
   :target :nodejs
   :verbose true})
