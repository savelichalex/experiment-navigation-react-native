(ns experiment-navigation.ios.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [experiment-navigation.handlers]
            [experiment-navigation.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

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

(defn first-scene-navbar [opacity width progress]
  [view {:style {:position "absolute"
                 ;:height (+ (* (- 1 progress) 50) 50)
                 :height 100
                 :width width
                 :top 0
                 :left 0
                 :right 0
                 :background-color "blue"
                 :justify-content "center"
                 :align-items "center"}}
   [text {:style {:font-size 16
                  :font-weight "bold"
                  :opacity opacity
                  :color "white"
                  :margin-left (* -1 (* progress 250))}} "First scene"]])

(defn first-scene-content [left height width]
  [view {:style {:position "absolute"
                 :height height
                 :width width
                 :left left
                 :padding-bottom 60
                 :justify-content "center"
                 :background-color "grey"
                 :align-items "center"}}
   [text "JKHKJDBJKDB"]])

(defn first-scene-tabbar [height bottom]
  [view {:style {:position "absolute"
                 :height height
                 :bottom (* -1 bottom)
                 :left 0
                 :right 0
                 :border-width 1
                 :border-color "black"
                 :background-color "red"}}])

(defn second-scene-navbar [opacity width progress]
  [view {:style {:position "absolute"
                 ;:height 50
                 :height 100
                 :width width
                 :top 0
                 :left 0
                 :right 0
                 :background-color "blue"
                 :justify-content "center"
                 :align-items "center"}}
   [text {:style {:font-size 16
                  :font-weight "bold"
                  :opacity opacity
                  :color "white"
                  :margin-left (* (- 1 progress) 250)}} "Second scene"]])

(defn second-scene-content [left height width]
  [view {:style {:position "absolute"
                 :left left
                 :height height
                 :width width
                 :padding-bottom 60
                 :justify-content "center"
                 :background-color "white"
                 :align-items "center"}}
   [text "sjkdfhkjsdbnkfjbsdf"]])

(defn second-scene-tabbar [height bottom]
  [view {:style {:position "absolute"
                 :height height
                 :bottom (* -1 bottom)
                 :left 0
                 :right 0
                 :border-width 1
                 :border-color "black"
                 :background-color "pink"}}])

(def first-s {:content first-scene-content
              :navbar first-scene-navbar
              :tabbar first-scene-tabbar})

(def second-s {:content second-scene-content
               :navbar second-scene-navbar
               :tabbar second-scene-tabbar})

;(defn navbar-transition [first-navbar second-navbar [width height progress]]
;  (if (< progress 0.5)
;    [first-navbar (- 1 (* progress 2)) width]
;    [second-navbar (- (* progress 2) 1) width]))

(defn navbar-transition [first-navbar second-navbar [width height progress]]
  (if (< progress 0.5)
    [first-navbar (- 1 (* progress 2)) width (* progress 2)]
    [second-navbar (- (* progress 2) 1) width (- (* progress 2) 1)]))

(defn content-transition [first-content second-content [width height progress]]
  (cond
    (= 0 progress) [first-content 0 height width]
    (= 1 progress) [second-content 0 height width]
    :else [view
           [first-content 0 height width]
           [second-content (* (- 1 progress) width) height width]]))

(defn tabbar-transition [first-tabbar second-tabbar [width height progress]]
  (let [tabbar-height 60]
    (if (< progress 0.5)
      [first-tabbar
       tabbar-height
       (- tabbar-height (* (- 1 (* progress 2)) tabbar-height))]
      [second-tabbar
       tabbar-height
       (- tabbar-height (* (- (* progress 2) 1) tabbar-height))])))

(def first-to-second {:navbar navbar-transition
                      :content content-transition
                      :tabbar tabbar-transition})

(defn render-scenes [wrapper first-scene second-scene relation props]
  (into wrapper
        (for [[key _] first-scene]
          (apply
            (get relation key)
            [(get first-scene key)
             (get second-scene key)
             props]))))
(def Dimensions
  (. ReactNative -Dimensions))

(defn get-dimensions-window []
  (-> (. Dimensions (get "window"))
      (js->clj :keywordize-keys true)))


(defn app-root []
  (let [dimensions (get-dimensions-window)
        width (:width dimensions)
        height (:height dimensions)
        progress (r/atom 0)
        start-time (js/Date.now)
        render-loop (fn []
                      (if (< @progress 1)
                        (js/requestAnimationFrame #(do
                                                    (swap! progress (fn [a] (+ a 0.16)))))
                        (do
                          (swap! progress #(identity 1))
                          (print (- (js/Date.now) start-time)))))]
    (fn []
      (render-loop)
      (render-scenes
        [view {:style (-> {:position "relative"
                           :flex 1
                           :overflow "hidden"
                           :width width
                           :height (- height 30)
                           :margin-top 30})}]
        first-s
        second-s
        first-to-second
        [width height @progress]))))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "ExperimentNavigation" #(r/reactify-component app-root)))
