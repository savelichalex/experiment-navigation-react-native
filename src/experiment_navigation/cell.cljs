(ns experiment-navigation.cell)

(def ReactNative (js/require "react-native"))

(def Animated (. ReactNative -Animated))
(def AnimatedValue
  (. Animated -Value))
(defn set-animated-value [anim value]
  (.setValue anim value))

(defprotocol IObservable
  (push [self val] "Get value and push it to subscribers")
  (subscribe [self on-next] "Subscribe to changes"))

(defprotocol ICell
  (bind [self cell cb]))

(deftype Cell [subscribers]
  IObservable
  (push [self val]
    (doseq [subscriber @subscribers]
      (apply subscriber [val])))
  (subscribe [self on-next]
    (swap! subscribers conj on-next)))

(defn cell []
  (Cell. (atom [])))

(defn cell-> [c cb]
  (let [new-cell (cell)]
    (subscribe
      c
      (fn [val]
        (push new-cell (cb val))))
    new-cell))

(defn bind-to-animated
  ([cell]
   (bind-to-animated cell 0))
  ([cell initial-value]
   (let [new-animated (AnimatedValue. initial-value)]
     (subscribe
       cell
       (fn [val]
         (set-animated-value new-animated val)))
     new-animated)))