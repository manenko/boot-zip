(ns manenko.boot-zip
  {:boot/export-tasks true}
  (:require [boot.core                 :as boot]
            [boot.util                 :as util]
            [clojure.java.io           :as io]
            [manenko.permissions.posix :as posix-permissions])
  (:import [org.apache.commons.compress.archivers.zip ZipArchiveEntry ZipFile]
           [java.io                                   File]))

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


(defn ^:private unzip
  "Extracts the given ZIP archive into the given directory while
  preserving the Unix permissions."
  [archive output-dir]
  (with-open [zip-file (ZipFile. archive)]
    (doseq [zip-entry (enumeration-seq (.getEntries zip-file))]
      (unzip-entry zip-file zip-entry output-dir))))


(boot/deftask compress-into-zip
  "NOT IMPLEMENTED YET"
  [])


(boot/deftask extract-from-zip
  "Extracts the given ZIP archive into the given directory preserving the Unix permissions.

If the path to the output directory was not provided (or is empty)
then extracts the archive into the project's root."
  [a archive    VAL str "Location of the ZIP archive that has to be extracted. Required."
   o output-dir VAL str "Path to the output directory. Optional."]
  (let [tmp (boot/tmp-dir!)]
    (boot/with-pre-wrap fileset
      (util/dbug "Extracting %s to %s...\n" archive output-dir)
      (let [output-dir (io/file tmp (or output-dir ""))
            archive    (boot/tmp-file (boot/tmp-get fileset archive))]
        (io/make-parents output-dir)
        (unzip archive output-dir)
        (boot/commit! (boot/add-asset fileset tmp))))))
