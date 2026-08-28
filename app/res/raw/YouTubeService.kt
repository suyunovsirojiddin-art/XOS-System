package com.xos.personalsystem.core.youtube

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface YouTubeAPI {
    @GET("channels")
    suspend fun getChannel(
        @Query("part") part: String = "statistics,snippet",
        @Query("id") channelId: String,
        @Query("key") apiKey: String
    ): ChannelResponse
    
    @GET("search")
    suspend fun getChannelVideos(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("order") order: String = "date",
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): VideoSearchResponse
}

data class ChannelResponse(
    @SerializedName("items") val items: List<ChannelItem>
)

data class ChannelItem(
    @SerializedName("snippet") val snippet: ChannelSnippet,
    @SerializedName("statistics") val statistics: ChannelStatistics
)

data class ChannelSnippet(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("thumbnails") val thumbnails: Thumbnails
)

data class ChannelStatistics(
    @SerializedName("subscriberCount") val subscriberCount: String,
    @SerializedName("videoCount") val videoCount: String,
    @SerializedName("viewCount") val viewCount: String
)

data class Thumbnails(
    @SerializedName("default") val default: Thumbnail,
    @SerializedName("medium") val medium: Thumbnail,
    @SerializedName("high") val high: Thumbnail
)

data class Thumbnail(
    @SerializedName("url") val url: String
)

data class VideoSearchResponse(
    @SerializedName("items") val items: List<VideoItem>
)

data class VideoItem(
    @SerializedName("id") val id: VideoId,
    @SerializedName("snippet") val snippet: VideoSnippet
)

data class VideoId(
    @SerializedName("videoId") val videoId: String
)

data class VideoSnippet(
    @SerializedName("title") val title: String,
    @SerializedName("publishedAt") val publishedAt: String
)

@Singleton
class YouTubeService @Inject constructor() {
    
    companion object {
        private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
        private const val API_KEY = "YOUR_YOUTUBE_API_KEY_HERE" // Replace with actual API key
    }
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val api = retrofit.create(YouTubeAPI::class.java)
    
    suspend fun getChannelInfo(channelId: String): ChannelItem? {
        return try {
            val response = api.getChannel(
                channelId = channelId,
                apiKey = API_KEY
            )
            response.items.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun getChannelVideos(channelId: String): List<VideoItem> {
        return try {
            val response = api.getChannelVideos(
                channelId = channelId,
                apiKey = API_KEY
            )
            response.items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun getYouTubeAchievements(
        channelInfo: ChannelItem?,
        videos: List<VideoItem>
    ): List<YouTubeAchievement> {
        val achievements = mutableListOf<YouTubeAchievement>()
        
        if (channelInfo == null) return achievements
        
        val subscribers = channelInfo.statistics.subscriberCount.toLongOrNull() ?: 0
        val views = channelInfo.statistics.viewCount.toLongOrNull() ?: 0
        val videoCount = channelInfo.statistics.videoCount.toLongOrNull() ?: 0
        
        // Subscriber achievements
        achievements.add(
            YouTubeAchievement(
                title = "SUBSCRIBER MILESTONE",
                description = "Reached $subscribers subscribers",
                requirement = "Grow your channel",
                isUnlocked = subscribers > 0,
                icon = "🎯"
            )
        )
        
        if (subscribers >= 100) {
            achievements.add(
                YouTubeAchievement(
                    title = "100 SUBSCRIBERS",
                    description = "You've reached 100 subscribers!",
                    requirement = "100 subscribers",
                    isUnlocked = true,
                    icon = "🎉"
                )
            )
        }
        
        if (subscribers >= 1000) {
            achievements.add(
                YouTubeAchievement(
                    title = "1000 SUBSCRIBERS",
                    description = "You've reached 1000 subscribers!",
                    requirement = "1000 subscribers",
                    isUnlocked = true,
                    icon = "🏆"
                )
            )
        }
        
        // Video count achievements
        if (videoCount >= 10) {
            achievements.add(
                YouTubeAchievement(
                    title = "10 VIDEOS",
                    description = "Published 10 videos",
                    requirement = "10 videos",
                    isUnlocked = true,
                    icon = "📹"
                )
            )
        }
        
        if (videoCount >= 50) {
            achievements.add(
                YouTubeAchievement(
                    title = "50 VIDEOS",
                    description = "Published 50 videos",
                    requirement = "50 videos",
                    isUnlocked = true,
                    icon = "🎬"
                )
            )
        }
        
        // View achievements
        if (views >= 10000) {
            achievements.add(
                YouTubeAchievement(
                    title = "10K VIEWS",
                    description = "Reached 10,000 views",
                    requirement = "10000 views",
                    isUnlocked = true,
                    icon = "👁️"
                )
            )
        }
        
        if (views >= 100000) {
            achievements.add(
                YouTubeAchievement(
                    title = "100K VIEWS",
                    description = "Reached 100,000 views",
                    requirement = "100000 views",
                    isUnlocked = true,
                    icon = "🌟"
                )
            )
        }
        
        return achievements
    }
}

data class YouTubeAchievement(
    val title: String,
    val description: String,
    val requirement: String,
    val isUnlocked: Boolean,
    val icon: String
)
