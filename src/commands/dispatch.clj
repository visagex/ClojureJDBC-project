(ns commands.dispatch)

(defmulti handle-command
          (fn [{:keys [cmd args]}] [(keyword cmd) (keyword (first args))]))
