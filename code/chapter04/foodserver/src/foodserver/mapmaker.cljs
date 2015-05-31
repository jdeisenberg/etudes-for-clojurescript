(ns foodserver.mapmaker)

(def xml (js/require "node-xml-lite"))

;; forward reference
(declare process-child)

(defn process-children
  "Process an array of child nodes, with a current food name
  and accumulate a result"
  [[food result] children]
  (let [[final-food final-map] (reduce process-child [food result] children)]
    [final-food final-map]))

(defn add-condiment
  "Add food to the vector of foods that go with this condiment"
  [result food condiment]
  (let [food-list (get result condiment)
        new-list (if food-list (conj food-list food) [food])]
    (assoc result condiment new-list)))

(defn process-child
  "Given a current food and result map, and an item,
  return the new food name and result map"
  [[food result] item]

  ;; The first child of an element is text - either a food name
  ;; or a condiment name, depending on the element name.
  (let [firstchild (first (.-childs item))]
    (cond
      (= (.-name item) "display_name") (vector firstchild result)
      (.test #"cond_._name" (.-name item))
      (vector food (add-condiment result food firstchild))
      (and (.-childs item) (.-name firstchild))
      (process-children [food result] (.-childs item))
      :else [food result])))

(defn foodmap [filename]
  (let [docmap (.parseFileSync xml filename)]
    (last (process-children ["" {}] (.-childs docmap)))))
