(ns manenko.boot-zip
  {:boot/export-tasks true}
  (:require [boot.core       :as boot]
            [boot.util       :as util]
            [clojure.java.io :as io]
            [clojure.string  :as str])
  (:import java.io.File
           java.nio.file.attribute.PosixFilePermissions
           [java.nio.file Files FileSystems]
           [org.apache.commons.compress.archivers.zip ZipArchiveEntry ZipFile]))

(defn ^:private octal->permission-str
  "Converts a number that represents POSIX file permissions for the
  single permission triad (owner, group, users)."
  [o]
  (condp = o
    0 "---"
    1 "--x"
    2 "-w-"
    3 "-wx"
    4 "r--"
    5 "r-x"
    6 "rw-"
    7 "rwx"))


(defn ^:private number->permissions-str
  "Converts a number that represents POSIX file permissions into the
  string representation.

  For example:

  777 -> rwxrwxrwx
  612 -> rw---x-w-
  444 -> r--r--r--"
  [n]
  (let [n (bit-and n 0777)]
    (str/join (map octal->permission-str
                   [(bit-shift-right n 6)
                    (bit-and (bit-shift-right n 3) 007)
                    (bit-and n 007)]))))


(defn ^:private posix?
  "Checks if the default file system is POSIX compliant.

  This is needed when we set file permissions: this is done only of
  the file system is POSIX compliant."
  []
  (.contains (.supportedFileAttributeViews (FileSystems/getDefault)) "posix"))


(defn ^:private set-file-permissions!
  "Sets POSIX file permissions for the given file.

  Permissions have standard string representation (chmod):

  rw-r--r--
  rw-r--r--
  rwxr-xr-x

  etc."
  [^File f ^String p]
  (when (posix?)
    (Files/setPosixFilePermissions (.toPath f) (PosixFilePermissions/fromString p))))


(defn ^:private unzip-entry
  "Extracts ZIP entry (a file or directory) creating all directories
  and setting Unix permissions (if run on POSIX compliant file
  system)."
  [^ZipFile zip-file ^ZipArchiveEntry zip-entry output-dir]
  (let [permissions (number->permissions-str (.getUnixMode zip-entry))
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
    (set-file-permissions! output-file permissions)))


(defn ^:private unzip
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
      (util/dbug "Extracting %s to %s...\n" archive output-dir)
      (let [output-dir (io/file tmp (or output-dir ""))
            archive    (boot/tmp-file (boot/tmp-get fileset archive))]
        (io/make-parents output-dir)
        (unzip archive output-dir)
        (boot/commit! (boot/add-asset fileset tmp))))))
