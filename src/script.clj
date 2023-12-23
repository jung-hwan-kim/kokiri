#!/usr/bin/env bb
(ns script
  (:require [babashka.process :refer [sh shell process exec]]))

(defn say[voice message]
  (shell "say" "-v" voice message))

(defn play-audio [file-path]
  (sh "afplay" file-path))

(defn play-macos-sound[name]
  (play-audio (str "/System/Library/Sounds/" name ".aiff")))

(defn display-notification [title message]
  (let [script (str "display notification \"" message "\" with title \"" title "\"")]
    (sh "osascript" "-e" script)))

(defn get-battery-percentage []
  (let [output (:out (sh "pmset" "-g" "batt"))
        regex-pattern #"(\d+)%"
        match (re-find regex-pattern output)]
    ;(println "checking battery..")
    (if match
      (Integer/parseInt (second match))
      nil)))

(defn current-time []
  (let [today (java.time.LocalDateTime/now)
        formatter (java.time.format.DateTimeFormatter/ofPattern "yyyyMMdd HH:mm")]
    (.format today formatter)))


(defn run
  "Main run"
  []
  (loop[bat (get-battery-percentage)]
    (println (str (current-time) " Battery:" bat "%"))
    (if (> bat 80)
      (display-notification "Battery Warning" (str "Battery at " bat "%")))
    (Thread/sleep (* 1 10 1000))
    (recur (get-battery-percentage))))