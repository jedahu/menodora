(ns menodora)

(defn opts-body-split
  [forms]
  (let [[opts body] (split-with (comp not list?) forms)]
    [(apply hash-map opts) body]))

(defmacro describe
  [text & forms]
  (let [[{:keys [before after] :as opts} body] (opts-body-split forms)]
    (defmacro should
      [text & body]
      `(swap! menodora.core/*describe-results*
              conj
              [~text (try
                       (binding [menodora.core/*should-results* (atom [])]
                         (and ~before (apply ~before nil))
                         ~@body
                         (and ~after (apply ~after nil))
                         @menodora.core/*should-results*)
                       (catch e# js/Object e#))]))
    (defmacro expect
      [pred & args]
      `(swap! menodora.core/*should-results*
              conj (try
                     (if ((:test ~pred) ~@args)
                       false
                       ((:message ~pred) ~@args))
                     (catch e# js/Object e#))))
    `(let [{before-all# :before-all after-all# :after-all} ~opts]
       (swap!
         menodora.core/*tests*
         conj
         [~text (delay
                  (binding [menodora.core/*describe-results* (atom [])]
                    (and before-all# (apply before-all# nil))
                    (try ~@body (catch e# js/Object e#))
                    (and after-all# (apply after-all# nil))
                    @menodora.core/*describe-results*))]))))

;;. vim: set lispwords+=macrolet:
