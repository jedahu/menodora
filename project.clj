(defproject
  menodora "0.1.0-SNAPSHOT"
  :description "Jasmine-like test library for clojurescript."

  :dependencies
  [[org.clojure/clojure "1.3.0"]
   [org.clojure/tools.macro "0.1.1"]
   [ring/ring-jetty-adapter "1.1.0-RC1"]]

  :plugins
  [[lein-cst "0.1.0-SNAPSHOT"]]

  :exclusions
  [org.apache.ant/ant]

  :extra-classpath-dirs ["src"]

  :source-path "src"

  :cst
  {:builds
   {:dev {:output-dir ".cst-out/dev"
          :optimizations nil
          :pretty-print true}
    :single {:output-dir ".cst-out/single"
             :optimizations :whitespace
             :pretty-print true}
    :small {:output-dir ".cst-out/small"
            :optimizations :advanced
            :pretty-print false}
    :deploy {:output-to "menodora.js"
             :output-dir ".cst-out/deploy"
             :optimizations :advanced
             :pretty-print false}}
   :build :dev
   :suites [menodora.test.core/core-tests]
   :runners
   {:rhino-console {:cljs menodora.runner.console/run-suites-rhino
                    :proc :rhino
                    :build :single}
    :v8-console {:cljs menodora.runner.console/run-suites-v8
                 :proc ["d8"]
                 :build :single}
    :browser-console {:cljs menodora.runner.console/run-suites-browser
                      :proc cst.server/serve-cljs}}
   :runner :rhino-console})
