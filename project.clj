(defproject
  menodora "0.1.0-SNAPSHOT"
  :description "Jasmine-like test library for clojurescript."

  :dependencies
  [[org.clojure/clojure "1.3.0"]
   [org.clojure/tools.macro "0.1.1"]]
  
  :plugins
  [[lein-cst "0.1.0-SNAPSHOT"]]

  :exclusions
  [org.apache.ant/ant]

  :extra-classpath-dirs ["src"]

  :source-path "src"

  :cst
  {:src-dir "src"
   :test-dir "test"
   :builds
   {:dev {:output-to ".cst-out/dev/main.js"
          :output-dir ".cst-out/dev"
          :optimizations nil
          :pretty-print true
          :src-dir "src"}
    :single {:output-dir ".cst-out/single"
             :optimizations :whitespace
             :pretty-print true}
    :small {:output-to ".cst-out/small/main.js"
            :output-dir ".cst-out/small"
            :optimizations :advanced
            :pretty-print false
            :src-dir "src"}
    :deploy {:output-to "menodora.js"
             :output-dir ".cst-out/deploy"
             :optimizations :advanced
             :pretty-print false
             :src-dir "src"}}
   :build :single
   :suites [menodora.test.core/core-tests]
   :runners
   {:rhino-console {:cljs menodora.runner.console/run-suites-rhino
                    :proc :rhino}
    :v8-console {:cljs menodora.runner.console/run-suites-v8
                 :proc ["d8"]}
    :browser-console {:cljs menodora.runner.console/run-suites-browser
                      :clj menodora.test.server/serve-cljs}}
   :runner :rhino-console
   :servers
   {:test menodora.test.server/serve-cljs}
   :server :test})
