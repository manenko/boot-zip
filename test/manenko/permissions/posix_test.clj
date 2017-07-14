(ns manenko.permissions.posix-test
  (:require [clojure.test              :refer [deftest is]]
            [manenko.permissions.posix :as    posix]))

(deftest test-octal->symbolic
  (is (= "rwxrwxrwx" (posix/octal->symbolic 0777)))
  (is (= "rw---x-w-" (posix/octal->symbolic 0612)))
  (is (= "r--r--r--" (posix/octal->symbolic 0444))))

(deftest test-symbolic->octal
  (is (= 0777 (posix/symbolic->octal "rwxrwxrwx")))
  (is (= 0612 (posix/symbolic->octal "rw---x-w-")))
  (is (= 0444 (posix/symbolic->octal "r--r--r--")))
  (is (= 0000 (posix/symbolic->octal "---------"))))

