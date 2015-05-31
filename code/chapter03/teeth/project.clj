(defproject teeth "0.1.0-SNAPSHOT"
  :description "Analyze data on pocket depth of gums using a list of lists."
  :url "https://github.com/jdeisenberg/etudes-for-clojurescript"
  :dependencies [[org.clojure/clojure "1.7.0-beta2"]
                 [org.clojure/clojurescript "0.0-3269"]]
  :jvm-opts ^:replace ["-Xmx1g" "-server"]
  :node-dependencies [[source-map-support "0.2.8"]]
  :plugins [[lein-npm "0.4.0"]]
  :source-paths ["src" "target/classes"]
  :clean-targets ["out" "release" "target" ".repl-0.0-3269"]
  :target-path "target")
