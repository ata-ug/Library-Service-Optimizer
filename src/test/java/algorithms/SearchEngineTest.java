package algorithms;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class SearchEngineTest {


    @Test
    public void linearSearch_emptyList_returnsNotFound() {
        List<SearchEngine.ServiceRequest> reqs = new ArrayList<>();
        assertEquals(-1, SearchEngine.linearSearchByRequestId(reqs, 5));
    }

    @Test
    public void linearSearch_singleElement_found() {
        List<SearchEngine.ServiceRequest> reqs = List.of(
                new SearchEngine.ServiceRequest(1, "PENDING"));
        assertEquals(0, SearchEngine.linearSearchByRequestId(reqs, 1));
    }

    @Test
    public void linearSearch_notPresent_returnsMinusOne() {
        List<SearchEngine.ServiceRequest> reqs = List.of(
                new SearchEngine.ServiceRequest(1, "PENDING"),
                new SearchEngine.ServiceRequest(2, "FULFILLED"));
        assertEquals(-1, SearchEngine.linearSearchByRequestId(reqs, 99));
    }

    @Test
    public void linearSearch_firstAndLastElement() {
        List<SearchEngine.ServiceRequest> reqs = List.of(
                new SearchEngine.ServiceRequest(1, "PENDING"),
                new SearchEngine.ServiceRequest(2, "IN_PROGRESS"),
                new SearchEngine.ServiceRequest(3, "FULFILLED"));
        assertEquals(0, SearchEngine.linearSearchByRequestId(reqs, 1));
        assertEquals(2, SearchEngine.linearSearchByRequestId(reqs, 3));
    }

    @Test
    public void linearSearch_duplicateRequestIds_returnsFirstMatch() {
        List<SearchEngine.ServiceRequest> reqs = List.of(
                new SearchEngine.ServiceRequest(7, "PENDING"),
                new SearchEngine.ServiceRequest(7, "FULFILLED"));
        assertEquals(0, SearchEngine.linearSearchByRequestId(reqs, 7));
    }


    @Test
    public void linearSearchByTitle_findsBookInUnsortedList() {
        List<SearchEngine.Book> books = List.of(
                new SearchEngine.Book(1, "444", "Networks"),
                new SearchEngine.Book(2, "111", "Algorithms"));
        assertEquals(1, SearchEngine.linearSearchByTitle(books, "Algorithms"));
    }


    @Test
    public void isSortedByTitle_emptyList_isTriviallySorted() {
        assertTrue(SearchEngine.isSortedByTitle(new ArrayList<>()));
    }

    @Test
    public void isSortedByTitle_singleElement_isSorted() {
        List<SearchEngine.Book> books = List.of(new SearchEngine.Book(1, "111", "Physics"));
        assertTrue(SearchEngine.isSortedByTitle(books));
    }

    @Test
    public void isSortedByTitle_sortedInput_returnsTrue() {
        List<SearchEngine.Book> books = List.of(
                new SearchEngine.Book(1, "111", "Algorithms"),
                new SearchEngine.Book(2, "222", "Databases"),
                new SearchEngine.Book(3, "333", "Zoology"));
        assertTrue(SearchEngine.isSortedByTitle(books));
    }

    @Test
    public void isSortedByTitle_unsortedInput_returnsFalse() {
        List<SearchEngine.Book> books = List.of(
                new SearchEngine.Book(1, "111", "Zoology"),
                new SearchEngine.Book(2, "222", "Algorithms"));
        assertFalse(SearchEngine.isSortedByTitle(books));
    }

    @Test
    public void isSortedByTitle_duplicateTitles_stillCountsAsSorted() {
        List<SearchEngine.Book> books = List.of(
                new SearchEngine.Book(1, "111", "Physics"),
                new SearchEngine.Book(2, "222", "Physics"));
        assertTrue(SearchEngine.isSortedByTitle(books));
    }


    @Test
    public void binarySearch_emptyList_returnsNotFound() {
        List<SearchEngine.Book> books = new ArrayList<>();
        assertEquals(-1, SearchEngine.binarySearchByTitle(books, "Anything"));
    }

    @Test
    public void binarySearch_singleElement_found() {
        List<SearchEngine.Book> books = List.of(
                new SearchEngine.Book(1, "111", "Data Structures"));
        assertEquals(0, SearchEngine.binarySearchByTitle(books, "Data Structures"));
    }

    @Test
    public void binarySearch_firstAndLastElement_sortedInput() {
        List<SearchEngine.Book> books = List.of(
                new SearchEngine.Book(1, "111", "Algorithms"),
                new SearchEngine.Book(2, "222", "Databases"),
                new SearchEngine.Book(3, "333", "Zoology"));
        assertEquals(0, SearchEngine.binarySearchByTitle(books, "Algorithms"));
        assertEquals(2, SearchEngine.binarySearchByTitle(books, "Zoology"));
    }

    @Test
    public void binarySearch_notPresent_returnsMinusOne() {
        List<SearchEngine.Book> books = List.of(
                new SearchEngine.Book(1, "111", "Algorithms"),
                new SearchEngine.Book(2, "222", "Databases"));
        assertEquals(-1, SearchEngine.binarySearchByTitle(books, "Chemistry"));
    }

    @Test
    public void binarySearch_duplicateTitles_returnsAValidIndex() {
        List<SearchEngine.Book> books = List.of(
                new SearchEngine.Book(1, "111", "Physics"),
                new SearchEngine.Book(2, "222", "Physics"),
                new SearchEngine.Book(3, "333", "Physics"));
        int idx = SearchEngine.binarySearchByTitle(books, "Physics");
        assertTrue(idx >= 0 && idx <= 2);
    }

    @Test
    public void binarySearchByIsbn_sortedInput_findsCorrectIndex() {
        List<SearchEngine.Book> books = List.of(
                new SearchEngine.Book(1, "111", "A"),
                new SearchEngine.Book(2, "222", "B"),
                new SearchEngine.Book(3, "333", "C"));
        assertEquals(1, SearchEngine.binarySearchByIsbn(books, "222"));
    }

    @Test(expected = SearchEngine.UnsortedDataException.class)
    public void binarySearchByTitle_unsortedInput_throwsUnsortedDataException() {
        List<SearchEngine.Book> unsorted = List.of(
                new SearchEngine.Book(1, "111", "Zoology"),
                new SearchEngine.Book(2, "222", "Algorithms"));
        SearchEngine.binarySearchByTitle(unsorted, "Algorithms");
    }

    @Test(expected = SearchEngine.UnsortedDataException.class)
    public void binarySearchByIsbn_unsortedInput_throwsUnsortedDataException() {
        List<SearchEngine.Book> unsorted = List.of(
                new SearchEngine.Book(1, "333", "A"),
                new SearchEngine.Book(2, "111", "B"));
        SearchEngine.binarySearchByIsbn(unsorted, "111");
    }

    @Test
    public void counterexample_uncheckedBinarySearch_missesPresentElement_onUnsortedInput() {
        List<SearchEngine.Book> unsorted = List.of(
                new SearchEngine.Book(1, "444", "Networks"),
                new SearchEngine.Book(2, "111", "Zoology"),
                new SearchEngine.Book(3, "333", "Algorithms"),
                new SearchEngine.Book(4, "222", "Chemistry")
        );

        int trueIndex = SearchEngine.linearSearchByTitle(unsorted, "Algorithms");
        assertEquals(2, trueIndex);

        int uncheckedResult = SearchEngine.binarySearchByTitleUnchecked(unsorted, "Algorithms");
        assertNotEquals(trueIndex, uncheckedResult);
    }

    @Test
    public void counterexample_checkedBinarySearch_refusesToRunOnUnsortedInput() {
        List<SearchEngine.Book> unsorted = List.of(
                new SearchEngine.Book(1, "444", "Networks"),
                new SearchEngine.Book(2, "111", "Zoology"),
                new SearchEngine.Book(3, "333", "Algorithms"),
                new SearchEngine.Book(4, "222", "Chemistry")
        );
        assertThrows(SearchEngine.UnsortedDataException.class,
                () -> SearchEngine.binarySearchByTitle(unsorted, "Algorithms"));
    }
}
