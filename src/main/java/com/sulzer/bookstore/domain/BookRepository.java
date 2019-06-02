package com.sulzer.bookstore.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "Select * from book b where " +
            "(b.title like %?1% OR CAST(b.id as CHAR) like %?1% OR LOWER(b.author) like %?1%) " +
            "AND b.category=?2",
            nativeQuery = true)
    List<Book> findAllBookByCategoryAndKeyword(String keyword, int category);

    @Query(value = "Select IF(SUM(b.sold) IS NULL,0,SUM(b.sold)) from book b where " +
            "(b.title like %?1% OR CAST(b.id as CHAR) like %?1% OR LOWER(b.author) like %?1%) " +
            "AND b.category=?2 AND b.sold>0",
            nativeQuery = true)
    long countNumberOfBooksSold(String keyword, int category);

}
