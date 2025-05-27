package com.herald.moviestask.data.remote.dto


import com.google.gson.annotations.SerializedName
import com.herald.moviestask.domain.models.MovieModel
import java.util.Locale

data class MovieDTO(
    @SerializedName("adult")
    val adult: Boolean? = false,
    @SerializedName("backdrop_path")
    val backdropPath: String? = "",
    @SerializedName("belongs_to_collection")
    val belongsToCollection: Any? = Any(),
    @SerializedName("budget")
    val budget: Int? = 0,
    @SerializedName("genres")
    val genres: List<Genre?>? = listOf(),
    @SerializedName("homepage")
    val homepage: String? = "",
    @SerializedName("id")
    val id: Int,
    @SerializedName("imdb_id")
    val imdbId: String? = "",
    @SerializedName("origin_country")
    val originCountry: List<String?>? = listOf(),
    @SerializedName("original_language")
    val originalLanguage: String? = "",
    @SerializedName("original_title")
    val originalTitle: String? = "",
    @SerializedName("overview")
    val overview: String? = "",
    @SerializedName("popularity")
    val popularity: Double? = 0.0,
    @SerializedName("poster_path")
    val posterPath: String? = "",
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany?>? = listOf(),
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry?>? = listOf(),
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("revenue")
    val revenue: Int? = 0,
    @SerializedName("runtime")
    val runtime: Int? = 0,
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage?>? = listOf(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("tagline")
    val tagline: String? = "",
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("video")
    val video: Boolean? = false,
    @SerializedName("videos")
    val videos: Videos? = Videos(),
    @SerializedName("vote_average")
    val voteAverage: Double? = 0.0,
    @SerializedName("vote_count")
    val voteCount: Int? = 0
) {
    data class Genre(
        @SerializedName("id")
        val id: Int? = 0,
        @SerializedName("name")
        val name: String? = ""
    )

    data class ProductionCompany(
        @SerializedName("id")
        val id: Int? = 0,
        @SerializedName("logo_path")
        val logoPath: String? = "",
        @SerializedName("name")
        val name: String? = "",
        @SerializedName("origin_country")
        val originCountry: String? = ""
    )

    data class ProductionCountry(
        @SerializedName("iso_3166_1")
        val iso31661: String? = "",
        @SerializedName("name")
        val name: String? = ""
    )

    data class SpokenLanguage(
        @SerializedName("english_name")
        val englishName: String? = "",
        @SerializedName("iso_639_1")
        val iso6391: String? = "",
        @SerializedName("name")
        val name: String? = ""
    )

    data class Videos(
        @SerializedName("results")
        val results: List<Result?>? = listOf()
    ) {
        data class Result(
            @SerializedName("id")
            val id: String? = "",
            @SerializedName("iso_3166_1")
            val iso31661: String? = "",
            @SerializedName("iso_639_1")
            val iso6391: String? = "",
            @SerializedName("key")
            val key: String? = "",
            @SerializedName("name")
            val name: String? = "",
            @SerializedName("official")
            val official: Boolean? = false,
            @SerializedName("published_at")
            val publishedAt: String? = "",
            @SerializedName("site")
            val site: String? = "",
            @SerializedName("size")
            val size: Int? = 0,
            @SerializedName("type")
            val type: String? = ""
        )
    }
    fun toMovieModel(): MovieModel {
        return MovieModel(
            backdropPath = backdropPath ?: "",
            genres = genres?.map { it?.name ?: "" } ?: emptyList(),
            id = id,
            overview = overview ?: "",
            releaseDate = releaseDate.split("-")[0].ifEmpty { "----" },
            runtime = runtime ?: 0,
            tagline = tagline ?: "",
            title = title ?: "",
            ytTrailer = videos?.results?.find { it?.type == "Trailer" }?.key ?: "",
            voteAverage =  String.format(Locale.ENGLISH,"%.1f",voteAverage)
        )
    }
}