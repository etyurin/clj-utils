(ns clj-utils.nano
  (:import [java.time Instant Duration]
           java.lang.System))

;; Note: (System/nanoTime) generates a long and takes about 20-25ns
;; (java.time.Instant/now) can be 10x as slow

;; (System/gc)
;; (for [i (range 10)]
;;   (let [n1 (System/nanoTime)
;;         n2 (System/nanoTime)
;;         i1 (java.time.Instant/now)
;;         i2 (java.time.Instant/now)
;;         delta-nano (- n2 n1)
;;         delta-instant (java.time.Duration/between i1 i2)]
;;   (println i "Instant->Instant"(.toNanos delta-instant)
;;            "nano->nano" delta-nano)))
 
;; 0 Instant->Instant 1000 nano->nano 191
;; 1 Instant->Instant 0 nano->nano 101
;; 2 Instant->Instant 0 nano->nano 70
;; 3 Instant->Instant 0 nano->nano 79
;; 4 Instant->Instant 1000 nano->nano 76
;; 5 Instant->Instant 1000 nano->nano 55
;; 6 Instant->Instant 1000 nano->nano 75
;; 7 Instant->Instant 0 nano->nano 72
;; 8 Instant->Instant 0 nano->nano 83
;; 9 Instant->Instant 1000 nano->nano 75

;; (System/gc)
;; (for [i (range 10)]
;;   (let [n1 (System/nanoTime)
;;         i1 (java.time.Instant/now)
;;         n2 (System/nanoTime)
;;         i2 (java.time.Instant/now)
;;         delta-nano (- n2 n1)
;;         delta-instant (java.time.Duration/between i1 i2)]
;;   (println i "Instant->nano->Instant"(.toNanos delta-instant)
;;            "nano->Instant->nano" delta-nano)))

;; 0 Instant->nano->Instant 1000 nano->Instant->nano 6015
;; 1 Instant->nano->Instant 0 nano->Instant->nano 964
;; 2 Instant->nano->Instant 1000 nano->Instant->nano 643
;; 3 Instant->nano->Instant 0 nano->Instant->nano 609
;; 4 Instant->nano->Instant 0 nano->Instant->nano 477
;; 5 Instant->nano->Instant 0 nano->Instant->nano 521
;; 6 Instant->nano->Instant 1000 nano->Instant->nano 603
;; 7 Instant->nano->Instant 0 nano->Instant->nano 483
;; 8 Instant->nano->Instant 0 nano->Instant->nano 505
;; 9 Instant->nano->Instant 0 nano->Instant->nano 503

(defonce ^:const default-sync-count 10)

(defn sync-nano
  "Produces a synced pair [Instant Long (nsec)]"
  ([]
   (sync-nano default-sync-count))
  ([n]
   (System/gc)
   (dotimes [i n]
     (let [_ (java.time.Instant/now)
           _ (System/nanoTime)
           _ (System/nanoTime)]))
   (let [i1 (java.time.Instant/now)
         n1 (System/nanoTime)
         n2 (System/nanoTime)
         delta-nano (- n2 n1)]
     ;; (println "delta-nano:" delta-nano)
     [i1 (- n1 delta-nano)])))


(defn Instant->nano [instant calibration]
  (let [[i0 n0] calibration
        delta-duration (java.time.Duration/between i0 instant)
        delta-nano (.toNanos delta-duration)]
    ;; (println "delta-duration:" delta-duration)
    ;; (println "delta-nano:" delta-nano)
    (+ n0 delta-nano)))


(defn nano->Instant [nano calibration]
  (let [[i0 n0] calibration
        delta-nano (- nano n0)
        delta-duration (java.time.Duration/ofNanos delta-nano)]
    ;; (println "delta-nano:" delta-nano)
    ;; (println "delta-duration:" delta-duration)
    (.plus i0 delta-duration)))


(defmacro now [] `(System/nanoTime))


(defmacro now-Instant [calibration]
  `(nano->Instant (System/nanoTime) ~calibration))
