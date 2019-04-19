(ns xi-360.clj-utils.until)

;; The following code is taken from Clojure submissions, it was never
;; adopted into clojure.core:
;; https://dev.clojure.org/jira/browse/CLJ-1451

(defn take-until
  "Returns a lazy sequence of successive items from coll until
  (pred item) returns true, including that item. pred must be
  free of side-effects. Returns a transducer when no collection
  is provided."
  {;:added "1.7"
   :static true}
  ([pred]
   (fn [rf]
     (fn
       ([] (rf))
       ([result] (rf result))
       ([result input]
        (if (pred input)
          (ensure-reduced (rf result input))
          (rf result input))))))
  ([pred coll]
   (lazy-seq
    (when-let [s (seq coll)]
      (if (pred (first s))
        (cons (first s) nil)
        (cons (first s) (take-until pred (rest s))))))))


;; The following code is adopted from clojure.core/partiion-by and partition-all

(defn partition-until
  "Applies pred to each value in coll, splitting it each time pred returns true.
  Returns a lazy seq of partitions.  Returns a stateful transducer when
  no collection is provided."
  {;:added "1.2"
   :static true}
  ([pred]
   (fn [rf]
     (let [a (java.util.ArrayList.)]
       (fn
         ([] (rf))
         ([result]
          (let [result (if (.isEmpty a)
                         result
                         (let [v (vec (.toArray a))]
                           ;;clear first!
                           (.clear a)
                           (unreduced (rf result v))))]
            (rf result)))
         ([result input]
          (.add a input)
          (if (pred input)
            (let [v (vec (.toArray a))]
              (.clear a)
              (rf result v))
            result))))))
  ([pred coll]
   (lazy-seq
    (when-let [s (seq coll)]
      (let [run (take-until pred s)]
        (cons run (partition-until pred (seq (drop (count run) s)))))))))
