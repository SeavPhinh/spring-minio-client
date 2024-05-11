package org.kshrd.springminioclient.repository;


import org.apache.ibatis.annotations.*;

@Mapper
public interface FileRepository {

    @Insert("""
            INSERT INTO files
            VALUES (DEFAULT, #{fileName})
            """)
    void saveFile(String fileName);

    @Select("""
            SELECT file_name FROM files
            WHERE file_name = #{fileName}
            """)
    String findFileByName(String fileName);

    @Delete("""
            DELETE FROM files
            WHERE file_name = #{fileName}
            """)
    void deleteFile(String fileName);

    @Update("""
            UPDATE files
            SET file_name = #{fileName}
            WHERE file_name = #{fileName}
            """)
    void updateFile(String fileName);
}
