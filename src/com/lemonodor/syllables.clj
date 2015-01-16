(ns com.lemonodor.syllables
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as string])
  (:gen-class))


(defn parse-cmudict-line [line]
  (string/split (string/lower-case line) #"(\(\d+\))? +"))


(def digit-chars #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})


(defn count-cmudict-syllables [phonemes]
  (count
   (filter
    (fn [^String p]
      (digit-chars (.charAt p (dec (.length p)))))
    phonemes)))


(defn make-syllable-db-from-cmudict [path]
  (with-open [rdr (io/reader path)]
    (apply
     merge-with
     set/union
     (map (fn [[word & phonemes]]
            {word #{(count-cmudict-syllables phonemes)}})
          (map parse-cmudict-line
               (filter
                (fn [^String l] (not (= (.charAt l 0) \;)))
                (line-seq rdr)))))))


(def default-syllables-db (atom nil))


(defn count-syllables
  ([word]
   (when-not @default-syllables-db
     (swap! default-syllables-db
            (fn [_]
              (edn/read-string
               (slurp (io/resource "com/lemonodor/syllables/syllables.db"))))))
   (count-syllables @default-syllables-db word))
  ([sdb word]
   (sdb (string/lower-case word))))


(defn -main [& args]
  (let [sdb (make-syllable-db-from-cmudict (first args))]
    (binding [*out* *err*]
      (println "Creating syllable database with" (count sdb) "words."))
    (prn sdb)))
