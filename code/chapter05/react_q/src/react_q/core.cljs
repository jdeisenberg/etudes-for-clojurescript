(ns react_q.core
  (:require [clojure.browser.repl :as repl]
            [quiescent.core :as q]
            [quiescent.dom :as d]
            [quiescent.dom.uncontrolled :as du]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(defonce status
         (atom {:w 0 :h 0 :proportional true
                :border-width "3" :border-style "none"
                :orig-w 0 :orig-h 0 :src "clock.jpg"}))

(enable-console-print!)

(defonce border-style-list '("none" "solid" "dotted" "dashed"
                              "double" "groove" "ridge"
                              "inset" "outset"))
(defn resize
  "Resize the image; if proportional, determine which field
  has changed and change the other accordingly."
  [evt]
  (let [{:keys [w h proportional orig-w orig-h]} @status
        target (.-target evt)
        id (.-id target)
        val (.-value target)]
    (if proportional
      (cond
        (= id "w") (swap! status assoc :w val :h (int (* (/ val orig-w) orig-h)))
        (= id "h") (swap! status assoc :h val :w (int (* (/ val orig-h) orig-w)))
        :else (swap! status assoc :h orig-h :w orig-w))
      (swap! status assoc (keyword id) (.-value target)))))

(defn recheck
  "Handle the checkbox. Since the checked property isn't the
  value of the checkbox, I had to set the property by hand"
  [evt]
  (let [new-checked (not (:proportional @status))]
    (swap! status assoc :proportional new-checked)
    (set! (.-checked (.-target evt)) new-checked)))

(defn change-border [evt]
  (let [{:keys [border-width border-style]} @status
        target (.-target evt)
        id (.-id target)
        val (.-value target)]
    (cond
      (= id "menu") (swap! status assoc :border-style val)
      (= id "bw") (swap! status assoc :border-width val))))

(defn set-dimensions
  "Set dimensions of the image once it loads"
  [evt]
  (let [node (.getElementById js/document "image")
        id (.-id node)]
    (swap! status assoc :orig-w (.-naturalWidth node)
           :orig-h (.-naturalHeight node)
           :w (.-naturalWidth node) :h (.-naturalHeight node))))

(q/defcomponent Image
                "A component that displays an image"
                :name "ImageWidget"
                [status]
                (d/img {:id "image"
                        :src (:src status)
                        :width (:w status)
                        :height (:h status)
                        :style {:float "right"
                                :borderWidth (:border-width status)
                                :borderColor "red"
                                :borderStyle (:border-style status)}
                        :onLoad set-dimensions
                        }))

(q/defcomponent Option
                [item]
                (d/option {:value item} item))

(q/defcomponent Form
                "Input form"
                :name "FormWidget"
                :on-mount (fn [node val]
                            (set! (.-checked (.getElementById js/document "prop"))
                                  (:proportional val)))
                [status]
                (d/form {:id "params"}
                        "Width: "
                        (d/input {:type "text" :size "5" :value (:w status)
                                   :id "w"
                                  :onChange resize})
                        "Height: "
                        (d/input {:type "text" :size "5":value (:h status)
                                   :id "h"
                                  :onChange resize})
                        (d/br)
                        (du/input {:type "checkbox"
                                   :id "prop"
                                  :onChange recheck
                                  :value "proportional"})
                        "Preserve Proportions"
                        (d/br)
                        "Border: "
                        (d/input {:type "text" :size "5"
                                  :value (:border-width status)
                                  :id "bw"
                                  :onChange change-border})
                        "px "
                        (apply d/select {:id "menu" :onChange change-border}
                               (map Option border-style-list))))

(q/defcomponent Interface
                "User Interface"
                :name "Interface"
                [status]
                (d/div {}
                  (Image status)
                  (Form status)))

(defn render
  "Render the current state atom, and schedule a render on the next
  frame"
  []
  (q/render (Interface @status) (.getElementById js/document "interface"))
  (.requestAnimationFrame js/window render))

(render)
