(ns recrypt.crypto
  (:require [geheimnis.aes :refer [encrypt decrypt]]
            [goog.crypt]))

(defn- random-bytes
  "Generates a random sequence of n bytes from 0-255"
  [n]
  (letfn [(random-byte [] (rand-nth (range 256)))]
    (take n (repeatedly random-byte))))

(defn ^:dummy-only new-encryption-key
  "Generate some random string to stand in for a real encryption
  key for proof of concept only. This *must* be replaced by a
  proper encryption key."
  []
  (goog.crypt/utf8ByteArrayToString
   (clj->js (random-bytes 32))))

(defn new-initialization-vector
  "Produces a new 16 byte initialization vector,
  using a secure random number generator from the
  Crypto API, if supported by the browser."
  []
  (if-not js.window.crypto
    (clj->js (random-bytes 16))
    (let [arr (js/Uint8Array. 16)]
      (js/window.crypto.getRandomValues arr)
      arr)))

(defn encrypt-bytes
  "Wraps geheimnis encrypt (goog.crypt.Aes), requiring an explicit IV"
  [plain-bytes encryption-key iv]
  (encrypt encryption-key plain-bytes :iv iv))

(defn decrypt-bytes
  "Wraps geheimnis decrypt (goog.crypt.Aes), requiring an explicit IV"
  [cipher-bytes encryption-key iv]
  (decrypt encryption-key cipher-bytes :iv iv))

(defn plaintext->encrypted-bytes
  [plaintext encryption-key iv]
  (-> plaintext
      goog.crypt/stringToUtf8ByteArray
      (encrypt-bytes encryption-key iv)))

(defn encrypted-bytes->plaintext
  [cipher-bytes encryption-key iv]
  (-> cipher-bytes
      (decrypt-bytes encryption-key  iv)
      goog.crypt/utf8ByteArrayToString))
