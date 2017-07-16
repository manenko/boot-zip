(ns manenko.boot-zip
  {:boot/export-tasks true}
  (:require [boot.core               :as boot]
            [boot.util               :as util]
            [clojure.java.io         :as io]
            [manenko.compression.zip :as compression]))


(defn ^:private by-directory
  [directory files]
  ((boot/file-filter #(fn [f] (.startsWith (.getPath f) %))) [directory] files false))


(boot/deftask compress-into-zip
  "Compresses the content of the given directory and writes it to the given ZIP file preserving Unix permissions.

The task will not put the folder itself to the archive, only its
children."
  [i input-dir VAL str "Location of the directory that should be compressed."
   a archive   VAL str "Location of the output ZIP archive."]
  (let [tmp (boot/tmp-dir!)]
    (boot/with-pre-wrap fileset
      (util/info "Compressing %s to %s...\n" input-dir archive)
      (let [input-files (by-directory input-dir (boot/ls fileset))
            output-file (io/file tmp archive)]
        (compression/zip input-dir input-files output-file)
        (boot/commit! (boot/add-asset fileset tmp))))))


(boot/deftask extract-from-zip
  "Extracts the given ZIP archive into the given directory preserving the Unix permissions.

If the path to the output directory was not provided (or is empty)
then extracts the archive into the project's root."
  [a archive    VAL str "Location of the ZIP archive that has to be extracted. Required."
   o output-dir VAL str "Path to the output directory. Optional."]
  (let [tmp (boot/tmp-dir!)]
    (boot/with-pre-wrap fileset
      (util/info "Extracting %s to %s...\n" archive output-dir)
      (let [output-dir (io/file tmp (or output-dir ""))
            archive    (boot/tmp-file (boot/tmp-get fileset archive))]
        (io/make-parents output-dir)
        (compression/unzip archive output-dir)
        (boot/commit! (boot/add-asset fileset tmp))))))
