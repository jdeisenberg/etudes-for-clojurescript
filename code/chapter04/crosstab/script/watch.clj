(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'crosstab.core
   :output-to "crosstab.js"
   :output-dir "out"
   :target :nodejs
   :verbose true})
