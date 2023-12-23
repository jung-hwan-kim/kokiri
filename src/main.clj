(ns main
  (:require [babashka.process :refer [sh shell process exec]]
            [clojure.tools.reader.edn :as edn]
            [databricks :as db]
            [script :as s]))

(def METER_INCH_RATIO 39.37)

(defn cm-to-inch [cm]
  (* (/ METER_INCH_RATIO 100) cm))

(defn inch-to-cm [inch]
  (* (/ inch METER_INCH_RATIO) 100))

(defn dbfs-ls
  {:org.babashka/cli {:exec-args
                      {:p "poc"
                       :dir ""}}}
  [m]
  (println "Input:" m)
  (let [out (db/fs-ls (:p m) (:dir m))]
    (map #(println %) (vec out))
    (println "Total: " (count out))
    (->> out
         (map #(do
                 (print (:name %))
                 (print "\t\t\t")
                 (println (:size %)))))))

(defn run
  "Main run ..
  This is about ru"
  {:org.babashka/cli {:exec-args
                      {:t 3}
                      :coerce {:t :long}}}
  [m]
  (println m)
  (let [config (edn/read-string (slurp "resources/config.edn"))]
    (loop[bat (s/get-battery-percentage)
          num 1]
        (println (str "[" num "] " (s/current-time) " Battery:" bat "%"))
        (if (>= bat 80)
          (let [c (nth config (rem num (count config)))
                msg (format (:msg c) bat)
                voice (:voice c)
                title (:title c)]
            (println msg)
            (s/display-notification title msg)
            (shell "say -v" voice msg)))
        (Thread/sleep (* (:t m) 6 1000))
        (recur (s/get-battery-percentage) (inc num)))))

