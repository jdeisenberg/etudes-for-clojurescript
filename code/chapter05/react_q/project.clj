(defproject react_q "0.1.0-SNAPSHOT"
  :description "Reactive programming with Quiescent"
  :url "https://github.com/jdeisenberg/etudes-for-clojurescript"
  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [org.clojure/clojurescript "0.0-3308"]
                 [quiescent "0.2.0-alpha1"]]
  :jvm-opts ^:replace ["-Xmx1g" "-server"]
  :node-dependencies [[source-map-support "0.2.8"]]
  :plugins [[lein-npm "0.4.0"]]
  :source-paths ["src" "target/classes"]
  :clean-targets ["out" "release"]
  :target-path "target")
