(ns xi-360.clj-utils.until-test
  (:use [clojure.test])
  (:require [xi-360.clj-utils.until :as u :refer :all]))


(deftest take-until-test
  (testing "take-until"
    (is (true? (= [:a] (u/take-until #{:a} [:a :b :c]))))
    (is (true? (= [:a :b] (u/take-until #{:b} [:a :b :c]))))
    (is (true? (= '(1 2 -1) (u/take-until neg? '(1 2 -1)))))
    (is (true? (= '() (u/take-until pos? '()))))))


(deftest partition-until-test
  (testing "partition-until in a functional form"
    (let [s1 [:a :b :c :a :b :d :d :e]]
      (is (true? (= '((:a :b :c) (:a :b :d) (:d) (:e))
                    (u/partition-until #{:c :d} s1)))))))

(deftest partition-until-xf-test
  (testing "partition-until in a transducer form"
    (let [xfp (u/partition-until pos?)]
      (is (true? (= '([1] [-1 -2 1] [1] :a)
                      (transduce xfp conj '(:a) [1 -1 -2 1 1])))))))
