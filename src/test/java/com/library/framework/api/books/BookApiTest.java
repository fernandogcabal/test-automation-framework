package com.library.framework.api.books;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.library.framework.api.AuthenticatedApiTest;
import com.library.framework.models.dto.books.BookResponseRecord;
import com.library.framework.models.dto.books.CreateBookRequestRecord;
import io.restassured.response.Response;
import java.util.List;
import org.testng.annotations.Test;

public class BookApiTest extends AuthenticatedApiTest {

    @Test(groups = "api")
    public void shouldCreateBook() {
        String title = "Test book " + System.nanoTime();
        CreateBookRequestRecord payload =
                new CreateBookRequestRecord(
                        title,
                        "Test Author",
                        Long.toString(System.currentTimeMillis()).substring(3),
                        "TO_READ",
                        "PHYSICAL",
                        true,
                        1L);

        Response response = client.post("/api/books", payload);
        assertEquals(
                response.statusCode(),
                200,
                "Book creation failed: " + response.asString());

        Long bookId = response.jsonPath().getLong("id");
        assertNotNull(bookId, "Create response did not contain the new book's ID");
        try {
            assertEquals(response.jsonPath().getString("title"), title);
        } finally {
            deleteBook(bookId);
        }
    }

    @Test(groups = "api")
    public void shouldGetAllBooks() {
        Response booksResponse = client.get("/api/books");
        assertEquals(
                booksResponse.statusCode(),
                200,
                "Fetching books failed: " + booksResponse.asString());

        List<BookResponseRecord> books =
                booksResponse.jsonPath().getList("content", BookResponseRecord.class);
        assertNotNull(books, "Expected the response to contain a 'content' list of books");
    }

    @Test(groups = "api")
    public void shouldDeleteBookById() {
        Response initialBooksResponse = client.get("/api/books");
        assertEquals(
                initialBooksResponse.statusCode(),
                200,
                "Fetching books before deletion failed: " + initialBooksResponse.asString());
        long initialBookCount = initialBooksResponse.jsonPath().getLong("totalElements");

        String uniqueSuffix = Long.toString(System.nanoTime());
        CreateBookRequestRecord payload =
                new CreateBookRequestRecord(
                        "Delete test book " + uniqueSuffix,
                        "Test Author",
                        Long.toString(System.currentTimeMillis()).substring(3),
                        "TO_READ",
                        "PHYSICAL",
                        true,
                        1L);

        Response createResponse = client.post("/api/books", payload);
        assertEquals(
                createResponse.statusCode(),
                200,
                "Creating book for deletion failed: " + createResponse.asString());

        Long bookId = createResponse.jsonPath().getLong("id");
        assertNotNull(bookId, "Create response did not contain the new book's ID");

        boolean deleted = false;
        try {
            Response deleteResponse = client.delete("/api/books/" + bookId);
            assertTrue(
                    deleteResponse.statusCode() >= 200 && deleteResponse.statusCode() < 300,
                    "Deleting book failed with HTTP " + deleteResponse.statusCode()
                            + ": " + deleteResponse.asString());
            deleted = true;

            Response finalBooksResponse = client.get("/api/books");
            assertEquals(
                    finalBooksResponse.statusCode(),
                    200,
                    "Fetching books after deletion failed: " + finalBooksResponse.asString());
            assertEquals(
                    finalBooksResponse.jsonPath().getLong("totalElements"),
                    initialBookCount,
                    "Expected the book count to return to its original value after deletion");
        } finally {
            if (!deleted) {
                deleteBook(bookId);
            }
        }
    }

    @Test(groups = "api")
    public void shouldUpdateBook() {
        String uniqueSuffix = Long.toString(System.nanoTime());
        String isbn = Long.toString(System.currentTimeMillis()).substring(3);
        CreateBookRequestRecord createPayload =
                new CreateBookRequestRecord(
                        "Book update test " + uniqueSuffix,
                        "Test Author",
                        isbn,
                        "TO_READ",
                        "PHYSICAL",
                        true,
                        1L);

        Response createResponse = client.post("/api/books", createPayload);
        assertEquals(
                createResponse.statusCode(),
                200,
                "Creating book for update failed: " + createResponse.asString());

        Long bookId = createResponse.jsonPath().getLong("id");
        assertNotNull(bookId, "Create response did not contain the new book's ID");

        try {
            String updatedTitle = "Updated test book " + uniqueSuffix;
            CreateBookRequestRecord updatePayload =
                    new CreateBookRequestRecord(
                            updatedTitle,
                            createPayload.author(),
                            createPayload.isbn(),
                            createPayload.readingStatus(),
                            createPayload.format(),
                            createPayload.available(),
                            createPayload.userId());

            Response updateResponse = client.put("/api/books/" + bookId, updatePayload);
            assertEquals(
                    updateResponse.statusCode(),
                    200,
                    "Updating book failed: " + updateResponse.asString());
            assertEquals(
                    updateResponse.jsonPath().getString("title"),
                    updatedTitle,
                    "Update response did not contain the new title");
        } finally {
            Response deleteResponse = client.delete("/api/books/" + bookId);
            assertTrue(
                    deleteResponse.statusCode() >= 200 && deleteResponse.statusCode() < 300,
                    "Cleaning up updated book failed with HTTP " + deleteResponse.statusCode()
                            + ": " + deleteResponse.asString());
        }
    }

    private void deleteBook(Long bookId) {
        Response deleteResponse = client.delete("/api/books/" + bookId);
        assertTrue(
                deleteResponse.statusCode() >= 200 && deleteResponse.statusCode() < 300,
                "Cleaning up book " + bookId + " failed with HTTP " + deleteResponse.statusCode()
                        + ": " + deleteResponse.asString());
    }
}
