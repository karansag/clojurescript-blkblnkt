(ns simCljs.app
  (:require 
   [clojure.browser.repl :as repl]
   [goog.net.Jsonp :as goog.net.Jsonp]
   [goog.array :as goog.array]
   [jayq.core :as jq]
   ))
	
;;(if (.has js/_ js/window "clojure") 
  (repl/connect "http://localhost:9000/repl")
  ;;)

(defn photoSrcs [elem]
  (if (and (.isArray js/_ (aget elem "photos")) (> (.-length (aget elem "photos")) 0))
    (goog.array.map (aget elem "photos") photoSrcs)
    (aget elem "photo-url-1280")))

(def myTumblrData (atom (js/Array)))


(defn insertPhotos [srcArray]
  (let [each (aget js/_ "each")]
    (each srcArray
      (fn [src i arr]
        (let [$img (jq/$ (apply str ["<img src=\"" src "\" />"]))
          insertPhoto #(-> $img (jq/append-to :body))
          ms-delay 1000]
          (js/setTimeout insertPhoto (* i ms-delay)))))))

(defn handle-json-resp [resp]
  (do
    (js/alert "Cool! Loaded my 50 most recent tumblr photo posts.")
    (.log js/console resp)
    (.log js/console (swap! myTumblrData
             (fn [_] (if (aget resp "posts")
                      (goog.array.flatten (goog.array.map (aget resp "posts") photoSrcs))))))
    (insertPhotos @myTumblrData)))

(defn get-tumblr-data []
  (let [jsonp (goog.net.Jsonp. "http://simloovoo.tumblr.com/api/read/json")]
    (.send jsonp (js-obj  "type" "photo" "num" 50 "start" 0) handle-json-resp #() "cacheplz")))

(def clickable (.getElementById js/document "go"))

(.addEventListener clickable "click" get-tumblr-data)

(defn ^:export showData [] @myTumblrData)

(defn ^:export externTest []
  (.attr (js/$ "#go") "data-sim"))