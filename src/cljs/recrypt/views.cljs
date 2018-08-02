(ns recrypt.views
  (:require
   [re-frame.core :as re-frame]
   [recrypt.subs :as subs]
   [recrypt.crypto :as crypto]))


;; how should we acquire the data encryption key?
;; multiple site users should be able to acquire the key used for encrypting data
;; should we vary the key per site trial? (by tagging the key in KMS with site trial id, for instance)

;; the iv could (should?) be different for each site-trial-patient
;; (could it be stored with the stp much like salts are stored with hashed passwords?)

(defn main-panel
  []
  (let [name (re-frame/subscribe [::subs/name])
        key  (crypto/new-encryption-key)
        iv   (crypto/new-initialization-vector)]
    [:div
     [:h1 (str "Hello from "
               (-> @name
                 (crypto/plaintext->encrypted-bytes key iv)
                 (crypto/encrypted-bytes->plaintext key iv)))]]))
