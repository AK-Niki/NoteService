import org.junit.Test
import org.junit.Assert.*

class NoteServiceTest {
    @Test
    fun `add note`() {
        val noteService = NoteService()
        val initialSize = noteService.notes.size

        noteService.add(ownerId = 1, title = "Title", text = "Text")

        assertEquals(initialSize + 1, noteService.notes.size)
    }

    @Test
    fun `create comment`() {
        val noteService = NoteService()
        val noteId = "1"
        val ownerId = 1
        val message = "Test comment"

        noteService.add(ownerId = ownerId, title = "Title", text = "Text")
        val initialSize = noteService.comments.size

        noteService.createComment(1, ownerId, message)

        assertEquals(initialSize + 1, noteService.comments.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throw exception comment`() {
        val noteService = NoteService()
        val noteId = "1"
        val ownerId = 1
        val message = "Test comment"

        noteService.createComment(1, ownerId, message)
    }

    @Test
    fun `delete note`() {
        val noteService = NoteService()
        val noteId = "1"
        noteService.add(ownerId = 1, title = "Title", text = "Text")

        noteService.delete(1)

        assertTrue(noteService.notes.none { it["id"] == noteId })
    }

    @Test
    fun `edit comment`() {
        val noteService = NoteService()
        val commentId = 1
        val ownerId = 1
        val newMessage = "New Message"
        noteService.add(ownerId = 1, title = "Title", text = "Text")
        noteService.createComment(1, ownerId, "Test comment")

        noteService.editComment(commentId, ownerId, newMessage)
        val editedComment = noteService.comments.find { it.id == commentId }

        assertEquals(newMessage, editedComment?.message)
    }

    @Test
    fun `get notes user id`() {
        val noteService = NoteService()
        val userId = 1
        noteService.add(ownerId = userId, title = "Title 1", text = "Text 1")
        noteService.add(ownerId = userId, title = "Title 2", text = "Text 2")
        noteService.add(ownerId = 2, title = "Title 3", text = "Text 3")

        val userNotes = noteService.get(userId)

        assertEquals(2, userNotes.size)
        assertTrue(userNotes.all { it["ownerId"] == userId })
    }

    @Test
    fun `get comments for note`() {
        val noteService = NoteService()
        val noteId = 1
        val ownerId = 1
        noteService.add(ownerId = ownerId, title = "Title", text = "Text")
        noteService.createComment(1, ownerId, "Comment 1")
        noteService.createComment(1, ownerId, "Comment 2")

        val comments = noteService.getComments(noteId)

        assertEquals(2, comments.size)
        assertTrue(comments.all { it.noteId == noteId })
    }

    @Test
    fun `restore deleted comment`() {
        val noteService = NoteService()
        val commentId = 1
        noteService.add(ownerId = 1, title = "Title", text = "Text")
        noteService.createComment(1, 1, "Test comment")
        noteService.deleteComment(commentId)

        noteService.restoreComment(commentId)
        val restoredComment = noteService.comments.find { it.id == commentId }

        assertNotNull(restoredComment)
        assertFalse(restoredComment?.isDeleted ?: true)
    }

    @Test
    fun `get note by id`() {
        val noteService = NoteService()
        val ownerId = 1
        val title = "Влог"
        val text = "Мой день"

        noteService.add(ownerId, title, text)
        val note = noteService.getById(1)

        assertNotNull(note)
        assertEquals(1, note?.get("id"))
        assertEquals(ownerId, note?.get("ownerId"))
        assertEquals(title, note?.get("title"))
        assertEquals(text, note?.get("text"))
        assertEquals(false, note?.get("isDeleted"))
    }

    @Test
    fun `delete comment`() {
        val noteService = NoteService()
        val noteId = 1
        val ownerId = 1
        noteService.add(ownerId, "Влог", "Мой день")
        noteService.createComment(noteId, ownerId, "Test comment")
        noteService.deleteComment(1)
        assertTrue(noteService.getComments(noteId).isEmpty())
    }

    @Test
    fun `edit note`() {
        val noteService = NoteService()
        noteService.add(ownerId = 1, title = "Влог", text = "Мой день")
        noteService.edit(noteId = 1, title = "Новый заголовок", text = "Новый текст")
        val editedNote = noteService.getById(1)
        assertEquals("Новый заголовок", editedNote?.get("title"))
        assertEquals("Новый текст", editedNote?.get("text"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throw exception editcomment`() {
        val noteService = NoteService()
        val ownerId = 1
        val nonExistentCommentId = 999 // Несуществующий ID комментария
        val newMessage = "New message"
        noteService.editComment(commentId = nonExistentCommentId, ownerId = ownerId, message = newMessage)
    }

}