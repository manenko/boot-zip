(ns manenko.compression.zip
  (:require [boot.util                 :as util]
            [clojure.java.io           :as io]
            [manenko.permissions.posix :as posix-permissions])
  (:import [org.apache.commons.compress.archivers.zip ZipFile]))


(defn ^:private unzip-entry
  "Extracts ZIP entry (a file or directory) creating all directories
  and setting Unix permissions (if run on a POSIX compliant file
  system)."
  [zip-file zip-entry output-dir]
  (let [permissions (posix-permissions/octal->symbolic (.getUnixMode zip-entry))
        entry-name  (.getName zip-entry)
        output-file (io/file output-dir entry-name)]
    (util/dbug "%s %s...\n" permissions entry-name)
    (if (.isDirectory zip-entry)
      (.mkdirs output-file)
      (do
        (io/make-parents output-file)
        (with-open [in  (io/input-stream (.getInputStream zip-file zip-entry))
                    out (io/output-stream output-file)]
          (io/copy in out))))
    (posix-permissions/set-file-permissions! output-file permissions)))


(defn unzip
  "Extracts the given ZIP archive into the given directory while
  preserving the Unix permissions."
  [archive output-dir]
  (with-open [zip-file (ZipFile. archive)]
    (doseq [zip-entry (enumeration-seq (.getEntries zip-file))]
      (unzip-entry zip-file zip-entry output-dir))))


(defn zip
  [input-files output-file]
  )
