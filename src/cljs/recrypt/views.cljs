(ns recrypt.views
  (:require
   [re-frame.core :as re-frame]
   [recrypt.subs :as subs]
   [recrypt.crypto :as crypto]))

;; Amazon KMS would manage the master key
;; A call to a backend API would return the decrypted data keys for a batch of entities
;; The UI would decrypt entity fields using the associated data key. The IV is returned
;; from KMS along with the data key material.

(defn main-panel
  "Demonstrate round-tripping values through AES encryption"
  []
  (let [name (re-frame/subscribe [::subs/name])
        key  (crypto/new-encryption-key)
        iv   (crypto/new-initialization-vector)]
    [:div
     [:h1 (str "This was encrypted and decrypted with AES: "
               (-> @name
                 (crypto/plaintext->encrypted-bytes key iv)
                 (crypto/encrypted-bytes->plaintext key iv)))]]))
