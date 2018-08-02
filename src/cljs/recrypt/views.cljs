(ns recrypt.views
  (:require
   [re-frame.core :as re-frame]
   [recrypt.subs :as subs]
   [geheimnis.aes :refer [encrypt decrypt]]
   [goog.crypt]))

(defn random-bytes
  [n]
  (letfn [(random-byte [] (rand-nth (range 256)))]
    (take n (repeatedly random-byte))))

(defn new-encryption-key
  []
  (goog.crypt/utf8ByteArrayToString
   (clj->js (random-bytes 32))))

;; how should we acquire the data encryption key?
;; multiple site users should be able to acquire the key used for encrypting data
;; should we vary the key per site trial? (by tagging the key in KMS with site trial id, for instance)

;; the iv could (should?) be different for each site-trial-patient
;; (could it be stored with the stp much like salts are stored with hashed passwords?)

(defn main-panel
  []
  (let [name (re-frame/subscribe [::subs/name])
        key  (new-encryption-key)
        iv   (clj->js (random-bytes 16))]
    [:div
     [:h1 (str "Hello from "
               (as-> @name $
                 (goog.crypt/stringToUtf8ByteArray $)
                 (encrypt key $ :iv iv)
                 (decrypt key $ :iv iv)
                 (goog.crypt/utf8ByteArrayToString $)))]]))
