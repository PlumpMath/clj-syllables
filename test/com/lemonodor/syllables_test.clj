(ns com.lemonodor.syllables-test
  (:require [clojure.test :refer :all]
            [com.lemonodor.syllables :as syllables]))

(deftest cmudict-test
  (testing "parse-cmudict-line"
    (is (= ["\"unquote" "ah1" "n" "k" "w" "ow1" "t"]
           (syllables/parse-cmudict-line "\"UNQUOTE  AH1 N K W OW1 T")))
    (is (= ["aaa" "t" "r" "ih2" "p" "ah0" "l" "ey1"]
           (syllables/parse-cmudict-line "AAA  T R IH2 P AH0 L EY1")))
    (is (= ["aaronson's" "aa1" "r" "ah0" "n" "s" "ah0" "n" "z"]
           (syllables/parse-cmudict-line
            "AARONSON'S(1)  AA1 R AH0 N S AH0 N Z")))
    (is (= ["abkhazian" "ae0" "b" "k" "ae1" "z" "y" "ah0" "n"]
           (syllables/parse-cmudict-line
            "ABKHAZIAN(3)  AE0 B K AE1 Z Y AH0 N"))))
  (testing "count-cmudict-syllables"
    (is (= 3
           (syllables/count-cmudict-syllables
            ["m" "ah0" "k" "aa1" "r" "th" "iy0"])))
    (is (= 1 (syllables/count-cmudict-syllables ["l" "ih1" "s" "p"]))))
  (testing "make-syllable-db-from-cmudict"
    (let [cmudict (str ";; Test dict\n"
                       "LLAMAS  L AA1 M AH0 Z\n"
                       "LOUIS'(1)  L UW1 IY0 Z\n"
                       "LOUIS'(2)  L UW1 IH0 S IH0 Z\n")
          sdb (syllables/make-syllable-db-from-cmudict (.getBytes cmudict))]
      (is (= {"louis'" #{3 2}, "llamas" #{2}} sdb))
      (is (= #{2 3} (syllables/count-syllables sdb "louis'")))
      (is (= #{2 3} (syllables/count-syllables sdb "LouIS'")))))
  (testing "count-syllables"
    (is (= #{4} (syllables/count-syllables "ridiculous")))
    (is (= nil (syllables/count-syllables "........")))))
