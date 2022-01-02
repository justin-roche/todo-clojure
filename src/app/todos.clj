(ns app.todos
  (:require
   [app.db :refer [insert-subdocument update-subdocument find-subdocument]]
   [monger.operators :as mo :refer :all]
   [monger.conversion :as mcv]
   [aprint.core :refer [aprint]]
   [monger.result :refer :all]
   [app.user :as user]))

(defn _get-todo [username todo-id]
  (first (:todos (find-subdocument
                  "users"
                  {"name" username "todos" {"$elemMatch" {"id" todo-id}}}
                  {"name" 1 "todos.$" 1}))))

(defn create-todo [user todo]
  (let [username (get-in user [:name])
        default-todo {:status "incomplete"
                      :visibility "visible"
                      :createdAt (new java.util.Date)}
        todo (merge default-todo todo)]
    (if-let [inserted-id (insert-subdocument "users"
                                             {:name username}
                                             "todos" todo)]
      {:status 200 :body {:data (_get-todo username inserted-id)}}
      {:status 409})))

(defn change-todo-status [{:keys [name]} todo-id]
  (let [todo (_get-todo name todo-id)
        set-value (if (= (:status todo) "complete")
                    {"todos.$.status" "incomplete"
                     "todos.$.completedAt" nil}
                    {"todos.$.status" "complete"
                     "todos.$.completedAt" (new java.util.Date)})]
    (if-let [updatedTodo (and (update-subdocument "users"
                                                  {:name name "todos.id" todo-id}
                                                  set-value)
                              (_get-todo name todo-id))]
      {:status 200 :body {:data updatedTodo}}
      {:status 409})))

(defn delete-todo [user todo-id]
  (let [name (get-in user [:name])]
    (if (update-subdocument "users"
                            {:name name "todos.id" todo-id}
                            {"todos.$.visibility" "deleted"
                             "todos.$.deletedAt" (new java.util.Date)})
      {:status 200}
      {:status 409})))

(defn _filter-visible-todos [todos]
  (filter #(not (= (:visibility %) "deleted")) todos))

(defn get-todos [{:keys [todos]}]
  (if-let [todos (_filter-visible-todos todos)]
    {:status 200 :body {:data todos}}
    {:status 200 :body {:data []}}))

(defn get-completion-report [{:keys [todos]}]
  (let [grouped (group-by #(:status %) (_filter-visible-todos todos))]
    {:status 200 :body {:data (if (not-empty grouped) grouped
                                  {:complete []
                                   :incomplete []})}}))

(defn _reduce-events [acc todo]
  (let [creation  {:eventDate (:createdAt todo) :id (:id todo) :type "creation"}]
    (cond
      (contains? todo :deletedAt)
      (concat acc [creation {:eventDate (:deletedAt todo) :id (:id todo) :type "deletion"}])
      (contains? todo :completedAt)
      (concat acc [creation {:eventDate (:completedAt todo) :id (:id todo) :type "completion"}])
      :else (concat acc [creation]))))

(defn get-burn-down-report [{:keys [todos]}]
  (let [events (reduce _reduce-events [] todos)
        sorted (sort #(compare (:eventDate %1) (:eventDate %2)) events)]
    {:status 200 :body {:data sorted}}))
