(ns manenko.permissions.posix
  (:require [clojure.string :as str])
  (:import  [java.nio.file           Files FileSystems]
            [java.io                 File]
            [java.nio.file.attribute PosixFilePermissions]))


(defn octal-triad->symbolic-triad
  "Converts an octal number that represents POSIX file permissions for
  the single permission triad (owner, group, users) to symbolic
  notation."
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


(defn octal->symbolic
  "Converts the given POSIX file permissions in octal notation to symbolic notation.

  For example:

  777 -> rwxrwxrwx
  612 -> rw---x-w-
  444 -> r--r--r--"
  [n]
  (let [n (bit-and n 0777)]
    (str/join (map octal-triad->symbolic-triad
                   [(bit-shift-right n 6)
                    (bit-and (bit-shift-right n 3) 007)
                    (bit-and n 007)]))))


(defn posix?
  "Checks if the default file system is POSIX compliant.

  This is needed when we set file permissions: this is done only of
  the file system is POSIX compliant."
  []
  (.contains
   (.supportedFileAttributeViews (FileSystems/getDefault))
   "posix"))


(defn set-file-permissions!
  "Sets POSIX file permissions for the given file.

  Permissions are in symbolic notation:

  rw-r--r--
  rw-r--r--
  rwxr-xr-x"
  [^File f ^String p]
  (when (posix?)
    (Files/setPosixFilePermissions
     (.toPath f)
     (PosixFilePermissions/fromString p))))
