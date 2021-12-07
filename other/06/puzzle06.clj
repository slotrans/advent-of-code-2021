(ns net.blergh.advent2021
    (:require [clojure.string :as str]
    )
)


(defn parse-input [s]
    (mapv
        #(Integer/parseInt %)
        (str/split (str/trim s) #",")
    )
)


(defn step-simulation-slow [timers]
    (flatten
        (map
            (fn [t]
                (if (= t 0)
                    [8 6]
                    [(dec t)]
                )
            )
            timers
        )
    )
)


(defn step-simulation-fast [timers]
    (loop [ i 0
          , mut-timers timers
          ]
        (let [ft (get timers i)]
            (cond
                (= i (count timers))
                    mut-timers
                (= ft 0)
                    (recur (inc i) (conj (assoc mut-timers i 6) 8))
                :else
                    (recur (inc i) (assoc mut-timers i (dec ft)))
            )
        )
    )
)


(defn simulate-for-days [timers days]
    (nth (iterate step-simulation-fast timers) days)
)


(defn sample-p1 [timers]
    (println "SAMPLE P1")

    (let [ after18 (count (simulate-for-days timers 18))
         , after80 (count (simulate-for-days timers 80))
         ]
        (println (str "fish after 18 days: " after18))
        (assert (= after18 26))
        (println (str "fish after 80 days: " after80))
        (assert (= after80 5934))
    )
)


(defn part1 [timers]
    (println "Part 1")

    (let [ after80 (count (simulate-for-days timers 80))
         ]
        (println (str "(p1 answer) fish after 80 days: " after80)) ; 362740
    )
)


(defn better-simulation [timer-map]
    (apply
        (partial merge-with +)
        (for [i [8 7 6 5 4 3 2 1 0]] ; clear than (reverse (range 9)) tbqh
            (let [ft (get timer-map i 0)]
                (if (= i 0)
                    {8 ft, 6 ft}
                    {(dec i) ft}
                )
            )
        )
    )
)


(defn sample-p2 [timers]
    (println "SAMPLE P2")

    (let [ timer-map (frequencies timers)
         , after18  (apply + (vals (nth (iterate better-simulation timer-map) 18)))
         , after80  (apply + (vals (nth (iterate better-simulation timer-map) 80)))
         , after256 (apply + (vals (nth (iterate better-simulation timer-map) 256)))
         ]
        (println (str "fish after 18 days: " after18))
        (assert (= after18 26))
        (println (str "fish after 80 days: " after80))
        (assert (= after80 5934))
        (println (str "fish after 256 days: " after256))
        (assert (= after256 26984457539))
    )
)


(defn part2 [timers]
    (println "Part 1")

    (let [ timer-map (frequencies timers)
         , after256 (apply + (vals (nth (iterate better-simulation timer-map) 256)))
         ]
        (println (str "(p2 answer) fish after 80 days: " after256)) ; 1644874076764
    )
)


(def sample-input "3,4,3,1,2")
(def sample-fish-timers (parse-input sample-input))

(def input06 (slurp "input06"))
(def initial-fish-timers (parse-input input06))


;(sample-p1 sample-fish-timers)

(part1 initial-fish-timers)

;(sample-p2 sample-fish-timers)

(part2 initial-fish-timers)
