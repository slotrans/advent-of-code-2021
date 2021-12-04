(ns net.blergh.advent2021
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
    )
)


(defn add-bit-strings [bs-one bs-two]
    (mapv
        (fn [x y]
            (+ (Integer/parseInt (str x)) (Integer/parseInt (str y)))
        )
        bs-one
        bs-two
    )
)

(defn get-bit-frequency [bit-strings]
    (reduce
        add-bit-strings
        bit-strings
    )
)

(defn get-most-common-bits [bit-strings]
    (let [ frequencies (get-bit-frequency bit-strings)
         , bit-string-length (count frequencies)
         , input-count (count bit-strings)
         ]
        (mapv
            #(if (>= (/ % input-count) 0.5)
                \1
                \0
            )
            frequencies
        )
    )
)

(defn invert-bit-vec [bit-vec]
    (mapv
        #(if (= \1 %)
            \0
            \1
        )
        bit-vec
    )
)

(defn bit-vec-to-int [bit-vec]
    (edn/read-string (str "2r" (str/join "" bit-vec)))
)


(def input03 (slurp "input03"))
(def bit-strings (str/split input03 #"\n"))

(def p1-most-common-bits (get-most-common-bits bit-strings))
(def p1-least-common-bits (invert-bit-vec p1-most-common-bits))

(def gamma-rate (bit-vec-to-int p1-most-common-bits))
(def epsilon-rate (bit-vec-to-int p1-least-common-bits))
(def power-consumption (* gamma-rate epsilon-rate))

(println (str "gamma rate = " gamma-rate)) ; 199
(println (str "epsilon rate = " epsilon-rate)) ; 3896
(println (str "(p1 answer) power consumption = " power-consumption)) ; 775304

;;; part 2 ;;;

(defn get-p2-rating [bit-strings get-comparison-bits-fn]
    (let [ bit-string-length (count (first bit-strings))
         ]
        (loop [ temp-bit-strings bit-strings
              , i 0
              ]
            (if (= 1 (count temp-bit-strings))
                (bit-vec-to-int (str/split (first temp-bit-strings) #""))
                (let [comparison-bits (get-comparison-bits-fn temp-bit-strings)]
                    (recur
                        (filter #(= (get % i) (get comparison-bits i)) temp-bit-strings)
                        (inc i)
                    )
                )
            )
        )
    )
)

(defn oxygen-generator-rating-fn [bit-strings]
    (get-most-common-bits bit-strings)
)

(defn co2-scrubber-rating-fn [bit-strings]
    (invert-bit-vec (get-most-common-bits bit-strings))
)


(def oyxgen-generator-rating (get-p2-rating bit-strings oxygen-generator-rating-fn))
(def co2-scrubber-rating (get-p2-rating bit-strings co2-scrubber-rating-fn))
(def life-support-rating (* oyxgen-generator-rating co2-scrubber-rating))

(println (str "oxygen generator rating = " oyxgen-generator-rating)) ; 509
(println (str "co2 scrubber rating = " co2-scrubber-rating)) ; 2693
(println (str "(p2 answer) life support rating = " life-support-rating)) ; 1370737
