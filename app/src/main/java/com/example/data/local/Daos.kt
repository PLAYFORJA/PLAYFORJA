package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY level DESC, xp DESC")
    fun observeAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUserById(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(handle) = LOWER(:handle) LIMIT 1")
    suspend fun getUserByHandle(handle: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(handle) = LOWER(:identifier) OR LOWER(email) = LOWER(:identifier) LIMIT 1")
    suspend fun findByHandleOrEmail(identifier: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun observeAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: Long): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: Long)
}

@Dao
interface PostReactionDao {
    @Query("SELECT * FROM post_reactions WHERE userId = :userId")
    fun observeReactionsForUser(userId: Long): Flow<List<PostReactionEntity>>

    @Query("SELECT * FROM post_reactions WHERE userId = :userId AND postId = :postId LIMIT 1")
    suspend fun getReaction(userId: Long, postId: Long): PostReactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReaction(reaction: PostReactionEntity)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments ORDER BY createdAt ASC")
    fun observeAllComments(): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Query("UPDATE comments SET likesCount = likesCount + 1 WHERE id = :commentId")
    suspend fun incrementCommentLike(commentId: Long)
}

@Dao
interface FollowDao {
    @Query("SELECT * FROM follows WHERE followerId = :followerId")
    fun observeFollowsByUser(followerId: Long): Flow<List<FollowEntity>>

    @Query("SELECT * FROM follows WHERE followerId = :followerId AND targetUserId = :targetUserId LIMIT 1")
    suspend fun getFollow(followerId: Long, targetUserId: Long): FollowEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFollow(follow: FollowEntity)

    @Query("DELETE FROM follows WHERE followerId = :followerId AND targetUserId = :targetUserId")
    suspend fun deleteFollow(followerId: Long, targetUserId: Long)
}

@Dao
interface ClanDao {
    @Query("SELECT * FROM clans ORDER BY isJoined DESC, membersCount DESC")
    fun observeAllClans(): Flow<List<ClanEntity>>

    @Query("SELECT * FROM clans WHERE id = :clanId LIMIT 1")
    suspend fun getClanById(clanId: Long): ClanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClan(clan: ClanEntity): Long

    @Update
    suspend fun updateClan(clan: ClanEntity)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_threads ORDER BY lastTimestamp DESC")
    fun observeAllThreads(): Flow<List<ChatThreadEntity>>

    @Query("SELECT * FROM chat_threads WHERE id = :threadId LIMIT 1")
    suspend fun getThreadById(threadId: Long): ChatThreadEntity?

    @Query("SELECT * FROM chat_threads WHERE participantUserId = :userId LIMIT 1")
    suspend fun getThreadByParticipantUserId(userId: Long): ChatThreadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThread(thread: ChatThreadEntity): Long

    @Update
    suspend fun updateThread(thread: ChatThreadEntity)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun observeAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId OR userId = 0 ORDER BY createdAt DESC")
    fun observeNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllRead(userId: Long)
}
