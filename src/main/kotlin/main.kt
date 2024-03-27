fun main() {
    val noteService = NoteService()

    // Добавление заметки
    noteService.add(ownerId = 1, title = "Влог", text = "Мой день")
    println("Добавлена заметка: ${noteService.getById(1)}")

    noteService.add(ownerId = 2, title = "Влог", text = "Планы на завтра")
    println("Добавлена заметка: ${noteService.getById(2)}")

    // Создание комментария к заметке
    noteService.createComment(noteId = 1, ownerId = 1, message = "Мощно")
    println("Добавлен комментарий: ${noteService.getComments(1).last().message}")
    noteService.createComment(noteId = 2, ownerId = 1, message = "Класс!")
    println("Добавлен комментарий: ${noteService.getComments(2).last().message}")

    // Удаление заметки
    noteService.delete(noteId = 1)
    println("Заметка с id = 1 удалена")

    // Удаление комментария
    noteService.deleteComment(commentId = 1)
    println("Комментарий с id = 1 удален")

    // Редактирование заметки
    noteService.edit(noteId = 2, title = "Новый заголовок", text = "Новый текст")
    println("Заметка с id = 2 отредактирована: ${noteService.getById(2)}")

    // Редактирование комментария
    val editedMessage = "Новый текст комментария"
    noteService.editComment(commentId = 2, ownerId = 1, message = editedMessage)
    val editedComment = noteService.getCommentById(2) // Получаем отредактированный комментарий
    println("Комментарий с id = 2 отредактирован: $editedMessage")

    // Получение списка заметок пользователя
    val userNotes = noteService.get(userId = 2)
    println("Заметки пользователя с id = 2:")
    userNotes.forEach { println(it) }

    // Получение конкретной заметки пользователя
    val note = noteService.getById(noteId = 2)
    println("Заметка с id = 2: $note")

    // Получение списка комментариев к заметке пользователя
    val comments = noteService.getComments(noteId = 2)
    println("Комментарии к заметке с id = 2:")
    comments.forEach { println(it.message) }

    // Восстановление удалённого комментария
    noteService.restoreComment(commentId = 1)
    println("Комментарий с id = 1 восстановлен")
}

class Comment(
    val id: Int,
    val noteId: Int,
    val ownerId: Int,
    var message: String,
    var isDeleted: Boolean = false
)

class NoteService {
    val notes = mutableListOf<MutableMap<String, Any>>()
    val comments = mutableListOf<Comment>()
    private var noteIdCounter = 1 // Счетчик для генерации id заметок
    private var commentIdCounter = 1 // Счетчик для генерации id комментариев

    fun add(ownerId: Int, title: String, text: String) {
        val note = mutableMapOf<String, Any>(
            "id" to noteIdCounter++,
            "ownerId" to ownerId,
            "title" to title,
            "text" to text,
            "isDeleted" to false
        )
        notes.add(note)
    }

    fun createComment(noteId: Int, ownerId: Int, message: String) {
        if (!noteExists(noteId)) {
            throw IllegalArgumentException("Заметки с id $noteId не существует")
        }
        comments.add(Comment(id = commentIdCounter++, noteId = noteId, ownerId = ownerId, message = message))
    }

    fun delete(noteId: Int) {
        val note = getNoteById(noteId)
        note?.set("isDeleted", true)
    }

    fun deleteComment(commentId: Int) {
        val comment = getCommentById(commentId)
        comment?.isDeleted = true
    }

    fun edit(noteId: Int, title: String, text: String) {
        val note = getNoteById(noteId)
        if (note != null) {
            note["title"] = title
            note["text"] = text
        }
    }

    fun editComment(commentId: Int, ownerId: Int, message: String) {
        val comment = getCommentById(commentId)
        if (comment == null || comment.ownerId != ownerId) {
            throw IllegalArgumentException("Заметки с id $commentId не существует")
        } else {
            comment.message = message
        }
    }

    fun get(userId: Int): List<Map<String, Any>> {
        val userNotes = mutableListOf<Map<String, Any>>()
        for (note in notes) {
            if (note["ownerId"] == userId && !(note["isDeleted"] as Boolean)) {
                userNotes.add(note)
            }
        }
        return userNotes
    }

    fun getById(noteId: Int): Map<String, Any>? {
        for (note in notes) {
            if (note["id"] == noteId) {
                return note
            }
        }
        return null
    }

    fun getComments(noteId: Int): List<Comment> {
        val noteComments = mutableListOf<Comment>()
        for (comment in comments) {
            if (comment.noteId == noteId && !comment.isDeleted) {
                noteComments.add(comment)
            }
        }
        return noteComments
    }

    fun restoreComment(commentId: Int) {
        val comment = getCommentById(commentId)
        comment?.isDeleted = false
    }

    private fun noteExists(noteId: Int): Boolean {
        for (note in notes) {
            if (note["id"] == noteId && !(note["isDeleted"] as Boolean)) {
                return true
            }
        }
        return false
    }

    private fun getNoteById(noteId: Int): MutableMap<String, Any>? {
        for (note in notes) {
            if (note["id"] == noteId) {
                return note
            }
        }
        return null
    }

    fun getCommentById(commentId: Int): Comment? {
        for (comment in comments) {
            if (comment.id == commentId) {
                return comment
            }
        }
        return null
    }

}
