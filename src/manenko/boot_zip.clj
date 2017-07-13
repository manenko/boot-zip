(ns manenko.boot-zip
  {:boot/export-tasks true}
  (:require [boot.core :as boot]
            [clojure.java.io :as io])
  (:import java.io.File
           [org.apache.commons.compress.archivers.zip ZipArchiveEntry ZipFile]))

(defn ^:private unzip-entry [^ZipFile zip-file ^ZipArchiveEntry zip-entry output-dir]
  (if (.isDirectory zip-entry)
    (.mkdirs (io/file output-dir (.getName zip-entry)))
    (do
      (let [output-file (io/file output-dir (.getName zip-entry))
            unix-permission (.getUnixMode zip-entry)]
        (io/make-parents output-file)
        (with-open [in  (io/input-stream (.getInputStream zip-file zip-entry))
                    out (io/output-stream output-file)]
          (io/copy in out))))))

(defn unzip
  "Extracts the given ZIP archive into the given directory while
  preserving the Unix permissions."
  [^File archive ^File output-dir]
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
      (let [output-dir (io/file tmp (or output-dir ""))
            archive    (boot/tmp-file (boot/tmp-get fileset archive))]
        (io/make-parents output-dir)
        (unzip archive output-dir)
        (boot/commit! (boot/add-asset fileset tmp))))))

