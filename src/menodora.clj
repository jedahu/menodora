(ns menodora)

(defn opts-body-split
  [forms]
  (let [[opts body] (split-with (comp not list?) forms)]
    [(apply hash-map opts) body]))

(defmacro describe
  [text & forms]
  (let [[{:keys [before after] :as opts} body] (opts-body-split forms)]
    (defmacro it
      [text & body]
      `(do
         (binding [menodora.core/*it-results* (atom [])]
           (and ~before (apply ~before nil))
           ~@body
           (and ~after (apply ~after nil))
           (swap! menodora.core/*describe-results*
                  conj [~text @menodora.core/*it-results*]))))
    (defmacro expect
      [pred & args]
      `(swap! menodora.core/*it-results*
              conj (if ((:test ~pred) ~@args)
                     false
                     ((:message ~pred) ~@args))))
    `(let [{before-all# :before-all after-all# :after-all} ~opts]
       (swap!
         menodora.core/tests
         conj
         [~text (delay
                  (binding [menodora.core/*describe-results* (atom [])]
                    (and before-all# (apply before-all# nil))
                    ~@body
                    (and after-all# (apply after-all# nil))
                    @menodora.core/*describe-results*))]))))

;;. vim: set lispwords+=macrolet:
