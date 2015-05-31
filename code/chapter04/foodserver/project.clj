(defproject foodserver "0.1.0-SNAPSHOT"
  :description "Server returns foods that go with chosen condiments."
  :url "https://github.com/jdeisenberg/etudes-for-clojurescript"

  :dependencies [[org.clojure/clojure "1.7.0-beta2"]
                 [org.clojure/clojurescript "0.0-3269"]
                 [hiccups "0.3.0"]]

  :node-dependencies [[source-map-support "0.2.8"]
                      [express "4.11.1"]]

  :plugins [[lein-npm "0.4.0"]]

  :source-paths ["src"])
