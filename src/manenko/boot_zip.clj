(ns manenko.boot-zip
  {:boot/export-tasks true}
  (:require [boot.core :refer [deftask]]))


(deftask compress-into-zip
  "NOT IMPLEMENTED YET"
  [])


(deftask extract-from-zip
  "Extracts the given ZIP archive into the given directory preserving the Unix permissions.

If the path to the output directory was not provided (or is empty)
then extracts the archive into the project's root."
  [a archive    VAL str "Location of the ZIP archive that has to be extracted. Required."
   o output-dir VAL str "Path to the output directory. Optional."]
  identity)

