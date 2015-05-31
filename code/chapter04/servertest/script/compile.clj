(require '[cljs.build.api :as b])

(b/build "src"
  {:main 'servertest.core
   :output-to "servertest.js"
   :output-dir "out"
   :target :nodejs
   :verbose true})
