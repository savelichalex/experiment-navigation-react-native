(ns experiment-navigation.ios.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [experiment-navigation.handlers]
            [experiment-navigation.subs]
            [experiment-navigation.cell :as c]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-opacity (r/adapt-react-class (.-TouchableOpacity ReactNative)))

(def Animated (. ReactNative -Animated))
(def animated-touchable-opacity
  (r/adapt-react-class
    (. Animated (createAnimatedComponent (.-TouchableOpacity ReactNative)))))
(def animated-view (r/adapt-react-class (. Animated -View)))
(def animated-text (r/adapt-react-class (. Animated -Text)))

;(defn first-scene-navbar [opacity width]
;  [view {:style {:position "absolute"
;                 :height 50
;                 :width width
;                 :top 0
;                 :left 0
;                 :right 0
;                 :opacity opacity
;                 :background-color "blue"
;                 :justify-content "center"
;                 :align-items "center"}}
;   [text "Title"]])

(defn first-scene-navbar [width height progress render-next]
  (let [opacity (c/cell->
                  progress
                  (fn [progress]
                    (if (< progress 0.5)
                      (- 1 (* progress 2))
                      0)))
        margin-left (c/cell->
                      progress
                      (fn [progress]
                        (if (< progress 0.5)
                          (* -1 (* (* progress 2) 250))
                          0)))]
    [animated-view {:style {:position         "absolute"
                            ;:height (+ (* (- 1 progress) 50) 50)
                            :height           100
                            :width            width
                            :top              0
                            :left             0
                            :right            0
                            :flex-direction   "row"
                            :background-color "rgba(78,86,242,.5)"
                            :justify-content  "center"
                            :align-items      "center"}}
     [view {:flex 3}]
     [animated-view {:style {:flex            8
                             :opacity         (c/bind-to-animated opacity 1)
                             :margin-left     (c/bind-to-animated margin-left 0)
                             :justify-content "center"
                             :align-items     "center"}}
      [animated-text {:style {:font-size   16
                              :font-weight "bold"
                              :color       "white"}} "First scene"]]
     [animated-touchable-opacity {:style    {:flex            3
                                             :padding-left    10
                                             :padding-right   10
                                             :justify-content "center"
                                             :align-items     "center"
                                             ;:opacity         (c/bind-to-animated opacity 1)
                                             }
                                  :on-press #(render-next progress)}
      [text "Next"]]]))

(defn second-scene-navbar [width height progress render-next render-back]
  (let [opacity (c/cell->
                  progress
                  (fn [progress]
                    (if (> progress 0.5)
                      (- (* progress 2) 1)
                      0)))
        margin-left (c/cell->
                      progress
                      (fn [progress]
                        (if (> progress 0.5)
                          (* (- 1 (- (* progress 2) 1)) 250)
                          0)))]
    [animated-view {:style {:position         "absolute"
                            ;:height (+ (* (- 1 progress) 50) 50)
                            :height           100
                            :width            width
                            :top              0
                            :left             0
                            :right            0
                            :flex-direction "row"
                            :justify-content  "center"
                            :align-items      "center"
                            :background-color "rgba(78,86,242,.5)"}}
     [animated-touchable-opacity {:style    {:flex            3
                                             :padding-left    10
                                             :padding-right   10
                                             :justify-content "center"
                                             :align-items     "center"
                                             ;:opacity         (c/bind-to-animated opacity 1)
                                             }
                                  :on-press #(render-back progress)}
      [text "Back"]]
     [animated-view {:style {:flex            8
                             :opacity         (c/bind-to-animated opacity 0)
                             :margin-left     (c/bind-to-animated margin-left 0)
                             :justify-content "center"
                             :align-items     "center"}}
      [animated-text {:style {:font-size   16
                              :font-weight "bold"
                              :color       "white"}} "Second scene"]]
     [view {:flex 3}]]))

(defn first-scene-content [width height progress]
  [animated-view {:style {:position         "absolute"
                          :height           height
                          :width            width
                          :left             0
                          :padding-bottom   60
                          :justify-content  "center"
                          :background-color "grey"
                          :align-items      "center"}}
   [text "JKHKJDBJKDB"]])

(def tabbar-height 60)

(defn first-scene-tabbar [width height progress]
  (let [bottom (c/cell->
                 progress
                 (fn [progress]
                   (if (< progress 0.5)
                     (* -1 (- tabbar-height (* (- 1 (* progress 2)) tabbar-height)))
                     (* -1 tabbar-height))))]
    [animated-view {:style {:position         "absolute"
                            :height           tabbar-height
                            :bottom           (c/bind-to-animated bottom 0)
                            :left             0
                            :right            0
                            :border-width     1
                            :border-color     "black"
                            :background-color "red"}}]))

;(defn second-scene-navbar [opacity width progress]
;  [animated-view {:style {:position         "absolute"
;                          ;:height 50
;                          :height           100
;                          :width            width
;                          :top              0
;                          :left             0
;                          :right            0
;                          :background-color "blue"
;                          :justify-content  "center"
;                          :align-items      "center"}}
;   [animated-text {:style {:font-size   16
;                           :font-weight "bold"
;                           :opacity     opacity
;                           :color       "white"
;                           :margin-left (* (- 1 progress) 250)}} "Second scene"]])

(defn second-scene-content [width height progress]
  (let [left (c/cell->
               progress
               (fn [progress]
                 (* (- 1 progress) width)))]
    [animated-view {:style {:position         "absolute"
                            :left             (c/bind-to-animated left width)
                            :height           height
                            :width            width
                            :padding-bottom   60
                            :justify-content  "center"
                            :background-color "white"
                            :align-items      "center"}}
     [text "sjkdfhkjsdbnkfjbsdf"]]))

(defn second-scene-tabbar [width height progress]
  (let [bottom (c/cell->
                 progress
                 (fn [progress]
                   (if (> progress 0.5)
                     (* -1 (- tabbar-height (* (- (* progress 2) 1) tabbar-height)))
                     (* -1 tabbar-height))))]
    [animated-view {:style {:position         "absolute"
                            :height           tabbar-height
                            :bottom           (c/bind-to-animated bottom (* -1 tabbar-height))
                            :left             0
                            :right            0
                            :border-width     1
                            :border-color     "black"
                            :background-color "pink"}}]))

(def first-s {:content first-scene-content
              :navbar  first-scene-navbar
              :tabbar  first-scene-tabbar})

(def second-s {:content second-scene-content
               :navbar  second-scene-navbar
               :tabbar  second-scene-tabbar})

(defn render-scenes [wrapper first-scene second-scene render-next render-back props]
  (let [progress (c/cell)
        result (into wrapper
                     (mapcat
                       identity
                       (for [[key _] first-scene]
                         [(apply (get first-scene key) (conj props progress render-next render-back))
                          (apply (get second-scene key) (conj props progress render-next render-back))])))]
    (render-next progress)
    result))
(def Dimensions
  (. ReactNative -Dimensions))

(defn get-dimensions-window []
  (-> (. Dimensions (get "window"))
      (js->clj :keywordize-keys true)))


(defn app-root []
  (let [dimensions (get-dimensions-window)
        width (:width dimensions)
        height (:height dimensions)
        start-time (js/Date.now)
        ;render-loop (fn []
        ;              (if (< @progress 1)
        ;                (js/requestAnimationFrame #(do
        ;                                            (swap! progress (fn [a] (+ a 0.16)))))
        ;                (do
        ;                  (swap! progress #(identity 1))
        ;                  (print (- (js/Date.now) start-time)))))
        render-next (fn [progress]
                      (js/setTimeout (fn anim-loop
                                       ([]
                                        (anim-loop 0))
                                       ([val]
                                        (js/requestAnimationFrame
                                          #(if (< val 1)
                                            (do
                                              (c/push progress val)
                                              (anim-loop (+ val 0.02)))
                                            (do
                                              (c/push progress 1)
                                              (print (- (js/Date.now) start-time))))
                                          )))
                                     300))
        render-back (fn [progress]
                      (js/setTimeout (fn anim-loop
                                       ([]
                                        (anim-loop 1))
                                       ([val]
                                        (js/requestAnimationFrame
                                          #(if (> val 0)
                                            (do
                                              (c/push progress val)
                                              (anim-loop (- val 0.016)))
                                            (do
                                              (c/push progress 0)
                                              (print (- (js/Date.now) start-time))))
                                          )))
                                     300))
        ]
    (fn []
      (render-scenes
        [view {:style (-> {:position   "relative"
                           :flex       1
                           :overflow   "hidden"
                           :width      width
                           :height     (- height 30)
                           :margin-top 30})}]
        first-s
        second-s
        render-next
        render-back
        [width height]))))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "ExperimentNavigation" #(r/reactify-component app-root)))
