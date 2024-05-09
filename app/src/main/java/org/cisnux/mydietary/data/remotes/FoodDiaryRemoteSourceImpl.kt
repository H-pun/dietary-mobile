package org.cisnux.mydietary.data.remotes

import arrow.core.Either
import org.cisnux.mydietary.data.locals.BaseApiUrlLocalSource
import org.cisnux.mydietary.data.remotes.responses.CommonResponse
import org.cisnux.mydietary.data.remotes.bodyrequests.FoodDiaryBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.GetFoodDiaryParams
import org.cisnux.mydietary.data.remotes.responses.FoodDiaryDetailResponse
import org.cisnux.mydietary.data.remotes.responses.FoodDiaryResponse
import org.cisnux.mydietary.data.remotes.responses.DetectedResponse
import org.cisnux.mydietary.data.remotes.responses.ReportResponse
import org.cisnux.mydietary.utils.Failure
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.cisnux.mydietary.data.remotes.bodyrequests.DietProgressBodyRequest
import org.cisnux.mydietary.data.remotes.responses.KeywordResponse
import java.io.File
import javax.inject.Inject

class FoodDiaryRemoteSourceImpl @Inject constructor(
    private val client: HttpClient,
    private val baseApiUrlLocalSource: BaseApiUrlLocalSource
) : FoodDiaryRemoteSource {
    /**
     * Method ini berfungsi untuk mendapatkan data list food diary
     * berdasarkan user id, tanggal, kategori dan query dengan melakukan request ke Dietary API
     * @param accessToken [String] untuk melakukan autorisasi
     * @param getFoodDiaryParams [GetFoodDiaryParams] untuk mengambil nilai parameter user id, tanggal, kategori dan query
     * @return [Either]<[Exception], [List]<[FoodDiaryResponse]>> Method ini akan mengembalikan exception
     * jika request gagal dan jika berhasil akan mengembalikan list food diary
     * berdasarkan nilai query parameter yang dikirimkan
     * */
    override suspend fun getFoodDiaries(
        accessToken: String,
        getFoodDiaryParams: GetFoodDiaryParams
    ): Either<Exception, List<FoodDiaryResponse>> =
        withContext(Dispatchers.IO) {// Untuk menjalankan code pada IO Thread
            // untuk catch error jika bukan berasal dari client request
            try {
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO)
                    .first() // BASE URL untuk melakukan request ke Dietary API
                // memanggil method get untuk melakukan request ke RESTful API dan
                // dan menyimpan response ke variable response
                val response = client.get(
                    urlString =
                    "$baseUrl/food-diary/user?idUser=${getFoodDiaryParams.userId}&date=${getFoodDiaryParams.date}"
                ) {
                    headers {
                        // menambahkan header authorization dengan nilai access token
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }
                // mengecek response status sukses atau tidak
                val isSuccess = response.status.isSuccess()
                // jika tidak sukses maka akan mengembalikan exception
                // dan jika sukses maka akan mengembalikan list food diary
                return@withContext if (!isSuccess) {
                    val commonResponse: CommonResponse<Nothing>? =
                        if (response.status != HttpStatusCode.Unauthorized) response.body()
                        else null
                    val failure = Failure.HTTP_FAILURES[response.status]?.let {
                        Either.Left(it.apply {
                            message = commonResponse?.message
                        })
                    } ?: Either.Left(Exception(commonResponse?.message))
                    failure
                } else {
                    val commonResponse: CommonResponse<List<FoodDiaryResponse>> = response.body()
                    Either.Right(commonResponse.data!!)
                }
                // catch error dari koneksi jaringan
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    /**
     * Method ini berfungsi untuk mendapatkan data lengkap food diary yang telah ditambahkan
     * berdasarkan user id, tanggal, kategori dan query dengan melakukan request ke Dietary API
     * @param accessToken [String] untuk melakukan autorisasi
     * @param id [String] sebagai user id
     * @return [Either]<[Exception], [FoodDiaryDetailResponse]> Method ini akan mengembalikan exception
     * jika request gagal dan jika berhasil akan mengembalikan data lengkap food diary yang ditambahkan
     * */
    override suspend fun getFoodDiaryById(
        accessToken: String, id: String
    ): Either<Exception, FoodDiaryDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO)
                .first() // BASE URL untuk melakukan request ke Dietary API
            // memanggil method get untuk melakukan request ke RESTful API dan
            // dan menyimpan response ke variable response
            val response = client.get(
                urlString = "$baseUrl/food-diary/$id"
            ) {
                headers {
                    // menambahkan header authorization dengan nilai access token
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            // mengecek response status sukses atau tidak
            val isSuccess = response.status.isSuccess()
            // jika tidak sukses maka akan mengembalikan exception
            // dan jika sukses maka akan mengembalikan list food diary
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing>? =
                    if (response.status != HttpStatusCode.Unauthorized) response.body()
                    else null
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse?.message
                    })
                } ?: Either.Left(Exception(commonResponse?.message))
                failure
            } else {
                val commonResponse: CommonResponse<FoodDiaryDetailResponse> = response.body()
                Either.Right(commonResponse.data!!)
            }
            // catch error dari koneksi jaringan
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    /**
     * Method ini berfungsi untuk menambahkan food diary
     * @param accessToken [String] untuk melakukan autorisasi
     * @param foodDiary [FoodDiaryBodyRequest] untuk mendapatkan nilai food diary yang akan ditambahkan
     * @return [Either]<[Exception], [String]> Method ini akan mengembalikan exception
     * jika request gagal dan jika berhasil akan mengembalikan food diary id
     * */
    override suspend fun addFoodDiary(
        accessToken: String, foodDiary: FoodDiaryBodyRequest
    ): Either<Exception, String> =
        withContext(Dispatchers.IO) {// Untuk menjalankan code pada IO Thread
            try { // untuk catch error jika bukan berasal dari client request
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO)
                    .first() // BASE URL untuk melakukan request ke Dietary API
                // memanggil method post untuk melakukan request ke RESTful API dan
                // dan menyimpan response ke variable response
                val response = client.post(
                    urlString = "$baseUrl/food-diary"
                ) {
                    headers {
                        // menambahkan header authorization dengan nilai access token
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    // tentukan body request
                    setBody(
                        MultiPartFormDataContent(
                            parts = formData {
                                append(key = "IdUser", foodDiary.userAccountId, Headers.build {
                                    append(HttpHeaders.ContentType, "text/plain")
                                })
                                append(key = "Title", foodDiary.title, Headers.build {
                                    append(HttpHeaders.ContentType, "text/plain")
                                })
                                append(key = "Category", foodDiary.category, Headers.build {
                                    append(HttpHeaders.ContentType, "text/plain")
                                })
                                append(
                                    key = "AddedAt",
                                    foodDiary.addedAt,
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "text/plain")
                                    })
                                append(
                                    key = "File",
                                    InputProvider(foodDiary.foodPicture.length()) {
                                        foodDiary.foodPicture.inputStream().asInput()
                                    },
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "image/jpg")
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${foodDiary.foodPicture.name}"
                                        )
                                    }
                                )
                                append(key = "Feedback[]", foodDiary.feedback, Headers.build {
                                    append(HttpHeaders.ContentType, "text/plain")
                                })
                                append(key = "IdFoods[]", foodDiary.foodIds, Headers.build {
                                    append(HttpHeaders.ContentType, "text/plain")
                                })
                                append(
                                    key = "TotalCalories",
                                    foodDiary.totalCalories,
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "text/plain")
                                    })
                                append(
                                    key = "TotalCarbohydrate",
                                    foodDiary.totalCarbohydrate,
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "text/plain")
                                    })
                                append(key = "TotalProtein", foodDiary.totalProtein, Headers.build {
                                    append(HttpHeaders.ContentType, "text/plain")
                                })
                                append(key = "TotalFat", foodDiary.totalFat, Headers.build {
                                    append(HttpHeaders.ContentType, "text/plain")
                                })
                            },
                            boundary = "WebAppBoundary"
                        )
                    )
                }
                // mengecek response status sukses atau tidak
                val isSuccess = response.status.isSuccess()
                // jika tidak sukses maka akan mengembalikan exception
                // dan jika sukses maka akan mengembalikan list food diary
                return@withContext if (!isSuccess) {
                    val commonResponse: CommonResponse<Nothing>? =
                        if (response.status != HttpStatusCode.Unauthorized) response.body()
                        else null
                    val failure = Failure.HTTP_FAILURES[response.status]?.let {
                        Either.Left(it.apply {
                            message = commonResponse?.message
                        })
                    } ?: Either.Left(Exception(commonResponse?.message))
                    failure
                } else {
                    val commonResponse: CommonResponse<String> = response.body()
                    Either.Right(commonResponse.data!!)
                }
                // catch error dari koneksi jaringan
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    /**
     * Method ini berfungsi untuk mendeteksi makanan
     * @param accessToken [String] untuk melakukan autorisasi
     * @param foodPicture [File] untuk mendapatkan gambar makanan
     * @return [Either]<[Exception], [DetectedResponse]> Method ini akan mengembalikan exception
     * jika request gagal dan jika request berhasil maka akan mengembalikan data makanan yang terdeteksi.
     * */
    override suspend fun predictFoods(
        accessToken: String,
        foodPicture: File
    ): Either<Exception, DetectedResponse> =
        withContext(Dispatchers.IO) {// Untuk menjalankan code pada IO Thread
            try { // untuk catch error jika bukan berasal dari client request
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO)
                    .first() // BASE URL untuk melakukan request ke Dietary API
                // memanggil method post untuk melakukan request ke RESTful API dan
                // dan menyimpan response ke variable response
                val response = client.post(
                    urlString = "$baseUrl/food/predict"
                ) {
                    headers {
                        // menambahkan header authorization dengan nilai access token
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    // tentukan body request
                    setBody(
                        MultiPartFormDataContent(
                            parts = formData {
                                // tambahkan gambar makanan
                                append(
                                    key = "imgFile",
                                    InputProvider(foodPicture.length()) {
                                        foodPicture.inputStream().asInput()
                                    },
                                    Headers.build {
                                        // tentukan content type
                                        append(HttpHeaders.ContentType, "image/jpg")
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${foodPicture.name}"
                                        )
                                    }
                                )
                            },
                            boundary = "WebAppBoundary"
                        )
                    )
                }
                // mengecek response status sukses atau tidak
                val isSuccess = response.status.isSuccess()
                // jika tidak sukses maka akan mengembalikan exception
                // dan jika sukses maka akan mengembalikan list food diary
                return@withContext if (!isSuccess) {
                    val commonResponse: CommonResponse<Nothing>? =
                        if (response.status != HttpStatusCode.Unauthorized) response.body()
                        else null
                    val failure = Failure.HTTP_FAILURES[response.status]?.let {
                        Either.Left(it.apply {
                            message = commonResponse?.message
                        })
                    } ?: Either.Left(Exception(commonResponse?.message))
                    failure
                } else {
                    val commonResponse: CommonResponse<DetectedResponse> = response.body()
                    Either.Right(commonResponse.data!!)
                }
                // catch error dari koneksi jaringan
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun getFoodDiaryReports(
        accessToken: String, userId: String, category: String
    ): Either<Exception, List<ReportResponse>> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.get(
                urlString = "$baseUrl/food-diary/report?idUser=$userId"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing>? =
                    if (response.status != HttpStatusCode.Unauthorized) response.body()
                    else null
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse?.message
                    })
                } ?: Either.Left(Exception(commonResponse?.message))
                failure
            } else {
                val commonResponse: CommonResponse<List<ReportResponse>> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun getKeywordSuggestions(
        accessToken: String,
        getFoodDiaryParams: GetFoodDiaryParams
    ): Either<Exception, List<KeywordResponse>> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.get(
                urlString = "$baseUrl/food-diary/user?idUser=${getFoodDiaryParams.userId}"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing>? =
                    if (response.status != HttpStatusCode.Unauthorized) response.body()
                    else null
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse?.message
                    })
                } ?: Either.Left(Exception(commonResponse?.message))
                failure
            } else {
                val commonResponse: CommonResponse<List<KeywordResponse>> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun deleteFoodDiaryById(
        accessToken: String,
        id: String
    ): Either<Exception, Nothing?> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.delete(
                urlString = "$baseUrl/food-diary?id=$id",
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing>? =
                    if (response.status != HttpStatusCode.Unauthorized) response.body()
                    else null
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse?.message
                    })
                } ?: Either.Left(Exception(commonResponse?.message))
                failure
            } else
                Either.Right(null)
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }
}