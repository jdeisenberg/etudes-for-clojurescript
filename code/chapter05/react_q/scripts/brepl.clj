(require
  '[cljs.repl :as repl]
  '[cljs.build.api]
  '[cljs.repl.browser :as browser])

(cljs.build.api/build "src"
  {:main 'hello-world.core
   :output-to "out/main.js"
   :verbose true})

(repl/repl (browser/repl-env) ;; was repl/repl*
  :watch "src"
  :output-dir "out"
  :optimizations :none
  :cache-analysis true                
  :source-map true)

