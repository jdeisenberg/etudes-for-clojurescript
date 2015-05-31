;; This is a macro, and must be in clojure. It's name and location is the same as
;; the cljs file, except with a .clj extension.
(ns cljs-made-easy.line-seq
  (:refer-clojure :exclude [with-open]))

(defmacro with-open [bindings & body]
  (assert (= 2 (count bindings)) "Incorrect with-open bindings")
  `(let ~bindings
     (try
       (do ~@body)
       (finally
         (.closeSync cljs-made-easy.line-seq/fs ~(bindings 0))))))
