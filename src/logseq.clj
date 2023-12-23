(ns logseq
  (:require [clojure.string :as str]
            [babashka.process :refer [sh shell process exec]])
  (:import (java.time LocalDate)
           (java.time.format DateTimeFormatter)))
(defn say[voice message]
  (println voice " says: " message)
  (shell "say" "-v" voice message))

(defn string-to-lines [s]
  (str/split s #"\n\t*"))

(defn remove-first-letter [s]
  (if (empty? s)
    s
    (subs s 1)))

(defn get-today-yyyy_MM_dd []
  (let [today (LocalDate/now)
        formatter (DateTimeFormatter/ofPattern "yyyy_MM_dd")]
    (.format today formatter)))

(defn read[voice]
  (let [today (get-today-yyyy_MM_dd)
        today-msg (slurp (str (str (System/getProperty "user.home") "/hermeneut/journals/" today ".md"))
        ]
    (println today)
    (doseq [line (map remove-first-letter (string-to-lines today-msg))]
      (say voice line))))

(defn run
  {:org.babashka/cli {:exec-args
                      {:t 3
                       :v "Zoe (Premium)"}
                      :coerce {:t :long}}}
  [m]
  (println m)
  (read (:v m)))