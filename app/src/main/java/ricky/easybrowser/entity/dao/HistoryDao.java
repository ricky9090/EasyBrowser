package ricky.easybrowser.entity.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT COUNT(*) FROM history")
    long count();

    @Query("SELECT * FROM history")
    List<History> getAll();

    @Query("SELECT * FROM history LIMIT ((:pageNo - 1) * :pageSize), :pageSize")
    List<History> getHistory(int pageNo, int pageSize);

    @Insert
    Long insertHistory(History history);

}
