(ns manenko.boot-zip
  {:boot/export-tasks true}
  (:require [boot.core               :as boot]
            [boot.util               :as util]
            [clojure.java.io         :as io]
            [manenko.compression.zip :as compression]))


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
        (compression/unzip archive output-dir)
        (boot/commit! (boot/add-asset fileset tmp))))))
